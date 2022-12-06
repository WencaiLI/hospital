package com.thtf.face_recognition.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageParamDTO;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageResultDTO;
import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.common.constant.MegviiConfig;
import com.thtf.face_recognition.common.enums.MegviiEventLevelEnum;
import com.thtf.face_recognition.common.util.HttpUtil;
import com.thtf.face_recognition.dto.MegviiListEventRecordParamDTO;
import com.thtf.face_recognition.dto.MegviiListEventRecordResultDTO;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.service.ManufacturerApiService;
import com.thtf.face_recognition.vo.FaceRecognitionAlarmParamVO;
import com.thtf.face_recognition.vo.FaceRecognitionAlarmResultVO;
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
    public List<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO) {
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
        String uri = " /v1/api/event/record/list";
        String jsonParam = JSON.toJSONString(paramDTO);
        try {
            String jsonResult = HttpUtil.httpPostJson(megviiConfig.getBaseUrl() + uri, jsonParam);
            megviiListEventRecordResultDTOS =  JSONArray.parseArray(String.valueOf(this.convertToJsonList(jsonResult).get("list")), MegviiListEventRecordResultDTO.class);
        }catch (Exception ignored){

        }
        if (null == megviiListEventRecordResultDTOS){
            return null;
        }
        // List<MegviiListEventRecordResultDTO> megviiListEventRecordResultDTOS = listEventRecords(paramDTO);
        for (MegviiListEventRecordResultDTO item : megviiListEventRecordResultDTOS) {
            resultVOList.add(convertToMegviiListEventRecordResultDTO(item,allItems));
        }

        return resultVOList;
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
    public List<MegviiListEventRecordResultDTO> listEventRecords(MegviiListEventRecordParamDTO paramDTO){
        String uri = " /v1/api/event/record/list";
        String jsonParam = JSON.toJSONString(paramDTO);
        try {
            String jsonResult = HttpUtil.httpPostJson(megviiConfig.getBaseUrl() + uri, jsonParam);
            return JSONArray.parseArray(String.valueOf(this.convertToJsonList(jsonResult).get("list")), MegviiListEventRecordResultDTO.class);
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
