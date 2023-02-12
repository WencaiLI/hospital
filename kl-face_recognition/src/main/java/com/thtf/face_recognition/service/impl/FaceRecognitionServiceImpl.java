package com.thtf.face_recognition.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.entity.itemserver.TblVideoItem;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.common.constant.ParameterConstant;
import com.thtf.face_recognition.common.util.megvii.StringUtil;
import com.thtf.face_recognition.dto.DisplayParamDTO;
import com.thtf.face_recognition.dto.FaceRecognitionPointDTO;
import com.thtf.face_recognition.dto.mapstruct.FaceRecognitionItemConvert;
import com.thtf.face_recognition.dto.mapstruct.PageInfoConvert;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.vo.*;

import com.thtf.face_recognition.vo.PageInfoVO;
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
    private RedisOperationService redisOperationService;

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private AlarmAPI alarmAPI;

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

        List<String> buildingCodeList = null;
        String areaCode = null;
        CountItemByParameterListDTO countItemByParameterListDTO = new CountItemByParameterListDTO();
        countItemByParameterListDTO.setSysCode(displayParamDTO.getSysCode());
        if(StringUtils.isNotBlank(displayParamDTO.getBuildingCodes())){
            buildingCodeList = Arrays.asList(displayParamDTO.getBuildingCodes().split(","));
            countItemByParameterListDTO.setBuildingCodeList(buildingCodeList);
        }else {
            if(StringUtils.isNotBlank(displayParamDTO.getAreaCode())){
                areaCode = displayParamDTO.getAreaCode();
                countItemByParameterListDTO.setAreaCode(displayParamDTO.getAreaCode());
            }

        }

        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(displayParamDTO.getSysCode());
        tblItem.setBuildingCodeList(buildingCodeList);
        if(StringUtils.isNotBlank(areaCode)){
            tblItem.setAreaCodeList(Collections.singletonList(areaCode));
        }

        Integer allItemCount = itemAPI.queryAllItemsCount(tblItem).getData();
        tblItem.setAlarm(1);
        Integer alarm = itemAPI.queryAllItemsCount(tblItem).getData();
        tblItem.setAlarm(null);
        tblItem.setAlarm(0);
        tblItem.setFault(1);
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
        List<String> itemCodeList = pageInfo.getList().stream().map(ListItemByKeywordPageResultDTO::getCode).collect(Collectors.toList());
        PageInfoVO<FaceRecognitionItemResultVO> pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
        // 集中查询摄像机信息
        ListVideoItemParamDTO listVideoItemParamDTO = new ListVideoItemParamDTO();
        listVideoItemParamDTO.setItemCodeList(itemCodeList);
        List<ListVideoItemResultDTO> videoItems = itemAPI.listVideoItemByRel(listVideoItemParamDTO).getData();
        // 集中查询设备参数
        List<String> parameterCode = new ArrayList<>();
        parameterCode.add(ParameterConstant.FACE_RECOGNITION_Position);
        parameterCode.add(ParameterConstant.FACE_RECOGNITION_ONLINE);
        parameterCode.add(ParameterConstant.FACE_RECOGNITION_ALARM);
        parameterCode.add(ParameterConstant.FACE_RECOGNITION_FAULT);
        ListItemNestedParametersParamDTO param = new ListItemNestedParametersParamDTO();
        param.setItemCodeList(itemCodeList);
        param.setParameterTypeCodeList(parameterCode);
        List<ListItemNestedParametersResultDTO> itemNestedParametersResultList = itemAPI.listItemNestedParameters(param).getData();
        // 结果集
        pageInfo.getList().forEach(e->{
            itemNestedParametersResultList.forEach(item->{
                if(item.getItemCode().equals(e.getCode())){
                    FaceRecognitionItemResultVO result = new FaceRecognitionItemResultVO();
                    result.setItemCode(e.getCode());
                    result.setItemName(e.getName());
                    result.setItemTypeName(e.getItemTypeName());
                    result.setDescription(e.getDescription());
                    result.setBuildingCode(e.getBuildingCode());
                    result.setAreaCode(e.getAreaCode());
                    result.setBuildingAreaName(e.getAreaName());
                    // 模型视角信息
                    if(StringUtils.isNotBlank(e.getViewLongitude())){
                        result.setEye(Arrays.stream(e.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                    if(StringUtils.isNotBlank(e.getViewLatitude())){
                        result.setCenter(Arrays.stream(e.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                    this.convertToParameterInfo(result,item.getParameterList());
                    videoItems.forEach(video->{
                        if(e.getCode().equals(video.getItemCode())){
                            result.setVideoUsername(video.getUserName());
                            result.setVideoPassword(video.getPassword());
                            result.setIpAddress(video.getIp());
                            result.setChannelNum(video.getItemChannelNum());
                        }
                    });
                    // 匹配报警信息
                    result.setAlarmCategory(this.getAlarmCategory(item.getAlarm(),item.getFault()));
                    resultList.add(result);
                }
            });

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
            if(ParameterConstant.FACE_RECOGNITION_ONLINE.equals(e.getParameterType())){
                listFaceRecognitionItem.setOnlineParameterCode(e.getCode());
                listFaceRecognitionItem.setOnlineValue(e.getValue());
                list.add(e);

            }
            // 方位
            if(ParameterConstant.FACE_RECOGNITION_Position.equals(e.getParameterType())){
                listFaceRecognitionItem.setPositionParameterCode(e.getCode());
                listFaceRecognitionItem.setPositionValue(e.getValue());
                list.add(e);
            }
            // 报警
            if(ParameterConstant.FACE_RECOGNITION_ALARM.equals(e.getParameterType())){
                listFaceRecognitionItem.setAlarmParameterCode(e.getCode());
                listFaceRecognitionItem.setAlarmParameterValue(e.getValue());
                list.add(e);
            }
            // 故障
            if(ParameterConstant.FACE_RECOGNITION_FAULT.equals(e.getParameterType())){
                listFaceRecognitionItem.setFaultParameterCode(e.getCode());
                listFaceRecognitionItem.setFaultParameterValue(e.getValue());
                list.add(e);
            }
        });
        listFaceRecognitionItem.setParameterList(faceRecognitionItemConvert.toTblItemParameterVOList(list));
    }
}
