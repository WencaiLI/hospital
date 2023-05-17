package com.thtf.face_recognition.adapter.driving.rpc.megvii;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thtf.face_recognition.application.MegviiAlarmDataService;
import com.thtf.face_recognition.application.dto.*;
import com.thtf.face_recognition.application.vo.MegviiUserInfoDTO;
import com.thtf.face_recognition.common.auth.MegviiAuth;
import com.thtf.face_recognition.common.constant.MegviiConfig;
import com.thtf.face_recognition.common.util.HttpUtil;
import com.thtf.face_recognition.common.util.megvii.StringUtil;
import com.thtf.face_recognition.domain.alarmMng.alarmDetail.*;
import com.thtf.face_recognition.domain.alarmMng.enums.ImageTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 旷世可调用接口
 * @author liwencai
 * @since 2023/5/16
 */
@Component
public class MegviiRpc {

    @Resource
    private MegviiAlarmDataService megviiAlarmDataService;

    @Autowired
    private MegviiConfig megviiConfig;

    @Resource
    private AlarmDetailBuilderFactory alarmDetailBuilderFactory;

    @Resource
    private AlarmDetailRepository alarmDetailRepository;

    @Resource
    private AlarmDetailOptionHandler alarmDetailOptionHandler;


    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/6
     * @Param paramDTO:
     * @Return: java.util.List<com.thtf.face_recognition.application.dto.MegviiListEventRecordResultDTO>
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
            map.put("list", JSONArray.parseArray(String.valueOf(map.get("list")), MegviiListEventRecordResultDTO.class));
            return map;
        }catch (Exception e){
            return null;
        }
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
     * @Description: 获取人员信息
     * @Date: 2023/2/21
     * @Param uuid:
     * @Return: com.thtf.face_recognition.application.vo.MegviiUserInfoDTO
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

    public static final String MEGVII_ALARM_TEMPLATE = "{\"alarmControlType\":1," +
            "\"alarmEndTime\":\"%s\"," +
            "\"alarmRecordUuId\":\"5ce33c9accb94a978976b232958bdc88\"," +
            "\"alarmTime\":\"%s\"," +
            "\"alarmType\":\"%s\"," +
            "\"areaId\":0," +
            "\"continueTime\":\"3.0\"," +
            "\"deviceName\":\"报警车辆模拟\"," +
            "\"deviceUuid\":\"RLSB_TYPE_%s"+"\"," +
            "\"targetRect\":[{\"bottom\":82.376396,\"left\":46.54868,\"right\":51.40949,\"top\":12}]," +
            "\"wholeImageUrl\":\"https://img.51miz.com/Photo/2017/06/05/15/P894207_edcab57f8cc99b89c686ca312df9ce05.jpg\",\n" +
            "\"arithmeticPackageType\":1," +
            "\"color\":1," +
            "\"fireEquipmentNumber\":3," +
            "\"gasCylinderFunction\":2," +
            "\"indicatorStatusColor\":\"㓒灯亮\"," +
            "\"personInfo\":[{\"uuid\":\"xssddss\",\"name\":\"李文彩\",\"bodyImageUrl\":\"https://img.51miz.com/Photo/2017/06/05/15/P894207_edcab57f8cc99b89c686ca312df9ce05.jpg\"}]\n" +
            "}";

    /**
     * @Author: liwencai
     * @Description: 向数据库写入智能分析事件（测试环境）
     * @Date: 2023/1/7
     * @Return: com.thtf.face_recognition.application.dto.MegviiPage<com.thtf.face_recognition.application.dto.MegviiPushDataIntelligentDTO>
     */
    @Transactional
    public MegviiPage<MegviiPushDataIntelligentDTO> listPushIntelligentData() throws Exception {
        // 真实数据来源
        // String uri = "/v1/api/device/list";
        // String jsonResult = HttpUtil.httpPostJson(megviiConfig.getBaseUrl() + uri, null);
        // String uri = "/v1/api/device/list";
        // Map<String, Object> headers = MegviiAuth.getAuthHeaders(uri, "POST", null, null);
        // String jsonResult = HttpUtil.httpPostJSON(megviiConfig.getBaseUrl() + uri, headers, null);
        // 模拟数据来源
        Random random = new Random();
        int max= 0 ;
        int min = 5;
        int alarmType = random.nextInt(min+max)+min;
        String alarmTime = String.valueOf(System.currentTimeMillis());
        String jsonResult = String.format(MEGVII_ALARM_TEMPLATE, alarmTime, alarmTime, alarmType, alarmType);

        // 转换为报警对象
        MegviiPushDataIntelligentDTO megviiPushDataIntelligentDTO = jsonToMegviiPushDataIntelligentDTO(jsonResult);
        // 设备编码
        String itemCode = megviiPushDataIntelligentDTO.getDeviceUuid();
        // 构建领域对象
        AlarmDetailBuilder alarmDetailBuilder = alarmDetailBuilderFactory.create()
                .itemCode(itemCode)
                .alarmTime(toLocalDateTimeMilliseconds(megviiPushDataIntelligentDTO.getAlarmTime()))
                .alarmType(megviiPushDataIntelligentDTO.getAlarmType());
        if(StringUtils.isNotBlank(megviiPushDataIntelligentDTO.getWholeImageUrl())){
            alarmDetailBuilder.imageType(ImageTypeEnum.PANORAMA.getId())
                    .imageUrl(megviiPushDataIntelligentDTO.getWholeImageUrl())
                    .targetRect(megviiPushDataIntelligentDTO.getTargetRect());
        }
        if(CollectionUtils.isNotEmpty(megviiPushDataIntelligentDTO.getPersonInfo())){
            // todo
        }
        AlarmDetailDO alarmDetailDO = alarmDetailBuilder.build();
        // 执行插入操作
        AlarmDetailDO insert = alarmDetailRepository.insert(alarmDetailDO);
        // 补充接入信息
        AlarmOtherInfoDTO alarmOtherInfo = AlarmOtherInfoDTO.builder()
                .parameterCode("megvii")
                .systemCode("sub_face_recognition")
                .systemName("人脸识别子系统")
                .alarmCategory(0) // 自定义判断规则
                .alarmLevel(1) // 自定义判断规则
                .alarmType(2) // 自定义判断规则
                .build();
        megviiAlarmDataService.copyToAlarmSystem(alarmDetailDO,alarmOtherInfo);
        alarmDetailOptionHandler.insertHandler(alarmDetailDO);
        return null;
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
     * @Return: com.thtf.face_recognition.application.dto.MegviiPage
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
                return dataResult.getString("url");
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
            return HttpUtil.httpPostJSON(megviiConfig.getBaseUrl() + uri, headers, jsonParam);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * @Author: liwencai
     * @Description: 将人脸识别推送数据转换为对象
     * @Date: 2023/2/21
     * @Param jsonString:
     * @Return: com.thtf.face_recognition.application.dto.MegviiPushDataIntelligentDTO
     */
    public static MegviiPushDataIntelligentDTO jsonToMegviiPushDataIntelligentDTO(String jsonString){
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

}
