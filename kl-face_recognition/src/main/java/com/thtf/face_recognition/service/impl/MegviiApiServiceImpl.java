package com.thtf.face_recognition.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;

import com.thtf.common.dto.adminserver.ResultPage;
import com.thtf.common.dto.alarmserver.AppAlarmRecordDTO;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.alarmserver.ListAlarmPageParamDTO;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageParamDTO;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageResultDTO;

import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblVideoItem;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.face_recognition.common.auth.MegviiAuth;
import com.thtf.face_recognition.common.config.IdGeneratorSnowflake;
import com.thtf.face_recognition.common.constant.MegviiConfig;
import com.thtf.face_recognition.common.enums.MegviiAlarmTypeEnum;
import com.thtf.face_recognition.common.enums.MegviiEventLevelEnum;
import com.thtf.face_recognition.common.enums.MegviiEventTypeEnum;
import com.thtf.face_recognition.common.enums.MegviiPersonTypeEnum;
import com.thtf.face_recognition.common.util.HttpUtil;
import com.thtf.face_recognition.common.util.megvii.StringUtil;
import com.thtf.face_recognition.dto.*;

import com.thtf.face_recognition.entity.faceServer.MegviiAlarmData;
import com.thtf.face_recognition.mapper.MegviiAlarmDataMapper;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.service.ManufacturerApiService;
import com.thtf.face_recognition.service.MegviiAlarmDataService;
import com.thtf.face_recognition.vo.*;
import okhttp3.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/11/13 17:57
 * @Description: 制造商：旷世科技 人脸识别API（盘古2.0）接口文档下称“文档”（盘古v1.3.0 OpenAPI）
 */
@Service("Megvii")
public class MegviiApiServiceImpl implements ManufacturerApiService {
    @Resource(name = "megvii")
    IdGeneratorSnowflake idGeneratorSnowflake;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @Autowired
    private MegviiAlarmDataService megviiAlarmDataService;

    @Autowired
    private ItemAPI itemAPI;

    @Autowired
    private AlarmAPI alarmAPI;

    @Autowired
    MegviiConfig megviiConfig;

    @Resource
    private MegviiAlarmDataMapper megviiAlarmDataMapper;

    //private  static final String BASE_API = "";
    /**
     * 事件开始查询的时间
     */
    private static final String EVENT_START_TIME = "2020-01-01 00:00:00";

    /**
     * @Author: liwencai
     * @Description: 对应文档721页
     * @Date: 2022/12/6
     * @Param paramVO:
     * @Return: java.util.List<com.thtf.face_recognition.vo.FaceRecognitionAlarmResultVO>
     */
    @Override
    public MegviiPage<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO) {
        MegviiPage<FaceRecognitionAlarmResultVO> result = new MegviiPage<>();

        List<String> buildingCodeList = null;
        List<String> areaCodeList = null;

        if(StringUtils.isNotBlank(paramVO.getBuildingCodes())){
            buildingCodeList = Arrays.asList(paramVO.getBuildingCodes().split(","));
        }else {
            if(StringUtils.isNotBlank(paramVO.getAreaCodes())){
                areaCodeList = Arrays.asList(paramVO.getAreaCodes().split(","));
            }
        }

        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(paramVO.getSysCode());
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);
        tblItem.setAlarm(1);
        if(StringUtils.isNoneBlank(paramVO.getKeyword())){
            tblItem.setKeyword(paramVO.getKeyword());
            tblItem.setKeyName(paramVO.getKeyword());
            tblItem.setKeyCode(paramVO.getKeyword());
            tblItem.setKeyAreaName(paramVO.getKeyword());
        }
        tblItem.setPageNumber(paramVO.getPageNumber());
        tblItem.setPageSize(paramVO.getPageSize());

        PageInfo<TblItem> pageInfo = itemAPI.queryAllItemsPage(tblItem).getData();

        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());

        List<TblItem> itemList = pageInfo.getList();

        List<String> itemCodeList = itemList.stream().map(TblItem::getCode).distinct().collect(Collectors.toList());
        if(itemCodeList.size() == 0){
            return null;
        }
        // 匹配报警信息
        List<TblAlarmRecordUnhandle> data = alarmAPI.getAlarmInfoByItemCodeListAndCategoryLimitOne(itemCodeList, 0).getData();
        // 第二种 使用库里的表数据 匹配报警信息
        List<String> detailAlarmIdList = data.stream().map(TblAlarmRecordUnhandle::getAlarmDescription).collect(Collectors.toList());
        List<String> newDetailAlarmIdList = detailAlarmIdList.stream().filter(StringUtils::isNumeric).collect(Collectors.toList());
        List<Long> alarmIdList = newDetailAlarmIdList.stream().map(Long::parseLong).collect(Collectors.toList());

        List<MegviiAlarmData> megviiAlarmData = new ArrayList<>();

        // 第一种使用报警表数据
        List<TblAlarmRecordUnhandle> x = alarmAPI.getAlarmInfoByItemCodeListAndCategoryLimitOne(itemCodeList, 0).getData();

        System.out.println(x);
        if(alarmIdList.size()>0){
            megviiAlarmData  = megviiAlarmDataMapper.selectList(new QueryWrapper<MegviiAlarmData>().lambda().in(MegviiAlarmData::getId, alarmIdList));
        }

        List<FaceRecognitionAlarmResultVO> resultVOList = new ArrayList<>();
        for (TblItem item : itemList) {
            FaceRecognitionAlarmResultVO innerResult = new FaceRecognitionAlarmResultVO();
            innerResult.setItemId(item.getId());
            innerResult.setItemCode(item.getCode());
            innerResult.setItemName(item.getName());
            innerResult.setItemDescription(item.getDescription());
            innerResult.setAreaName(item.getAreaName());
            innerResult.setAreaCode(item.getAreaCode());
            innerResult.setBuildingCode(item.getBuildingCode());
            innerResult.setIpAddress("127.0.0.1");

            x.forEach(e->{
                if(e.getItemCode().equals(item.getCode())){
                    innerResult.setAlarmTime(e.getAlarmTime());
                    if(null != e.getAlarmTime()){
                        long duration = LocalDateTimeUtil.between(e.getAlarmTime(), LocalDateTime.now(), ChronoUnit.MILLIS);
                        innerResult.setStayTime(DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND));
                    }
                    innerResult.setAlarmType(MegviiAlarmTypeEnum.getMegviiEventLevelDescByTypeId(e.getAlarmType()));
                    innerResult.setCatchImageUrl("https://img95.699pic.com/photo/40178/3328.jpg_wh860.jpg");
                    // innerResult.setCatchImageTarget(e.getTargetRect());
                    // innerResult.setAlarmLevel("2");
                }
            });
//            megviiAlarmData.forEach(e->{
//                if(e.getItemCode().equals(item.getCode())){
//                    innerResult.setAlarmTime(e.getAlarmTime());
//                    System.out.println(e.getAlarmType());
//                    innerResult.setAlarmType(MegviiAlarmTypeEnum.getMegviiEventLevelDescByTypeId(e.getAlarmType()));
//                    innerResult.setCatchImageUrl(e.getImageUrl());
//                    innerResult.setCatchImageTarget(e.getTargetRect());
//                    // innerResult.setAlarmLevel("2");
//                }
//            });
            if(StringUtils.isNotBlank(item.getViewLongitude())){
                innerResult.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if(StringUtils.isNotBlank(item.getViewLatitude())){
                innerResult.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            resultVOList.add(innerResult);
        }
        result.setList(resultVOList);
        return result;


//        return null;
//        MegviiPage<FaceRecognitionAlarmResultVO> megviiPage = new MegviiPage<FaceRecognitionAlarmResultVO>();
//        List<FaceRecognitionAlarmResultVO> resultVOList = new ArrayList<>();
//
//        ListItemByKeywordPageParamDTO listItemByKeywordPageParamDTO = new ListItemByKeywordPageParamDTO();
//        // 关键词搜索
//        if(StringUtils.isNoneBlank(paramVO.getKeyword())){
//            listItemByKeywordPageParamDTO.setKeywordOfDesc(paramVO.getKeyword());
//            listItemByKeywordPageParamDTO.setKeywordOfCode(paramVO.getKeyword());
//            listItemByKeywordPageParamDTO.setKeywordOfName(paramVO.getKeyword());
//        }
//
//        listItemByKeywordPageParamDTO.setAreaCodes(paramVO.getAreaCodes());
//        listItemByKeywordPageParamDTO.setBuildingCodes(paramVO.getBuildingCodes());
//
//        // 获取设备信息
//        List<ListItemByKeywordPageResultDTO> allItems = itemAPI.listItemByKeywordPage(listItemByKeywordPageParamDTO).getData().getList();
//        // 所有设备编码
//        List<String> allItemCodeList = allItems.stream().map(ListItemByKeywordPageResultDTO::getCode).collect(Collectors.toList());
//
//        MegviiListEventRecordParamDTO paramDTO = new MegviiListEventRecordParamDTO();
//        paramDTO.setPageNum(paramVO.getPageNumber());
//        paramDTO.setPageSize(paramVO.getPageSize());
//        // 设置设备编码
//        paramDTO.setDeviceUuids(allItemCodeList);
//        paramDTO.setStatus(0);
//        paramDTO.setStartTime(this.convertLocalDateTimeToTimeStamp(EVENT_START_TIME));
//        paramDTO.setEndTime(this.convertLocalDateTimeToTimeStamp(LocalDateTime.now()));
//
//        List<MegviiListEventRecordResultDTO> megviiListEventRecordResultDTOS = null;
//        Map<String, Object> map = listEventRecords(paramDTO);
//        megviiListEventRecordResultDTOS = (List<MegviiListEventRecordResultDTO>) map.get("list");
//        megviiPage.setPageNum(Integer.valueOf((String)map.get("pageNum")));
//        megviiPage.setPageSize(Integer.valueOf((String)map.get("pageSize")));
//        megviiPage.setTotal(Integer.valueOf((String)map.get("total")));
//        if (null == megviiListEventRecordResultDTOS){
//            return null;
//        }
//        // List<MegviiListEventRecordResultDTO> megviiListEventRecordResultDTOS = listEventRecords(paramDTO);
//        for (MegviiListEventRecordResultDTO item : megviiListEventRecordResultDTOS) {
//            resultVOList.add(convertToMegviiListEventRecordResultDTO(item,allItems));
//        }
//        megviiPage.setList(resultVOList);
//        return megviiPage;
    }


    /**
     * @Author: liwencai
     * @Description: 对应文档721页
     * @Date: 2022/12/6
     * @Param paramVO:
     * @Return: java.util.List<com.thtf.face_recognition.vo.FaceRecognitionAlarmResultVO>
     */
//    @Override
    public MegviiPage<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarmOld(FaceRecognitionAlarmParamVO paramVO) {
        MegviiPage<FaceRecognitionAlarmResultVO> megviiPage = new MegviiPage<FaceRecognitionAlarmResultVO>();
        List<FaceRecognitionAlarmResultVO> resultVOList = new ArrayList<>();

        ListItemByKeywordPageParamDTO listItemByKeywordPageParamDTO = new ListItemByKeywordPageParamDTO();
        // 关键词搜索
        if(StringUtils.isNoneBlank(paramVO.getKeyword())){
            listItemByKeywordPageParamDTO.setKeywordOfDesc(paramVO.getKeyword());
            listItemByKeywordPageParamDTO.setKeywordOfCode(paramVO.getKeyword());
            listItemByKeywordPageParamDTO.setKeywordOfName(paramVO.getKeyword());
        }

        listItemByKeywordPageParamDTO.setAreaCodes(paramVO.getAreaCodes());
        listItemByKeywordPageParamDTO.setBuildingCodes(paramVO.getBuildingCodes());

        // 获取设备信息
        List<ListItemByKeywordPageResultDTO> allItems = itemAPI.listItemByKeywordPage(listItemByKeywordPageParamDTO).getData().getList();
        // 所有设备编码
        List<String> allItemCodeList = allItems.stream().map(ListItemByKeywordPageResultDTO::getCode).collect(Collectors.toList());

        MegviiListEventRecordParamDTO paramDTO = new MegviiListEventRecordParamDTO();
        paramDTO.setPageNum(paramVO.getPageNumber());
        paramDTO.setPageSize(paramVO.getPageSize());
        // 设置设备编码
        paramDTO.setDeviceUuids(allItemCodeList);
        paramDTO.setStatus(0);
        paramDTO.setStartTime(this.convertLocalDateTimeToTimeStamp(EVENT_START_TIME));
        paramDTO.setEndTime(this.convertLocalDateTimeToTimeStamp(LocalDateTime.now()));

        List<MegviiListEventRecordResultDTO> megviiListEventRecordResultDTOS = null;
        Map<String, Object> map = listEventRecords(paramDTO);
        megviiListEventRecordResultDTOS = (List<MegviiListEventRecordResultDTO>) map.get("list");
        megviiPage.setPageNum(Integer.valueOf((String)map.get("pageNum")));
        megviiPage.setPageSize(Integer.valueOf((String)map.get("pageSize")));
        megviiPage.setTotal(Long.valueOf((String)map.get("total")));
        if (null == megviiListEventRecordResultDTOS){
            return null;
        }
        // List<MegviiListEventRecordResultDTO> megviiListEventRecordResultDTOS = listEventRecords(paramDTO);
        for (MegviiListEventRecordResultDTO item : megviiListEventRecordResultDTOS) {
            resultVOList.add(convertToMegviiListEventRecordResultDTO(item,allItems));
        }
        megviiPage.setList(resultVOList);
        return megviiPage;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备故障信息
     * @Date: 2022/12/7
     * @Param paramVO:
     * @Return: com.thtf.face_recognition.dto.MegviiPage<com.thtf.face_recognition.vo.FaceRecognitionAlarmResultVO>
     */
    @Override
    public MegviiPage<FaceRecognitionFaultResultVO> listFaceRecognitionFault(FaceRecognitionAlarmParamVO paramVO) {
        MegviiPage<FaceRecognitionFaultResultVO> result = new MegviiPage<>();

        List<String> buildingCodeList = null;
        List<String> areaCodeList = null;
        if(StringUtils.isNotBlank(paramVO.getBuildingCodes())){
            buildingCodeList = Arrays.asList(paramVO.getBuildingCodes().split(","));
        }else {
            if(StringUtils.isNotBlank(paramVO.getAreaCodes())){
                areaCodeList = Arrays.asList(paramVO.getAreaCodes().split(","));
            }
        }
        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(paramVO.getSysCode());
        tblItem.setPageSize(paramVO.getPageSize());
        tblItem.setPageNumber(paramVO.getPageNumber());
        if(StringUtils.isNotBlank(paramVO.getKeyword())){
            tblItem.setKeyword(paramVO.getKeyword());
            tblItem.setKeyAreaName(paramVO.getKeyword());
            tblItem.setKeyName(paramVO.getKeyword());
            tblItem.setKeyCode(paramVO.getKeyword());
        }
        tblItem.setAlarm(0); // 有报警显示，报警优先
        tblItem.setFault(1);
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);
        PageInfo<TblItem> pageInfo = itemAPI.queryAllItemsPage(tblItem).getData();
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());

        // 设备集
        List<TblItem> itemList = pageInfo.getList();
        // 设备编码集
        List<String> itemCodeList = pageInfo.getList().stream().map(TblItem::getCode).distinct().collect(Collectors.toList());
        if(itemCodeList.size() == 0){
            return null;
        }
        List<TblAlarmRecordUnhandle> recordList = alarmAPI.getAlarmInfoByItemCodeListAndCategoryLimitOne(itemCodeList, 1).getData();

        List<FaceRecognitionFaultResultVO> resultList = new ArrayList<>();
        for (TblItem item : itemList) {
            FaceRecognitionFaultResultVO innerResult = new FaceRecognitionFaultResultVO();
            innerResult.setAreaCode(item.getAreaCode());
            innerResult.setAreaName(item.getAreaName());
            innerResult.setItemName(item.getName());
            innerResult.setItemCode(item.getCode());
            innerResult.setIpAddress("127.0.0.1");
            // 匹配设备模型视角信息
            if(StringUtils.isNotBlank(item.getViewLongitude())){
                innerResult.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if(StringUtils.isNotBlank(item.getViewLatitude())){
                innerResult.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            recordList.forEach(record->{
                if(record.getItemCode().equals(item.getCode())){
                    innerResult.setAlarmLevel(record.getAlarmLevel());
                    innerResult.setAlarmTime(record.getAlarmTime());
                    long duration = LocalDateTimeUtil.between(record.getAlarmTime(), LocalDateTime.now(), ChronoUnit.MILLIS);
                    innerResult.setStayTime(DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND));
                }
            });
            resultList.add(innerResult);
        }

        result.setList(resultList);
        return result;
    }

    public MegviiPage<FaceRecognitionFaultResultVO> listFaceRecognitionFaultOld(FaceRecognitionAlarmParamVO paramVO) {
        MegviiPage<FaceRecognitionFaultResultVO> result = new MegviiPage<>();

        ListAlarmInfoLimitOneParamDTO listAlarmInfoLimitOneParamDTO = new ListAlarmInfoLimitOneParamDTO();
        listAlarmInfoLimitOneParamDTO.setSystemCode(paramVO.getSysCode());
        // 故障
        listAlarmInfoLimitOneParamDTO.setAlarmCategory("1");
        listAlarmInfoLimitOneParamDTO.setPageSize(paramVO.getPageSize());
        listAlarmInfoLimitOneParamDTO.setPageNumber(paramVO.getPageNumber());
        PageInfo<TblAlarmRecordUnhandle> pageInfo = alarmAPI.listAlarmInfoLimitOnePage(listAlarmInfoLimitOneParamDTO).getData();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        List<FaceRecognitionFaultResultVO> resultList = new ArrayList<>();
        //
        List<String> itemCodeList = pageInfo.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
        TblItem tblItem = new TblItem();
//        tblItem.setSystemCode(paramVO.getSysCode());
//        tblItem.setBuildingCodeList(Arrays.asList(paramVO.getBuildingCodes().split(",")));
        tblItem.setCodeList(itemCodeList);
//        tblItem.setAreaCodeList(Arrays.asList(paramVO.getAreaCodes().split(",")));
        List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();

        for (TblAlarmRecordUnhandle alarm :  pageInfo.getList()) {
            FaceRecognitionFaultResultVO faceRecognitionFaultResultVO = new FaceRecognitionFaultResultVO();
            faceRecognitionFaultResultVO.setItemCode(alarm.getItemCode());
            faceRecognitionFaultResultVO.setItemName(alarm.getItemName());
            faceRecognitionFaultResultVO.setAreaCode(alarm.getBuildingAreaCode());
            faceRecognitionFaultResultVO.setAreaName(alarm.getBuildingAreaName());
            faceRecognitionFaultResultVO.setAlarmLevel(alarm.getAlarmLevel());
            long duration = LocalDateTimeUtil.between(alarm.getAlarmTime(), LocalDateTime.now(), ChronoUnit.MILLIS);
            faceRecognitionFaultResultVO.setStayTime(DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND));
            // faceRecognitionFaultResultVO.setStayTime(getTimeGap(alarm.getAlarmTime(),LocalDateTime.now()));
            List<TblVideoItem> data = itemAPI.getVideoItemListByItemCode(faceRecognitionFaultResultVO.getItemCode()).getBody().getData();
            faceRecognitionFaultResultVO.setIpAddress(data.get(0).getIp());
            resultList.add(faceRecognitionFaultResultVO);
            // 配置模型信息
            itemList.forEach(e->{
                if(e.getCode().equals(alarm.getItemCode())){
                    if(StringUtils.isNotBlank(e.getViewLongitude())){
                        faceRecognitionFaultResultVO.setEye(Arrays.stream(e.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                    if(StringUtils.isNotBlank(e.getViewLatitude())){
                        faceRecognitionFaultResultVO.setCenter(Arrays.stream(e.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                }
            });
        }
        result.setList(resultList);
        return result;
    }

    @Override
    public MegviiPage<MegviiItemEventDTO> listItemEventByItemCode(String itemCode, Integer pageNumber, Integer pageSize) {
        MegviiPage<MegviiItemEventDTO> result = new MegviiPage<>();
        List<MegviiItemEventDTO> resultList = new ArrayList<>();
        ListAlarmPageParamDTO listAlarmPageParamDTO = new ListAlarmPageParamDTO();
        listAlarmPageParamDTO.setItemCodeList(Collections.singletonList(itemCode));
        listAlarmPageParamDTO.setPageSize(pageSize);
        listAlarmPageParamDTO.setPageNumber(pageNumber);
        ResultPage<AppAlarmRecordDTO> data = alarmAPI.listAlarmUnhandled(listAlarmPageParamDTO).getData();

        result.setTotal(data.getTotal());
        result.setPageSize(pageSize);
        result.setPageNum(pageNumber);

        List<AppAlarmRecordDTO> list = data.getList();
        List<String> alarmCodeList = list.stream().map(AppAlarmRecordDTO::getItemCode).distinct().collect(Collectors.toList());
        List<MegviiAlarmData> megviiAlarmDataList = megviiAlarmDataMapper.selectList(new QueryWrapper<MegviiAlarmData>().lambda().in(MegviiAlarmData::getItemCode, alarmCodeList));

        // todo 临时测试
        data.getList().forEach(e->{
            MegviiItemEventDTO innerResult = new MegviiItemEventDTO();
            innerResult.setEventTime(e.getAlarmTime());
            innerResult.setEventName("抽烟检测");
            innerResult.setEventArea(e.getBuildingAreaName());
            innerResult.setEventType("抽烟检测");
            innerResult.setPersonImageUri("https://cn.bing.com/images/search?view=detailV2&ccid=isSbzM2F&id=BFD57DAD8BC76037E4EB26A7F794DE185D483CAC&thid=OIP.isSbzM2F9zSH9BEOocDK6wHaE8&mediaurl=https%3a%2f%2fimg.zcool.cn%2fcommunity%2f0174d05a003921a801202b0ce5d968.jpg%403000w_1l_0o_100sh.jpg&exph=2000&expw=3000&q=%e6%8a%bd%e7%83%9f%e5%9b%be%e7%89%87&simid=608036338657023855&FORM=IRPRST&ck=46DDA6838EDC1FF2A4A11F9565B260AE&selectedIndex=1");
            innerResult.setCaptureImageUrl("https://cn.bing.com/images/search?view=detailV2&ccid=Idi4oDUx&id=3D448ACC293537F1EAB594508F054B24C158E392&thid=OIP.Idi4oDUxgQvVnkvMZUoXawHaFj&mediaurl=https%3a%2f%2fimg95.699pic.com%2fphoto%2f50007%2f4628.jpg_wh860.jpg&exph=620&expw=827&q=%e6%8a%bd%e7%83%9f%e5%9b%be%e7%89%87&simid=608000857919004612&FORM=IRPRST&ck=F1D8B44F7CC479845454E819FEB1C1E9&selectedIndex=15");
            innerResult.setIdentifyNum("411523199920011004123X");
            innerResult.setPersonName("张非");
            resultList.add(innerResult);
        });


        // todo 正式环境
//        for (MegviiAlarmData megviiAlarmData : megviiAlarmDataList) {
//
//            MegviiItemEventDTO innerResult = new MegviiItemEventDTO();
//            innerResult.setEventName(MegviiAlarmTypeEnum.getMegviiEventLevelDescByTypeId(megviiAlarmData.getAlarmType()));
//            if(StringUtils.isNoneBlank(megviiAlarmData.getPersonInfo())){
//                String jsonString = megviiAlarmData.getPersonInfo();
//                // todo 测试使用
//                List<MegviiPersonInfo> megviiPersonInfo = JSON.parseArray(jsonString, MegviiPersonInfo.class);
//                if(megviiPersonInfo.size() > 0) {
//                    String uuid = megviiPersonInfo.get(0).getUuid();
//                    // todo 测试使用
//                    String bodyImageUrl = megviiPersonInfo.get(0).getBodyImageUrl();
//                    innerResult.setPersonImageUri(bodyImageUrl);
//
//                    if (StringUtils.isNoneBlank(uuid)) {
//                        MegviiUserInfoDTO userInfo = getUserInfoByUUId(uuid);
//                        System.out.println(userInfo);
//                        if(null != userInfo){
//                            innerResult.setIdentifyNum(userInfo.getIdentifyNum());
//                            // todo 正式环境
//                            innerResult.setPersonImageUri(userInfo.getImageUri());
//                            if (userInfo.getVisitedName() != null) {
//                                innerResult.setPersonName(userInfo.getName());
//                            }
//                            if (userInfo.getName() != null) {
//                                innerResult.setPersonName(userInfo.getName());
//                            }
//                            innerResult.setPersonType(MegviiPersonTypeEnum.getMegviiPersonTypeEnumById(userInfo.getType()));
//                            innerResult.setPhone(userInfo.getPhone());
//                        }else {
//                            innerResult.setIdentifyNum("411523199920011004123X");
//                            innerResult.setPersonName("张非");
//                        }
//                    }
//                }
//            }
//            String imageUrl = megviiAlarmData.getImageUrl();
//            innerResult.setCaptureImageUrl(imageUrl);
//            innerResult.setCaptureImageRect(megviiAlarmData.getTargetRect());
//            innerResult.setEventTime(megviiAlarmData.getAlarmTime());
//            resultList.add(innerResult);
//        }
        result.setList(resultList);
        return result;
    }


    /**
     *
     */
    // @Override
    public MegviiPage<MegviiItemEventDTO> listItemEventByItemCodeOld(String itemCode, Integer pageNumber, Integer pageSize) {
        MegviiPage<MegviiItemEventDTO> result = new MegviiPage<>();
        List<MegviiItemEventDTO> resultList = new ArrayList<>();
        MegviiListEventRecordParamDTO megviiListEventRecordParamDTO = new MegviiListEventRecordParamDTO();
        megviiListEventRecordParamDTO.setDeviceUuids(Collections.singletonList(itemCode));
        Map<String, Object> map = this.listEventRecords(megviiListEventRecordParamDTO);
        if(map == null){
            return null;
        }else {
            // 数据结果
            List<MegviiListEventRecordResultDTO> megviiListEventRecordResults = (List<MegviiListEventRecordResultDTO>)map.get("list");
            result.setPageSize((Integer) map.get("pageSize"));
            result.setPageNum((Integer) map.get("pageNum"));
            result.setTotal((Long) map.get("total"));
            for (MegviiListEventRecordResultDTO recordResult : megviiListEventRecordResults) {
                //
                String ext = recordResult.getExt();
                JSONObject jsonObject = JSONObject.parseObject(ext);
                String personInfo = jsonObject.getString("personInfo");
                String plateNumber = jsonObject.getString("plateNumber");
                String personName = jsonObject.getString("personName");
                if(null != plateNumber && null != personName){
                    MegviiItemEventDTO innerResult = new MegviiItemEventDTO();
                    innerResult.setPersonName(personName);
                    /* 车辆相关的事件 */

                }else if(null == plateNumber && null != personName){
                    //通行记录
                    MegviiItemEventDTO innerResult = new MegviiItemEventDTO();
                    innerResult.setPersonName(personName);
                } else if (null != personInfo){
                    /* 智能分析相关事件 */
                    // 获取事件中的用户信息
                    MegviiUserInfoDTO userInfo = getUserInfoByUUId(recordResult.getUuid());
                    MegviiItemEventDTO innerResult = new MegviiItemEventDTO();
                    innerResult.setIdentifyNum(userInfo.getIdentifyNum());
                    innerResult.setPersonImageUri(userInfo.getImageUri());
                    if(userInfo.getVisitedName() != null){
                        innerResult.setPersonName(userInfo.getName());
                    }
                    if(userInfo.getName() != null){
                        innerResult.setPersonName(userInfo.getName());
                    }
                    innerResult.setPersonType(MegviiPersonTypeEnum.getMegviiPersonTypeEnumById(userInfo.getType()));
                    innerResult.setPhone(userInfo.getPhone());
                    innerResult.setCaptureImageUrl(recordResult.getCaptureImageUrl());
                    innerResult.setEventType(MegviiEventTypeEnum.getMegviiEventTypeDescByTypeId(recordResult.getEventTypeId()));
                    // innerResult.setEventArea();
                    innerResult.setEventName(recordResult.getEventTypeName());
                    // 设备区域 innerResult.setEventArea();
                    innerResult.setEventTime(covertTimeStampToLocalDateTime(recordResult.getDealTime()));
                    resultList.add(innerResult);
                }else {
                    continue;
                }

            }

//            megviiListEventRecordResults.forEach(e->{
//
//            });
        }
        result.setList(resultList);
        return result;
    }

    /**
     * 将MegviiListEvent转换为EventRecordResultDTO
     */
    public FaceRecognitionAlarmResultVO convertToMegviiListEventRecordResultDTO (MegviiListEventRecordResultDTO param,
                                                                                 List<ListItemByKeywordPageResultDTO> allItems){
        FaceRecognitionAlarmResultVO result = null;

        for (ListItemByKeywordPageResultDTO item : allItems) {
            if(item.getName().equals(param.getDeviceName())){
                result = new FaceRecognitionAlarmResultVO();
                result.setItemId(item.getId());
                result.setItemCode(item.getCode());
                result.setItemName(item.getName());
                result.setItemDescription(item.getDescription());
                result.setAreaName(item.getAreaName());
                result.setIpAddress("");
                // 配置模型视角
                if(StringUtils.isNotBlank(item.getViewLongitude())){
                    result.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                }
                if(StringUtils.isNotBlank(item.getViewLatitude())){
                    result.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                }
                LocalDateTime alarmDateTime = this.covertTimeStampToLocalDateTime(param.getTimestamp());
                result.setAlarmTime(alarmDateTime);
                result.setAlarmType(MegviiEventLevelEnum.getMegviiEventLevelDescByTypeId(param.getEventLevelId()));
                long duration = LocalDateTimeUtil.between(alarmDateTime, LocalDateTime.now(), ChronoUnit.MILLIS);
                result.setStayTime(DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND));
                //result.setStayTime(getTimeGap(alarmDateTime,LocalDateTime.now()));
            }
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/6
     * @Param paramDTO:
     * @Return: java.util.List<com.thtf.face_recognition.dto.MegviiListEventRecordResultDTO>
     */
    public Map<String, Object> listEventRecords(MegviiListEventRecordParamDTO paramDTO){
        String uri = " /v1/api/event/record/list";
        String jsonParam = JSON.toJSONString(paramDTO);
        Map<String, Object> headers = MegviiAuth.getAuthHeaders(uri, "POST", null, paramDTO);
        try {
            String jsonResult = HttpUtil.httpPostJSON(megviiConfig.getBaseUrl() + uri, headers, jsonParam);
            Map<String, Object> map = convertToJsonList(jsonResult);
            if(null == map.get("list")){
                return null;
            }
            map.put("list",JSONArray.parseArray(String.valueOf(map.get("list")), MegviiListEventRecordResultDTO.class));
            return map;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * @Author: liwencai
     * @Description: 获取人员信息
     * @Date: 2023/2/21
     * @Param uuid:
     * @Return: com.thtf.face_recognition.vo.MegviiUserInfoDTO
     */
    public MegviiUserInfoDTO getUserInfoByUUId(String  uuid){
        if(StringUtil.isEmpty(uuid))
        {
            return null;
        }
        String uri = "/v1/api/person/query";
        Map<String, String> paramMap =  new HashMap<>();
        paramMap.put("uuid",uuid);
        String jsonParam = JSON.toJSONString(paramMap);
        Map<String, Object> headers = MegviiAuth.getAuthHeaders(uri, "POST", null, paramMap);
        try {
            String jsonResult = HttpUtil.httpPostJSON(megviiConfig.getBaseUrl() + uri, headers, jsonParam);
            return JSON.parseObject(jsonResult, MegviiUserInfoDTO.class);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * @Author: liwencai
     * @Description: 查询设备
     * @Date: 2022/12/29
     * @Param paramDTO:
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     */
    public MegviiPage<MegviiDeviceDTO> listMegviiDeviceDTO(MegviiListDeviceParamDTO paramDTO){

        MegviiPage<MegviiDeviceDTO> result = new MegviiPage<>();
        String uri = "/v1/api/device/list";
        String jsonParam = JSON.toJSONString(paramDTO);
        Map<String, Object> headers = MegviiAuth.getAuthHeaders(uri, "POST", null, paramDTO);
        try {
            String jsonResult = HttpUtil.httpPostJSON(megviiConfig.getBaseUrl() + uri, headers, jsonParam);
            Map<String, Object> map = convertToJsonList(jsonResult);
            result.setList(JSONArray.parseArray(String.valueOf(map.get("list")), MegviiDeviceDTO.class));
            result.setTotal(JSON.parseObject(String.valueOf(map.get("total")),Long.class));
            result.setPageNum(JSON.parseObject(String.valueOf(map.get("pageNum")),Integer.class));
            result.setPageSize(JSON.parseObject(String.valueOf(map.get("pageSize")),Integer.class));
            return result;
        }catch (Exception e){
            return null;
        }
    }



    /**
     * @Author: liwencai
     * @Description: 向数据库写入智能分析事件（测试环境）
     * @Date: 2023/1/7
     * @Return: com.thtf.face_recognition.dto.MegviiPage<com.thtf.face_recognition.dto.MegviiPushDataIntelligentDTO>
     */
    @Transactional
    public MegviiPage<MegviiPushDataIntelligentDTO> listPushIntelligentData() throws Exception {
        // String uri = "/v1/api/device/list";
        // String jsonResult = HttpUtil.httpPostJson(megviiConfig.getBaseUrl() + uri, null);
        Random random = new Random();
        int max= 0 ;
        int min = 5;
        int alarmType = random.nextInt(min+max)+min;
        // int  min_1 = 1671140;
        // int max_1= (int) (System.currentTimeMillis()/1000000);
        // String alarmTime = (random.nextInt(max_1 - min_1) + min_1) +"000000";
        String alarmTime = String.valueOf(System.currentTimeMillis());
        String jsonResult =  "{\"alarmControlType\":1,\n" +
                "\"alarmEndTime\":"+alarmTime+",\n" +
                "\"alarmRecordUuId\":\"5ce33c9accb94a978976b232958bdc88\",\n" +
                "\"alarmTime\":"+alarmTime+",\n" +
                "\"alarmType\":"+alarmType+",\n" +
                "\"areaId\":0,\n" +
                "\"continueTime\":\"3.0\",\n" +
                "\"deviceName\":\"警ᡂ㇇⌅仓-车辆虚拟\",\n" +
                "\"deviceUuid\":\"RLSB_TYPE_"+alarmType+"\",\n" +
                "\"targetRect\":[{\"bottom\":82.376396,\"left\":46.54868,\"right\":51.40949,\"top\":12}],\n" +
                "\"wholeImageUrl\":\"https://ts1.cn.mm.bing.net/th/id/R-C.d012fa027bb8ceffe790b2ea785ba69e?rik=34mJMkl6ADGSyw&riu=http%3a%2f%2f9.pic.paopaoche.net%2fthumb%2fup%2f2017-5%2f201705230909503371641_600_0.jpg&ehk=w%2b948Sa8mw8kU7HReJnWr9mni6KU1u1enn%2fZ8T7AV78%3d&risl=&pid=ImgRaw&r=0\",\n" +
                "\"arithmeticPackageType\":1,\n" +
                "\"color\":1,\n" +
                "\"fireEquipmentNumber\":3,\n" +
                "\"gasCylinderFunction\":2,\n" +
                "\"indicatorStatusColor\":\"㓒灯亮\",\n" +
                "\"personInfo\":[{\"uuid\":\"xssddss\",\"name\":\"李文彩\",\"bodyImageUrl\":\"https://img.51miz.com/Photo/2017/06/05/15/P894207_edcab57f8cc99b89c686ca312df9ce05.jpg\"}]\n" +
                "}";

        MegviiPushDataIntelligentDTO megviiPushDataIntelligentDTO = jsonToMegviiPushDataIntelligentDTO(jsonResult);
        MegviiApiServiceImpl megviiApiService = new MegviiApiServiceImpl();
        MegviiAlarmData megviiAlarmData = new MegviiAlarmData();

        String deviceUuid = megviiPushDataIntelligentDTO.getDeviceUuid();
        // 生成id
        Long id = idGeneratorSnowflake.getId();
        megviiAlarmData.setId(id);
        megviiAlarmData.setAlarmType(megviiPushDataIntelligentDTO.getAlarmType());
        // 时间戳 转 时间
        megviiAlarmData.setAlarmTime(megviiApiService.toLocalDateTimeMilliseconds(megviiPushDataIntelligentDTO.getAlarmTime()));
        if(StringUtils.isNotBlank(megviiPushDataIntelligentDTO.getWholeImageUrl())){
            megviiAlarmData.setImageUrl(megviiPushDataIntelligentDTO.getWholeImageUrl());
            megviiAlarmData.setImageType(1);
            megviiAlarmData.setTargetRect(megviiPushDataIntelligentDTO.getTargetRect());
            String jsonString = JSON.toJSONString(megviiPushDataIntelligentDTO.getPersonInfo());
            megviiAlarmData.setPersonInfo(jsonString);
            megviiAlarmData.setItemCode(deviceUuid);
        }
        megviiAlarmDataMapper.insert(megviiAlarmData);
        //

        TblItem item = itemAPI.searchItemByItemCode(deviceUuid).getData();
        if(null == item){
            return null;
        }
        TblAlarmRecordUnhandle tblAlarmRecordUnhandle = new TblAlarmRecordUnhandle();
        tblAlarmRecordUnhandle.setAlarmTime(megviiAlarmData.getAlarmTime());
        tblAlarmRecordUnhandle.setItemId(String.valueOf(item.getId()));
        tblAlarmRecordUnhandle.setItemCode(item.getCode());
        tblAlarmRecordUnhandle.setItemTypeCode(item.getTypeCode());
        tblAlarmRecordUnhandle.setParameterCode("旷世数据推送");
        tblAlarmRecordUnhandle.setSystemCode("sub_face_recognition");
        tblAlarmRecordUnhandle.setSystemName("人脸识别系统");
        tblAlarmRecordUnhandle.setBuildingAreaCode(item.getAreaCode());
        tblAlarmRecordUnhandle.setBuildingAreaName(item.getAreaName());
        tblAlarmRecordUnhandle.setBuildingArea(item.getBuildingCode());
        tblAlarmRecordUnhandle.setAlarmDescription(String.valueOf(id));
        tblAlarmRecordUnhandle.setAlarmLevel(2);
        tblAlarmRecordUnhandle.setAlarmType(4);
        tblAlarmRecordUnhandle.setAlarmCategory(0);
        // tblAlarmRecordUnhandle.setAlarmPlanId();
        alarmAPI.insertAlarmUnhandled(tblAlarmRecordUnhandle);
        itemAPI.updateAlarmOrFaultStatus(item.getCode(),1,null);
        return null;
    }


    /**
     * @Author: liwencai
     * @Description: 向数据库写入智能分析事件(生产环境)
     * @Date: 2023/1/7
     * @Return: com.thtf.face_recognition.dto.MegviiPage<com.thtf.face_recognition.dto.MegviiPushDataIntelligentDTO>
     */
    @Transactional
    public MegviiPage<MegviiPushDataIntelligentDTO> listPushIntelligentDataProduce() throws Exception {


        String uri = "/v1/api/device/list";
        Map<String, Object> headers = MegviiAuth.getAuthHeaders(uri, "POST", null, null);
        String jsonResult = HttpUtil.httpPostJSON(megviiConfig.getBaseUrl() + uri, headers, null);


        MegviiPushDataIntelligentDTO megviiPushDataIntelligentDTO = jsonToMegviiPushDataIntelligentDTO(jsonResult);
        MegviiApiServiceImpl megviiApiService = new MegviiApiServiceImpl();
        MegviiAlarmData megviiAlarmData = new MegviiAlarmData();
        String deviceUuid = megviiPushDataIntelligentDTO.getDeviceUuid();
        // 生成id
        Long id = idGeneratorSnowflake.getId();
        megviiAlarmData.setId(id);
        megviiAlarmData.setAlarmType(megviiPushDataIntelligentDTO.getAlarmType());
        // 时间戳 转 时间
        megviiAlarmData.setAlarmTime(megviiApiService.toLocalDateTimeMilliseconds(megviiPushDataIntelligentDTO.getAlarmTime()));
        if(StringUtils.isNotBlank(megviiPushDataIntelligentDTO.getWholeImageUrl())){
            megviiAlarmData.setImageUrl(megviiPushDataIntelligentDTO.getWholeImageUrl());
            megviiAlarmData.setImageType(1);
            megviiAlarmData.setTargetRect(megviiPushDataIntelligentDTO.getTargetRect());
            String jsonString = JSON.toJSONString(megviiPushDataIntelligentDTO.getPersonInfo());
            megviiAlarmData.setPersonInfo(jsonString);
            megviiAlarmData.setItemCode(deviceUuid);
        }
        megviiAlarmDataMapper.insert(megviiAlarmData);
        // 查询设备信息
        TblItem item = itemAPI.searchItemByItemCode(deviceUuid).getData();
        if(null == item){
            return null;
        }
        TblAlarmRecordUnhandle tblAlarmRecordUnhandle = new TblAlarmRecordUnhandle();
        tblAlarmRecordUnhandle.setAlarmTime(megviiAlarmData.getAlarmTime());
        tblAlarmRecordUnhandle.setItemId(String.valueOf(item.getId()));
        tblAlarmRecordUnhandle.setItemCode(item.getCode());
        tblAlarmRecordUnhandle.setItemTypeCode(item.getTypeCode());
        tblAlarmRecordUnhandle.setParameterCode("旷世数据推送");
        tblAlarmRecordUnhandle.setSystemCode("sub_face_recognition");
        tblAlarmRecordUnhandle.setSystemName("人脸识别系统");
        tblAlarmRecordUnhandle.setBuildingAreaCode(item.getAreaCode());
        tblAlarmRecordUnhandle.setBuildingAreaName(item.getAreaName());
        tblAlarmRecordUnhandle.setBuildingArea(item.getBuildingCode());
        tblAlarmRecordUnhandle.setAlarmDescription(String.valueOf(id));
        tblAlarmRecordUnhandle.setAlarmLevel(2);
        tblAlarmRecordUnhandle.setAlarmType(4);
        tblAlarmRecordUnhandle.setAlarmCategory(0);
        // tblAlarmRecordUnhandle.setAlarmPlanId();
        // 向ibs5报警表中插入报警数据
        alarmAPI.insertAlarmUnhandled(tblAlarmRecordUnhandle);
        // 修改设备为报警状态
        itemAPI.updateAlarmOrFaultStatus(item.getCode(),1,null);
        return null;
    }


    /**
     * @Author: liwencai
     * @Description: 将人脸识别推送数据转换为对象
     * @Date: 2023/2/21
     * @Param jsonString:
     * @Return: com.thtf.face_recognition.dto.MegviiPushDataIntelligentDTO
     */
    public MegviiPushDataIntelligentDTO jsonToMegviiPushDataIntelligentDTO(String jsonString){
        return JSON.parseObject(jsonString, MegviiPushDataIntelligentDTO.class);
    }

    /**
     * @Author: liwencai
     * @Description: 转换为LocalDateTime
     * @Date: 2023/1/7
     * @Param timestamp: 毫秒时间戳
     * @Return: java.time.LocalDateTime
     */
    public LocalDateTime toLocalDateTimeMilliseconds(Long timestamp){
        if(null == timestamp){
            return null;
        }
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * @Author: liwencai
     * @Description: 转换为时间戳
     * @Date: 2022/12/6
     * @Param localDateTime:
     * @Return: java.lang.Long
     */
    public Long convertLocalDateTimeToTimeStamp(String localDateTimeString){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(localDateTimeString, df);
        return this.convertLocalDateTimeToTimeStamp(parse);
    }


    /**
     * @Author: liwencai
     * @Description: 转换为时间戳
     * @Date: 2022/12/6
     * @Param localDateTime:
     * @Return: java.lang.Long
     */
    public Long convertLocalDateTimeToTimeStamp(LocalDateTime localDateTime){
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * @Author: liwencai
     * @Description: 时间戳转LocalDateTime
     * @Date: 2022/12/7
     * @Param TimeStamp:
     * @Return: java.time.LocalDateTime
     */
    public LocalDateTime covertTimeStampToLocalDateTime(Long TimeStamp){
        return LocalDateTime.ofEpochSecond (TimeStamp/1000, 0, ZoneOffset.ofHours (8));
    }

    /**
     * @Author: liwencai
     * @Description: 获取megvii的结果集的list的JSON String
     * 示例：
     * {
     *     "code":"",
     *     "msg":"",
     *     "data":{
     *         "list":[]
     *     }
     * }
     * @Date: 2022/12/6
     * @Param jsonString:
     * @Return: java.lang.String
     */
    public Map<String, Object> convertToJsonList(String jsonString){
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonObjectLevelOne = JSONObject.parseObject(jsonString);
        String data = jsonObjectLevelOne.getString("data");
        JSONObject jsonObjectLevelTwo= JSONObject.parseObject(data);
        map.put("list",jsonObjectLevelTwo.getString("list"));
        map.put("pageNum",jsonObjectLevelTwo.getInteger("pageNum"));
        map.put("pageSize",jsonObjectLevelTwo.getInteger("pageSize"));
        map.put("total",jsonObjectLevelTwo.getInteger("total"));
        return map;
    }

    /**
     * @Author: liwencai
     * @Description: 获取时间差（*天*小时*分*秒）
     * @Date: 2022/10/27
     * @Param: startTime: 开始时间
     * @Param: endTime: 结束使劲按
     * @Return: java.lang.String
     */
    public String getTimeGap(LocalDateTime startTime, LocalDateTime endTime){
        Date nowDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
        Date alarmTimeStartTime = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = nowDate.getTime() - alarmTimeStartTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒
        long sec = diff % nd % nh % nm /ns;
        // 输出结果
        return (day+"天"+hour + "小时" + min + "分" +sec+"秒");
    }
    /* ***************************** 视频控制相关接口 *********************************** */


    /**
     * @Author: liwencai
     * @Description: 云台控制
     * @Date: 2023/2/21
     * @Param param:
     * @Return: boolean
     */
    public boolean ptzControl(MegviiPTZControlParam param){
        String uri = "/v1/api/video/realtime/ptzControl";
        String jsonResult = megviiHttpPostJSON(uri, param);
        MegviiResult megviiResult = JSON.parseObject(jsonResult, MegviiResult.class);
        if(0 == megviiResult.getCode()){
            return true;
        }
        return false;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备的websocket的路径，用于视频实时预览
     * @Date: 2023/2/21
     * @Param uuids: 对应设备编码 code
     * @Return: java.util.Map<java.lang.String,java.lang.String>
     */
    public Map<String, String> getWsUrlForPreview(List<String> uuids){
        Map<String, String> resultMap = new HashMap<>();
        String uri = "/v1/api/video/realtime/preview";
        for (String uuid : uuids) {
            Map<String, String> requestBodyMap = new HashMap<>();
            requestBodyMap.put("deviceUuid",uuid);
            String s = megviiHttpPostJSON(uri, requestBodyMap);
            JSONObject jsonObject = JSONObject.parseObject(s);
            if(null != jsonObject){
                String data = jsonObject.getString("data");
                if(StringUtils.isNotBlank(data)){
                    JSONObject dataResult = JSONObject.parseObject(data);
                    String url = dataResult.getString("url");
                    resultMap.put(uuid,url);
                }
            }
        }
        return resultMap;
    }

    /**
     * @Author: liwencai
     * @Description: 立即抓拍
     * @Date: 2023/2/21
     * @Param uuid: 旷世设备uuid 对应 ibs5.0 设备编码
     * @Return: java.lang.String
     */
    public String capture(String uuid){
        String uri = "/v1/api/video/realtime/capture";
        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("deviceUuid",uuid);
        String s = megviiHttpPostJSON(uri, requestBodyMap);
        JSONObject jsonObject = JSONObject.parseObject(s);
        if(null != jsonObject){
            String data = jsonObject.getString("data");
            if(StringUtils.isNotBlank(data)){
                JSONObject dataResult = JSONObject.parseObject(data);
                String captureUrl = dataResult.getString("captureUrl");
                return captureUrl;
            }
        }
        return null;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备的回放列表
     * @Date: 2023/2/21
     * @Param uuid: 旷世设备uuid 对应 ibs5.0 设备编码
     * @Param startTime: 开始时间 单位毫秒（下载使用）
     * @Param endTime: 结束时间 单位毫秒（下载使用）
     * @Return: com.thtf.face_recognition.dto.MegviiPage
     */
    public MegviiPage listPlaybackInfo(String uuid,Long startTime,Long endTime){
        String uri = "/v1/api/video/playback/list";
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("deviceUuid",uuid);
        requestBodyMap.put("startTime",startTime);
        requestBodyMap.put("endTime", endTime);
        String s = megviiHttpPostJSON(uri, requestBodyMap);
        JSONObject jsonObject = JSONObject.parseObject(s);
        if(null != jsonObject){
            String data = jsonObject.getString("data");
            MegviiPage megviiPage = JSON.parseObject(data, MegviiPage.class);
            return megviiPage;
        }else {
            return null;
        }
    }

    /* 视频回放下载 */
    /**
     * @Author: liwencai
     * @Description: 获取下载任务id
     * @Date: 2023/2/21
     * @Param uuid:
     * @Param startTime:
     * @Param endTime:
     * @Return: java.lang.String
     */
    public String getDownloadTaskUuid(String uuid,Long startTime,Long endTime){
        String uri = "/v1/api/video/playback/download";
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("deviceUuid",uuid);
        requestBodyMap.put("startTime",startTime);
        requestBodyMap.put("endTime", endTime);
        String s = megviiHttpPostJSON(uri, requestBodyMap);
        JSONObject jsonObject = JSONObject.parseObject(s);
        if(null != jsonObject){
            String data = jsonObject.getString("data");
            if(StringUtils.isNotBlank(data)){
                JSONObject dataResult = JSONObject.parseObject(data);
                String taskUuid = dataResult.getString("taskUuid");
                return taskUuid;
            }
        }
        return null;
    }

    public String getDownloadUrl(String taskUuid){
        String uri = "/v1/api/video/playback/open";
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("taskUuid",taskUuid);
        String s = megviiHttpPostJSON(uri, requestBodyMap);
        JSONObject jsonObject = JSONObject.parseObject(s);
        if(null != jsonObject){
            String data = jsonObject.getString("data");
            if(StringUtils.isNotBlank(data)){
                JSONObject dataResult = JSONObject.parseObject(data);
                String url = dataResult.getString("url");
                return url;
            }
        }
        return null;
    }


    /* ************************** 复用代码 ******************************** */
    /**
     * @Author: liwencai
     * @Description: 旷世人脸识别接口 http post请求
     * @Date: 2023/2/21
     * @Param uri: 路径
     * @Param requestBody: 负载 请求体 JSON
     * @Return: java.lang.String
     */
    String megviiHttpPostJSON(String uri, Object requestBody){
        String jsonParam = JSON.toJSONString(requestBody);
        Map<String, Object> headers = MegviiAuth.getAuthHeaders(uri, "POST", null, requestBody);
        try {
            String jsonResult = HttpUtil.httpPostJSON(megviiConfig.getBaseUrl() + uri, headers, jsonParam);
            return jsonResult;
        }catch (Exception e){
            return null;
        }
    }





}
