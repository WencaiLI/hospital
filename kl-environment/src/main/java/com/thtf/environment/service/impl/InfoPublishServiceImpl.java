package com.thtf.environment.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO;
import com.thtf.environment.dto.ItemInfoOfLargeScreenDTO;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.dto.convert.AlarmConvert;
import com.thtf.environment.dto.convert.ItemConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.service.InfoPublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/9/21 17:23
 * @Description:
 */
@Service
@Slf4j
public class InfoPublishServiceImpl implements InfoPublishService {

    private final static String BIG_SCREEN_TYPE_CODE = "XXFBDP_TYPE"; // 信息发布大屏类别在数据库中的字段

    @Resource
    private ItemAPI itemAPI;
    @Resource
    private AlarmAPI alarmAPI;
    @Resource
    private AdminAPI adminAPI;
    @Resource
    PageInfoConvert pageInfoConvert;
    @Resource
    ItemConvert itemConvert;
    @Resource
    AlarmConvert alarmConvert;
    @Resource
    RedisOperationService redisOperationService;



    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/9/23
     * @Param sysCode:
     * @Param areaCode:
     * @Param keyword:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: java.util.List<com.thtf.environment.dto.ItemInfoOfLargeScreenDTO>
     */
    @Override
    public PageInfoVO getLargeScreenInfo(String sysCode, String areaCode, String keyword, Integer pageNumber, Integer pageSize) {

        /* 获取区域编码 */
        List<String> areaCodeList = new ArrayList<>();

        if(null != areaCode){
            String[] areaCodes = areaCode.split(",");
            for (String areaCodeItem : areaCodes) {
                try {
                    areaCodeList.addAll(adminAPI.getAllChildBuildingAreaCodeList(areaCodeItem).getData());
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        }else {
            areaCodeList = null;
        }

        PageInfo<TblItem> itemPageInfo = itemAPI.searchItemBySysCodeAndTypeCodeAndAreaCodeListAndKeywordPage(sysCode, BIG_SCREEN_TYPE_CODE, keyword, areaCodeList, pageNumber, pageSize).getData();
        PageInfoVO pageInfoVO;
        if(itemPageInfo.getList().size()==0){
            return pageInfoConvert.toPageInfoVO(itemPageInfo);
        }
        pageInfoVO = pageInfoConvert.toPageInfoVO(itemPageInfo);
        List<ItemInfoOfLargeScreenDTO> itemInfoOfLargeScreenDTOS = itemConvert.toItemInfoOfLSList(itemPageInfo.getList());
        // 获取设备和参数信息
        List<ItemNestedParameterVO> itemNestedParameterVOS = itemAPI.searchItemNestedParametersBySysCodeAndItemCodeList(sysCode, itemInfoOfLargeScreenDTOS.stream().map(ItemInfoOfLargeScreenDTO::getItemCode).collect(Collectors.toList())).getData();
        for (ItemInfoOfLargeScreenDTO largeScreen : itemInfoOfLargeScreenDTOS) {
            try {
                largeScreen.setAreaName(this.getAreaNameByAreaCode(largeScreen.getAreaCode()));
                for (ItemNestedParameterVO o : itemNestedParameterVOS) {
                    if(o.getCode().equals(largeScreen.getItemCode())){
                        for (TblItemParameter p : o.getParameterList()) {
                            if(p.getParameterType().equals("OnOffStatus")){
                                largeScreen.setRunParameterCode(p.getCode());
                            }
                            if (p.getParameterType().equals("OnlineStatus")){
                                largeScreen.setOnlineParameterCode(p.getCode());
                            }
                            // todo 补全其他参数
                        }
                    }
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }

        }

        pageInfoVO.setList(itemInfoOfLargeScreenDTOS);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/9/23
     * @Param sysCode:
     * @Param keyword:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: java.util.List<com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO>
     */
    @Override
    public PageInfoVO getLargeScreenAlarmInfo(String sysCode, String keyword, Integer pageNumber, Integer pageSize) {
        PageInfo<TblAlarmRecordUnhandle> data = alarmAPI.getAlarmInfoBySysCodeLimitOneByKeywordPage(keyword, sysCode, pageNumber, pageSize).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(data);
        List<AlarmInfoOfLargeScreenDTO> alarmInfoOfLargeScreenDTOS = alarmConvert.toAlarmInfoOfLargeScreenDTOList(data.getList());

        for (AlarmInfoOfLargeScreenDTO largeScreen : alarmInfoOfLargeScreenDTOS) {
            largeScreen.setAreaName(this.getAreaNameByAreaCode(largeScreen.getAreaCode()));
            largeScreen.setStayTime(timeGap(LocalDateTime.now(), largeScreen.getAlarmTime(), ChronoUnit.SECONDS));
            // todo 对接高博医院自身的信息发布系统后再写 largeScreen.setPublishContent();
        }
        pageInfoVO.setList(alarmInfoOfLargeScreenDTOS);
        return pageInfoVO;
    }



    /**
     * @Author: liwencai
     * @Description: 结合redis，根据区域编码查询区域名称
     * @Date: 2022/9/23
     * @Param areaCode:
     * @return: java.lang.String
     */
    public String getAreaNameByAreaCode(String areaCode) {
        String buildAreaName = null;
        try {
            // 在redis中查询缓存
            buildAreaName = redisOperationService.getBuildAreaNameByCode(areaCode);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        if (StringUtils.isBlank(buildAreaName)) {
            // 在数据库中查询
            try {
                String areaNameInDB = adminAPI.searchAreaNameByAreaCode(areaCode).getData();
                if (StringUtils.isNotBlank(areaCode)) {
                    // 数据库中存在，存入缓存中并返回
                    redisOperationService.saveBuildAreaCodeMapToName(areaCode, areaNameInDB);
                    return areaNameInDB;
                } else {
                    return null;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        } else {
            return buildAreaName;
        }
    }

    /**
     * @Author: liwencai
     * @Description: 计算两时间的差值（单位/秒）
     * @Date: 2022/8/31
     * @Param startTime:
     * @Param endTime:
     * @return: java.lang.Long
     */
    public Long timeGap(LocalDateTime startTime,LocalDateTime endTime,ChronoUnit chronoUnit){
        try {
            return Math.abs(endTime.until(startTime, chronoUnit));
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
}
