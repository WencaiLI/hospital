package com.thtf.face_recognition.application;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.face_recognition.adapter.driven.persistence.model.MegviiAlarmData;
import com.thtf.face_recognition.application.dto.AlarmOtherInfoDTO;
import com.thtf.face_recognition.domain.alarmMng.alarmDetail.AlarmDetailDO;

/**
 * @Author: liwencai
 * @Date: 2023/1/7 10:01
 * @Description:
 */
public interface MegviiAlarmDataService extends IService<MegviiAlarmData> {

    /**
     * 将报警信息同步到报警服务表中
     *
     * @param alarmDetailDO  AlarmDetailDO
     * @param alarmOtherInfo AlarmOtherInfoDTO
     * @author liwencai
     */
    void copyToAlarmSystem(AlarmDetailDO alarmDetailDO, AlarmOtherInfoDTO alarmOtherInfo);

}
