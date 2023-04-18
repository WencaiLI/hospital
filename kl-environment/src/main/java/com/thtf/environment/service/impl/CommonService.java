package com.thtf.environment.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.thtf.common.constant.AlarmConstants;
import com.thtf.common.constant.ItemConstants;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.ItemAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2023/3/15 17:13
 * @Description:
 */
@Service
@Slf4j
public class CommonService {

    @Autowired
    private ItemAPI itemAPI;

    public String getParameterValueByStateExplain(String systemCode,String parameterTypeCode,String itemTypeCodes,String[] keywords){
        Map<String, String> map = itemAPI.listParameterStateExplainBySysCode(parameterTypeCode,systemCode,itemTypeCodes).getData();
        System.out.println(map);
        if(null != map){
            String stateExplain = map.get(parameterTypeCode);
            System.out.println("parameterTypeCode "+parameterTypeCode+"  stateExplain "+stateExplain);
            if(StringUtils.isNotBlank(stateExplain)){
                String[] split = stateExplain.split(";");
                for (String explain :split) {
                    String[] valueAndWords = explain.split(":");
                    if(valueAndWords.length == 2){
                        for (String keyWord: keywords){
                            if(valueAndWords[1].contains(keyWord)){
                                return valueAndWords[0];
                            }
                        }
                    }
                }

            }
        }
        return null;
    }

    public String getParameterValueByStateExplain(String parameterCode, List<TblItemParameter> parameterList, String[] keywords) {
        for (TblItemParameter parameter : parameterList) {
            if(parameter.getParameterType().equals(parameterCode)){
                for (String keyword : keywords) {
                    if(parameter.getStateExplain().contains(keyword)){
                        String[] split = parameter.getStateExplain().split(";");
                        for (String s : split) {
                            String[] split1 = s.split(":");
                            if(split1.length == 2){
                                if(split1[1].contains(keyword)){
                                    return split1[0];
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取报警留置时长
     * @author liwencai
     * @param alarmTime 报警时间
     * @return {@link String}
     */
    String getAlarmStayTime(LocalDateTime alarmTime){
        if(null == alarmTime){
            return null;
        }
        long duration = LocalDateTimeUtil.between(alarmTime, LocalDateTime.now(), ChronoUnit.MILLIS);
        return DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND);
    }

    /**
     *  获取报警留置时长
     * @author liwencai
     * @param alarmTime 报警时间
     * @param now 当前时间
     * @return {@link String}
     */
    String getAlarmStartTime(LocalDateTime alarmTime ,LocalDateTime now){
        if(null == alarmTime || null == now){
            return null;
        }
        long duration = LocalDateTimeUtil.between(alarmTime, now, ChronoUnit.MILLIS);
        return DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND);
    }

    Integer getAlarmCategory(Integer itemAlarmVale,Integer itemFaultValue) {
        if(null == itemAlarmVale || null == itemFaultValue){
            return null;
        }
        if(itemAlarmVale.equals(ItemConstants.ITEM_ALARM_TRUE)){
            return AlarmConstants.ALARM_CATEGORY_INTEGER;
        }else if (ItemConstants.ITEM_ALARM_FALSE.equals(itemAlarmVale) && ItemConstants.ITEM_FAULT_TRUE.equals(itemFaultValue)){
            return AlarmConstants.FAULT_CATEGORY_INTEGER;
        }else {
            return null;
        }
    }

    String getAlarmCategoryString(Integer itemAlarmVale,Integer itemFaultValue){
        Integer integerValue = this.getAlarmCategory(itemAlarmVale, itemFaultValue);
        if(null == integerValue){
            return null;
        }else {
            return String.valueOf(integerValue);
        }

    }
}

