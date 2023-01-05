package com.thtf.face_recognition.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageParamDTO;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageResultDTO;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblVideoItem;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.common.constant.MegviiConfig;
import com.thtf.face_recognition.common.enums.MegviiEventLevelEnum;
import com.thtf.face_recognition.common.enums.MegviiEventTypeEnum;
import com.thtf.face_recognition.common.enums.MegviiPersonTypeEnum;
import com.thtf.face_recognition.common.util.HttpUtil;
import com.thtf.face_recognition.common.util.megvii.StringUtil;
import com.thtf.face_recognition.dto.*;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.service.ManufacturerApiService;
import com.thtf.face_recognition.vo.FaceRecognitionAlarmParamVO;
import com.thtf.face_recognition.vo.FaceRecognitionAlarmResultVO;
import com.thtf.face_recognition.vo.FaceRecognitionFaultResultVO;
import com.thtf.face_recognition.vo.MegviiUserInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/11/13 17:57
 * @Description: 制造商：旷世科技 人脸识别API（盘古2.0）接口文档下称“文档”（盘古v1.3.0 OpenAPI）
 */
@Service("Megvii")
public class MegviiApiServiceImpl implements ManufacturerApiService {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @Autowired
    private ItemAPI itemAPI;

    @Autowired
    private AlarmAPI alarmAPI;

    @Autowired
    MegviiConfig megviiConfig;


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
        megviiPage.setTotal(Integer.valueOf((String)map.get("total")));
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

        ListAlarmInfoLimitOneParamDTO listAlarmInfoLimitOneParamDTO = new ListAlarmInfoLimitOneParamDTO();
        listAlarmInfoLimitOneParamDTO.setSystemCode(paramVO.getSysCode());
        // 故障
        listAlarmInfoLimitOneParamDTO.setAlarmCategory("1");
        listAlarmInfoLimitOneParamDTO.setPageSize(paramVO.getPageSize());
        listAlarmInfoLimitOneParamDTO.setPageNumber(paramVO.getPageNumber());
        PageInfo<TblAlarmRecordUnhandle> pageInfo = alarmAPI.listAlarmInfoLimitOnePage(listAlarmInfoLimitOneParamDTO).getData();
        result.setTotal(Long.valueOf(pageInfo.getTotal()).intValue());
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
            faceRecognitionFaultResultVO.setStayTime(getTimeGap(alarm.getAlarmTime(),LocalDateTime.now()));
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

    /**
     *
     */
    @Override
    public MegviiPage<MegviiItemEventDTO> listItemEventByItemCode(String itemCode, Integer pageNumber, Integer pageSize) {
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
            result.setTotal((Integer) map.get("total"));
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
                result.setStayTime(getTimeGap(alarmDateTime,LocalDateTime.now()));
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
        try {
            String jsonResult = HttpUtil.httpPostJson(megviiConfig.getBaseUrl() + uri, jsonParam);
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
     * 获取人员信息
     */
    public MegviiUserInfoDTO getUserInfoByUUId(String  uuid){
        MegviiUserInfoDTO result = new MegviiUserInfoDTO();
        if(StringUtil.isEmpty(uuid))
        {
            return null;
        }
        String uri = "/v1/api/person/query";
        Map<String, String> paramMap =  new HashMap<>();
        paramMap.put("uuid",uuid);
        String jsonParam = JSON.toJSONString(paramMap);
        try {
            String jsonResult = HttpUtil.httpPostJson(megviiConfig.getBaseUrl() + uri, jsonParam);
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
        try {
            String jsonResult = HttpUtil.httpPostJson(megviiConfig.getBaseUrl() + uri, jsonParam);
            Map<String, Object> map = convertToJsonList(jsonResult);
            result.setList(JSONArray.parseArray(String.valueOf(map.get("list")), MegviiDeviceDTO.class));
            result.setTotal(JSON.parseObject(String.valueOf(map.get("total")),Integer.class));
            result.setPageNum(JSON.parseObject(String.valueOf(map.get("pageNum")),Integer.class));
            result.setPageSize(JSON.parseObject(String.valueOf(map.get("pageSize")),Integer.class));
            return result;
        }catch (Exception e){
            return null;
        }
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
}
