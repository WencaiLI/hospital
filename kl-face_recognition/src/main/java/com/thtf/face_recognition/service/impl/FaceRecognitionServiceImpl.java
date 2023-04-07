package com.thtf.face_recognition.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.constant.ItemConstants;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.entity.itemserver.TblVideoItem;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.common.config.ItemParameterConfig;
import com.thtf.face_recognition.common.constant.ParameterConstant;
import com.thtf.face_recognition.common.util.megvii.StringUtil;
import com.thtf.face_recognition.dto.DisplayParamDTO;
import com.thtf.face_recognition.dto.FaceRecognitionPointDTO;
import com.thtf.face_recognition.dto.mapstruct.FaceRecognitionItemConvert;
import com.thtf.face_recognition.dto.mapstruct.PageInfoConvert;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.vo.*;

import com.thtf.face_recognition.vo.PageInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private ItemParameterConfig itemParameterConfig;

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private AdminAPI adminAPI;

    @Resource
    private PageInfoConvert pageInfoConvert;


    @Resource
    FaceRecognitionItemConvert faceRecognitionItemConvert;


    /**
     * @Author: liwencai
     * @Description: 获取前端展示信息
     * @Date: 2022/12/7
     * @Param displayParamDTO:
     * @Return: com.thtf.face_recognition.vo.FaceRecognitionDisplayVO
     */
    @Override
    public FaceRecognitionDisplayVO getDisplayInfo(DisplayParamDTO displayParamDTO) {
        FaceRecognitionDisplayVO result = new FaceRecognitionDisplayVO();

        List<String> buildingCodeList = StringUtils.isNotBlank(displayParamDTO.getBuildingCodes())?Arrays.asList(displayParamDTO.getBuildingCodes().split(",")):adminAPI.listBuildingCodeUserSelf().getData();
        List<String> areaCodeList = StringUtils.isNotBlank(displayParamDTO.getAreaCode())?Arrays.asList(displayParamDTO.getAreaCode().split(",")):null;
        String areaCode = StringUtils.isNotBlank(displayParamDTO.getAreaCode())?displayParamDTO.getAreaCode():null;

        CountItemByParameterListDTO countItemByParameterListDTO = new CountItemByParameterListDTO();
        countItemByParameterListDTO.setSysCode(displayParamDTO.getSysCode());
        countItemByParameterListDTO.setBuildingCodeList(buildingCodeList);
        countItemByParameterListDTO.setAreaCode(areaCode);

        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(displayParamDTO.getSysCode());
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);

        Integer allItemCount = itemAPI.queryAllItemsCount(tblItem).getData();
        tblItem.setAlarm(ItemConstants.ITEM_ALARM_TRUE);
        Integer alarm = itemAPI.queryAllItemsCount(tblItem).getData();
        tblItem.setAlarm(ItemConstants.ITEM_ALARM_FALSE);
        tblItem.setFault(ItemConstants.ITEM_FAULT_TRUE);
        Integer fault = itemAPI.queryAllItemsCount(tblItem).getData();
        result.setItemNum(allItemCount);
        result.setFaultNum(fault);
        result.setAlarmNum(alarm);

        // 查询在线数量
        countItemByParameterListDTO.setParameterTypeCode(ParameterConstant.FACE_RECOGNITION_ONLINE);
        countItemByParameterListDTO.setParameterValue(ParameterConstant.FACE_RECOGNITION_ONLINE_VALUE);
        Integer onlineCount = itemAPI.countItemByParameterList(countItemByParameterListDTO).getData();
//        // 离线数量
//        countItemByParameterListDTO.setParameterTypeCode(ParameterConstant.FACE_RECOGNITION_ONLINE);
//        countItemByParameterListDTO.setParameterValue(ParameterConstant.FACE_RECOGNITION_OFFLINE_VALUE);
//        Integer offlineCount = itemAPI.countItemByParameterList(countItemByParameterListDTO).getData();

        result.setOnlineNum(onlineCount);
        // result.setOfflineNum(offlineCount);
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备信息
     * @Date: 2022/12/7
     * @Param paramVO:
     * @Return: com.thtf.face_recognition.vo.PageInfoVO
     */
    @Override
    public PageInfoVO<FaceRecognitionItemResultVO> listFaceRecognitionItem(FaceRecognitionItemParamVO paramVO) {
        ListItemNestedParametersPageParamDTO listItemNestedParametersPageParamDTO =  ListItemNestedParametersPageParamDTO.builder()
                .sysCode(paramVO.getSysCode())
                .buildingCodeList(StringUtils.isNotBlank(paramVO.getBuildingCodes()) ? Arrays.asList(paramVO.getBuildingCodes().split(",")) : null)
                .areaCodeList(StringUtils.isNotBlank(paramVO.getAreaCodes()) ? Arrays.asList(paramVO.getAreaCodes().split(",")) : null)
                .pageNumber(paramVO.getPageNumber())
                .pageSize(paramVO.getPageSize())
                .build();

        if(StringUtils.isNotBlank(paramVO.getKeyword())){
            listItemNestedParametersPageParamDTO.setCodeKey(paramVO.getKeyword());
            listItemNestedParametersPageParamDTO.setAreaKey(paramVO.getKeyword());
            listItemNestedParametersPageParamDTO.setNameKey(paramVO.getKeyword());
        }

        PageInfo<ItemNestedParameterVO> pageInfo = itemAPI.listItemNestedParametersPage(listItemNestedParametersPageParamDTO).getData();
        PageInfoVO<FaceRecognitionItemResultVO> pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
        if(CollectionUtils.isEmpty(pageInfo.getList())){
            return pageInfoVO;
        }

        List<String> itemCodeList = pageInfo.getList().stream().map(ItemNestedParameterVO::getCode).distinct().collect(Collectors.toList());
        ListVideoItemParamDTO listVideoItemParamDTO = new ListVideoItemParamDTO();
        listVideoItemParamDTO.setItemCodeList(itemCodeList);
        List<ListVideoItemResultDTO> videoItems = itemAPI.listVideoItemByRel(listVideoItemParamDTO).getData();

        List<FaceRecognitionItemResultVO> resultList = new ArrayList<>();
        pageInfo.getList().forEach(itemNestedParameterVO -> {
            FaceRecognitionItemResultVO faceRecognitionItemResultVO = new FaceRecognitionItemResultVO();
            faceRecognitionItemResultVO.setItemCode(itemNestedParameterVO.getCode());
            faceRecognitionItemResultVO.setItemName(itemNestedParameterVO.getName());
            faceRecognitionItemResultVO.setItemTypeName(itemNestedParameterVO.getTypeName());
            faceRecognitionItemResultVO.setDescription(itemNestedParameterVO.getDescription());
            faceRecognitionItemResultVO.setBuildingCode(itemNestedParameterVO.getBuildingCode());
            faceRecognitionItemResultVO.setAreaCode(itemNestedParameterVO.getAreaCode());
            faceRecognitionItemResultVO.setBuildingAreaName(itemNestedParameterVO.getAreaName());
            // 模型视角信息
            if(StringUtils.isNotBlank(itemNestedParameterVO.getViewLongitude())){
                faceRecognitionItemResultVO.setEye(Arrays.stream(itemNestedParameterVO.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if(StringUtils.isNotBlank(itemNestedParameterVO.getViewLatitude())){
                faceRecognitionItemResultVO.setCenter(Arrays.stream(itemNestedParameterVO.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            this.convertToParameterInfo(faceRecognitionItemResultVO,itemNestedParameterVO.getParameterList());
            if(CollectionUtils.isNotEmpty(videoItems)){
                videoItems.forEach(video->{
                    if(itemNestedParameterVO.getCode().equals(video.getItemCode())){
                        faceRecognitionItemResultVO.setVideoUsername(video.getUserName());
                        faceRecognitionItemResultVO.setVideoPassword(video.getPassword());
                        faceRecognitionItemResultVO.setIpAddress(video.getIp());
                        faceRecognitionItemResultVO.setChannelNum(video.getItemChannelNum());
                    }
                });
            }

            // 匹配报警信息
            faceRecognitionItemResultVO.setAlarmCategory(this.getAlarmCategory(itemNestedParameterVO.getAlarm(),itemNestedParameterVO.getFault()));
            resultList.add(faceRecognitionItemResultVO);
        });
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 将item中的alarm和fault转换为alarmCategory(报警表)
     * @Date: 2023/2/2
     * @Param alarm: 0 正常 1 报警
     * @Param fault: 0 正常 1 故障
     * @Return: java.lang.Integer
     */
    String getAlarmCategory(Integer alarm,Integer fault){
        if(1 == alarm){
            return ParameterConstant.FACE_RECOGNITION_ALARM_VALUE;
        }
        if(0 == alarm && 1 == fault){
            return ParameterConstant.FACE_RECOGNITION_FAULT_VALUE;
        }
        return null;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备点位信息
     * @Date: 2022/12/7
     * @Param itemCode: 设备编码
     * @Return: com.thtf.face_recognition.dto.FaceRecognitionPointDTO
     */
    @Override
    public FaceRecognitionPointDTO getMonitorPointInfo(String itemCode) {
        List<String> parameterCode = new ArrayList<>();
        parameterCode.add(ParameterConstant.FACE_RECOGNITION_ONLINE);
        parameterCode.add(ParameterConstant.FACE_RECOGNITION_Position);
        ListItemNestedParametersParamDTO param = new ListItemNestedParametersParamDTO();
        param.setItemCodeList(Collections.singletonList(itemCode));
        param.setParameterTypeCodeList(parameterCode);
        List<ListItemNestedParametersResultDTO> itemNestedParametersResultList = itemAPI.listItemNestedParameters(param).getData();
        if(null == itemNestedParametersResultList || itemNestedParametersResultList.size() == 0){
            return null;
        }

        ListItemNestedParametersResultDTO item = itemNestedParametersResultList.get(0);
        FaceRecognitionPointDTO result = new FaceRecognitionPointDTO();
        List<TblItemParameter> resultParameterList = new ArrayList<>();
        item.getParameterList().forEach(e->{
            if(ParameterConstant.FACE_RECOGNITION_ONLINE.equals(e.getParameterType())){
                result.setOnlineParameterCode(e.getCode());
                result.setOnlineValue(e.getValue());
                resultParameterList.add(e);
            }
            if(ParameterConstant.FACE_RECOGNITION_Position.equals(e.getParameterType())){
                result.setPositionParameterCode(e.getCode());
                result.setPositionValue(e.getValue());
                resultParameterList.add(e);
            }
        });

        // 模型点位信息
        if(null != item.getEye()){
            result.setEye(item.getEye());
        }
        if(null != item.getCenter()){
            result.setCenter(item.getCenter());
        }

        // 将item同名参数copy到result
        BeanUtils.copyProperties(item,result);
        // 参数信息
        this.convertToParameterInfo(result, item.getParameterList());
        // 相机信息
        ListVideoItemParamDTO listVideoItemParamDTO = new ListVideoItemParamDTO();
        listVideoItemParamDTO.setItemCodes(itemCode);
        // 摄像设备信息
        List<ListVideoItemResultDTO> videoItem = itemAPI.listVideoItemByRel(listVideoItemParamDTO).getData();
        // 一个人脸识别设备理论只绑定一个摄像设备
        if(null != videoItem && videoItem.size() == 1){
            result.setIpAddress(videoItem.get(0).getIp());
            result.setChannelNum(videoItem.get(0).getItemChannelNum());
            result.setPassword(videoItem.get(0).getPassword());
            result.setPassword(videoItem.get(0).getPassword());
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 转换点位信息
     * @Date: 2022/12/7
     * @Param faceRecognitionPointDTO:
     * @Param parameterList:
     * @Return: void
     */
    public void convertToParameterInfo(FaceRecognitionPointDTO faceRecognitionPointDTO, List<TblItemParameter> parameterList){
        parameterList.forEach(e->{
            if(ParameterConstant.FACE_RECOGNITION_ONLINE.equals(e.getParameterType())){
                faceRecognitionPointDTO.setOnlineParameterCode(e.getCode());
                faceRecognitionPointDTO.setOnlineValue(e.getValue());
            }
            if(ParameterConstant.FACE_RECOGNITION_Position.equals(e.getParameterType())){
                faceRecognitionPointDTO.setPositionParameterCode(e.getCode());
                faceRecognitionPointDTO.setPositionValue(e.getValue());
            }
        });
    }


    /**
     * @Author: liwencai
     * @Description: 转换点位信息
     * @Date: 2022/12/7
     * @Param faceRecognitionPointDTO:
     * @Param parameterList:
     * @Return: void
     */
    public void convertToParameterInfo(FaceRecognitionItemResultVO listFaceRecognitionItem, List<TblItemParameter> parameterList){
        List<TblItemParameter> list = new ArrayList<>();
        parameterList.forEach(e->{
            // 在线
            if(itemParameterConfig.getFaceRecognitionOnline().equals(e.getParameterType())){
                listFaceRecognitionItem.setOnlineParameterCode(e.getCode());
                listFaceRecognitionItem.setOnlineValue(e.getValue());
                list.add(e);

            }
            // 方位
            if(itemParameterConfig.getFaceRecognitionPosition().equals(e.getParameterType())){
                listFaceRecognitionItem.setPositionParameterCode(e.getCode());
                listFaceRecognitionItem.setPositionValue(e.getValue());
                list.add(e);
            }
            // 报警
            if(itemParameterConfig.getAlarm().equals(e.getParameterType())){
                listFaceRecognitionItem.setAlarmParameterCode(e.getCode());
                listFaceRecognitionItem.setAlarmParameterValue(e.getValue());
                list.add(e);
            }
            // 故障
            if(itemParameterConfig.getFault().equals(e.getParameterType())){
                listFaceRecognitionItem.setFaultParameterCode(e.getCode());
                listFaceRecognitionItem.setFaultParameterValue(e.getValue());
                list.add(e);
            }
        });
        listFaceRecognitionItem.setParameterList(faceRecognitionItemConvert.toTblItemParameterVOList(list));
    }
}
