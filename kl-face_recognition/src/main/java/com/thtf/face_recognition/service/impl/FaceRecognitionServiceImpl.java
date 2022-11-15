package com.thtf.face_recognition.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageParamDTO;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageResultDTO;
import com.thtf.common.dto.itemserver.ListVideoItemParamDTO;
import com.thtf.common.dto.itemserver.ListVideoItemResultDTO;
import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.dto.mapstruct.PageInfoConvert;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.vo.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 09:56
 * @Description:
 */
@Service("face_recognition")
public class FaceRecognitionServiceImpl implements FaceRecognitionService {

    @Autowired
    private RedisOperationService redisOperationService;

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private PageInfoConvert pageInfoConvert;


    @Override
    public FaceRecognitionDisplayVO getDisplayInfo(String sysCode) {
        return null;
    }

    @Override
    public PageInfoVO listFaceRecognitionItem(FaceRecognitionItemParamVO paramVO) {
        List<FaceRecognitionItemResultVO> resultList = new ArrayList<>();
        ListItemByKeywordPageParamDTO feignParam = new ListItemByKeywordPageParamDTO();
        BeanUtils.copyProperties(paramVO,feignParam);
        if(StringUtils.isNotBlank(paramVO.getKeyword())){
            feignParam.setKeywordOfName(paramVO.getKeyword());
            feignParam.setKeywordOfCode(paramVO.getKeyword());
            feignParam.setKeywordOfDesc(paramVO.getKeyword());
        }
        PageInfo<ListItemByKeywordPageResultDTO> pageInfo = itemAPI.listItemByKeywordPage(feignParam).getData();
        if(pageInfo.getList().size() == 0){
            return null;
        }
        // 设备编码集
        List<String> itemCode = pageInfo.getList().stream().map(ListItemByKeywordPageResultDTO::getCode).collect(Collectors.toList());
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
        // 集中查询摄像机信息
        ListVideoItemParamDTO listVideoItemParamDTO = new ListVideoItemParamDTO();
        listVideoItemParamDTO.setItemCodeList(itemCode);
        List<ListVideoItemResultDTO> videoItems = itemAPI.listVideoItemByRel(listVideoItemParamDTO).getData();
        // 集中查询设备参数
        // 结果集
        pageInfo.getList().forEach(e->{
            FaceRecognitionItemResultVO result = new FaceRecognitionItemResultVO();
            result.setItemCode(e.getCode());
            result.setItemName(e.getName());
            result.setItemTypeName(e.getItemTypeName());
            result.setItemDescription(e.getDescription());
            if(StringUtils.isNotBlank(e.getViewLongitude())){
                result.setEye(Arrays.stream(e.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if(StringUtils.isNotBlank(e.getViewLatitude())){
                result.setCenter(Arrays.stream(e.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            result.setPosition(" "); // todo liwencai 方位

            videoItems.forEach(video->{
                if(e.getCode().equals(video.getItemCode())){
                    result.setVideoUsername(video.getUserName());
                    result.setVideoPassword(video.getPassword());
                    result.setIpAddress(video.getIp());
                    result.setChannelNum(video.getItemChannelNum());
                }
            });
            resultList.add(result);
        });
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

//    @Override
//    public List<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO) {
//        return null;
//    }
}
