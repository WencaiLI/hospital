package com.thtf.elevator.controller.app;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.adminserver.ResultPage;
import com.thtf.common.dto.alarmserver.AppAlarmRecordDTO;
import com.thtf.common.dto.alarmserver.ListAlarmPageParamDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.elevator.common.constant.ItemTypeConstant;
import com.thtf.elevator.common.constant.ParameterConstant;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.convert.PageInfoConvert;
import com.thtf.elevator.service.ElevatorAppService;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.AppAlarmInfoVO;
import com.thtf.elevator.vo.AppItemSortDTO;
import com.thtf.elevator.vo.AppItemSortVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/2/28 18:47
 * @Description:
 */
@RestController
@RequestMapping(value ="/elevator/app/")
@Slf4j
public class AppElevatorController {
    @Resource
    private PageInfoConvert pageInfoConvert;

    @Resource
    private ElevatorService elevatorService;

    @Resource
    private ElevatorAppService elevatorAppService;

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private AlarmAPI alarmAPI;
    /**
     * @Author: liwencai
     * @Description: 前端展示界面数据
     * @Date: 2022/9/2
     * @Param sysCode: 子系统编码
     * @return: com.thtf.common.response.JsonResult<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @PostMapping("/displayInfo")
    JsonResult<List<DisplayInfoDTO>> displayInfo(@RequestParam(value ="sysCode")String sysCode,
                                                 @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                 @RequestParam(value = "areaCode",required = false) String areaCode){
        return JsonResult.querySuccess(elevatorAppService.displayInfo(sysCode,buildingCodes,areaCode));
    }

    /**
     * @Author: liwencai
     * @Description: 前端展示界面数据
     * @Date: 2022/9/2
     * @Param sysCode: 子系统编码
     * @return: com.thtf.common.response.JsonResult<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @PostMapping("/alarmInfo")
    JsonResult<AppAlarmInfoVO> getAlarmInfo(@RequestParam(value ="sysCode")String sysCode,
                                            @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                            @RequestParam(value = "areaCode",required = false) String areaCode){
        return JsonResult.querySuccess(elevatorAppService.getAlarmInfo(sysCode,buildingCodes,areaCode));
    }

    /**
     * @Author: liwencai
     * @Description: 设备列表查看（根据设备状态）
     * @Date: 2022/8/16
     * @Param param:
     * @return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/item_info")
    public JsonResult<PageInfoVO<AppItemSortVO>> getItemInfoByItemStatusAndType(@RequestBody AppItemSortDTO param){

        ListItemNestedParametersPageParamDTO listItemNestedParametersPageParamDTO = new ListItemNestedParametersPageParamDTO();

        BeanUtils.copyProperties(param,listItemNestedParametersPageParamDTO);
        if(StringUtils.isNotBlank(param.getAreaCodes())){
            listItemNestedParametersPageParamDTO.setAreaCodeList(Arrays.asList(param.getAreaCodes().split(",")));
        }else {
            if (StringUtils.isNotBlank(param.getBuildingCodes())){
                listItemNestedParametersPageParamDTO.setBuildingCodeList(Arrays.asList(param.getBuildingCodes().split(",")));
            }
        }
        if (StringUtils.isNotBlank(param.getItemTypeCodes())){
            listItemNestedParametersPageParamDTO.setItemTypeCodeList(Arrays.asList(param.getItemTypeCodes().split(",")));
        }

        List<ParameterTypeCodeAndValueDTO> parameters = new ArrayList<>();
        Integer runStatus = param.getRunStatus();
        if(null != runStatus){
            ParameterTypeCodeAndValueDTO parameter = new ParameterTypeCodeAndValueDTO();
            parameter.setParameterTypeCode(ParameterConstant.ELEVATOR_RUN_STATUS);
            parameter.setParameterValue(String.valueOf(param.getRunStatus()));
            parameters.add(parameter);
            
            ParameterTypeCodeAndValueDTO parameter1 = new ParameterTypeCodeAndValueDTO();
            parameter1.setParameterTypeCode(ParameterConstant.RUN_STATUS1);
            parameter1.setParameterValue(String.valueOf(param.getRunStatus()));
            parameters.add(parameter1);
            
            ParameterTypeCodeAndValueDTO parameter2 = new ParameterTypeCodeAndValueDTO();
            parameter2.setParameterTypeCode(ParameterConstant.RUN_STATUS2);
            parameter2.setParameterValue(String.valueOf(param.getRunStatus()));
            parameters.add(parameter2);
            
            ParameterTypeCodeAndValueDTO parameter3 = new ParameterTypeCodeAndValueDTO();
            parameter3.setParameterTypeCode(ParameterConstant.RUN_STATUS3);
            parameter3.setParameterValue(String.valueOf(param.getRunStatus()));
            parameters.add(parameter3);
        }
        listItemNestedParametersPageParamDTO.setParameterList(parameters);

        if(StringUtils.isNotBlank(param.getAlarmCategory())){
            if("0".equals(param.getAlarmCategory())){
                listItemNestedParametersPageParamDTO.setAlarm(1);
            }
            if ("1".equals(param.getAlarmCategory())){
                listItemNestedParametersPageParamDTO.setAlarm(0);
                listItemNestedParametersPageParamDTO.setFault(1);
            }
        }


        PageInfo<ItemNestedParameterVO> data = itemAPI.listItemNestedParametersPage(listItemNestedParametersPageParamDTO).getData();


        PageInfoVO<AppItemSortVO> pageInfoVO = pageInfoConvert.toPageInfoVO(data);
        if(null != data && !CollectionUtils.isEmpty(data.getList())){
            List<AppItemSortVO> resultList = new ArrayList<>();
            data.getList().forEach(e->{
                AppItemSortVO appItemSortVO = new AppItemSortVO();
                appItemSortVO.setItemName(e.getName());
                e.getParameterList().forEach(parameter -> {
                    if (ParameterConstant.ELEVATOR_RUN_STATUS.equals(parameter.getParameterType())
                    		||ParameterConstant.RUN_STATUS1.equals(parameter.getParameterType())
                    		||ParameterConstant.RUN_STATUS2.equals(parameter.getParameterType())
                    		||ParameterConstant.RUN_STATUS3.equals(parameter.getParameterType())){
                        appItemSortVO.setRunStatus(parameter.getValue());
                    }
                });
                if (1 == e.getAlarm()){
                    appItemSortVO.setAlarmCategory("0");
                }
                if (1 != e.getAlarm() && 1 == e.getFault()){
                    appItemSortVO.setAlarmCategory("1");
                }
                resultList.add(appItemSortVO);

            });
            pageInfoVO.setList(resultList);
            return JsonResult.querySuccess(pageInfoVO);
        }else {
            return JsonResult.querySuccess(pageInfoVO);
        }
    }

    /**
     * @Author: liwencai
     * @Description: 未处理报警
     * @Date: 2022/12/12
     * @Param param:
     * @Return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/alarm_unhandled")
    public JsonResult<ResultPage<AppAlarmRecordDTO>> getUnhandledAlarm(@RequestBody ListAlarmPageParamDTO param){
        param.setStatus(0);
        return alarmAPI.listAlarm(param);
    }

    /**
     * @Author: liwencai
     * @Description: 已处理报警 应该跟未处理报警
     * @Date: 2022/12/12
     * @Param param:
     * @Return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo<com.thtf.common.dto.alarmserver.AppAlarmRecordDTO>>
     */
    @PostMapping("/alarm_processed")
    public JsonResult<ResultPage<AppAlarmRecordDTO>> getProcessedAlarm(@RequestBody ListAlarmPageParamDTO param){
        param.setStatus(1);
        return alarmAPI.listAlarm(param);
    }

    /**
     * @Author: liwencai
     * @Description: 获取电梯类别
     * @Date: 2023/3/2
     * @Param sysCode:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.dto.itemserver.CodeAndNameDTO>>
     */
    @GetMapping("/getElevatorType")
    public JsonResult<List<CodeAndNameDTO>> getElevatorType(@RequestParam("sysCode") String sysCode){
        // 获取电梯的所有子类,这里假设只有一级父级
        TblItemType tblItemType = new TblItemType();
        tblItemType.setSysCode(sysCode);
        tblItemType.setIsLeaf(ItemTypeConstant.IS_LEAF);
        // 父类为itemType的设备类别
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        List<CodeAndNameDTO> codeAndNameList = new ArrayList<>();
        for (TblItemType itemType : itemTypeList) {
            CodeAndNameDTO codeAndNameDTO  =  new CodeAndNameDTO();
            codeAndNameDTO.setCode(itemType.getCode());
            codeAndNameDTO.setName(itemType.getName());
            codeAndNameList.add(codeAndNameDTO);
        }

        return JsonResult.querySuccess(codeAndNameList);
    }

}
