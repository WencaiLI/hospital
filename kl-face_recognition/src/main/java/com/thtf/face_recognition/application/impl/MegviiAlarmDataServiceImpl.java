package com.thtf.face_recognition.application.impl;

/**
 * @Author: liwencai
 * @Date: 2023/1/7 10:01
 * @Description:
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.common.constant.AlarmConstants;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.adapter.driven.persistence.MegviiAlarmDataMapper;
import com.thtf.face_recognition.adapter.driven.persistence.model.MegviiAlarmData;
import com.thtf.face_recognition.application.MegviiAlarmDataService;
import com.thtf.face_recognition.application.dto.AlarmOtherInfoDTO;
import com.thtf.face_recognition.domain.alarmMng.alarmDetail.AlarmDetailDO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class MegviiAlarmDataServiceImpl extends ServiceImpl<MegviiAlarmDataMapper, MegviiAlarmData> implements MegviiAlarmDataService {

    @Resource
    private AlarmAPI alarmAPI;

    @Resource
    private ItemAPI itemAPI;

    /**
     * 将报警信息同步到报警服务表中
     *
     * @param alarmDetailDO  AlarmDetailDO
     * @param alarmOtherInfo AlarmOtherInfoDTO
     * @author liwencai
     */
    @Override
    @Transactional
    public void copyToAlarmSystem(AlarmDetailDO alarmDetailDO, AlarmOtherInfoDTO alarmOtherInfo) {
        TblItem item = itemAPI.searchItemByItemCode(alarmDetailDO.getItemCode()).getData();
        if (null == item) {
            return;
        }
        TblAlarmRecordUnhandle tblAlarmRecordUnhandle = new TblAlarmRecordUnhandle();
        tblAlarmRecordUnhandle.setAlarmTime(alarmDetailDO.getAlarmTime());
        tblAlarmRecordUnhandle.setItemId(String.valueOf(item.getId()));
        tblAlarmRecordUnhandle.setItemCode(item.getCode());
        tblAlarmRecordUnhandle.setItemTypeCode(item.getTypeCode());
        tblAlarmRecordUnhandle.setParameterCode(alarmOtherInfo.getParameterCode());
        tblAlarmRecordUnhandle.setSystemCode(alarmOtherInfo.getSystemCode());
        tblAlarmRecordUnhandle.setSystemName(alarmOtherInfo.getSystemName());
        tblAlarmRecordUnhandle.setBuildingAreaCode(item.getAreaCode());
        tblAlarmRecordUnhandle.setBuildingAreaName(item.getAreaName());
        tblAlarmRecordUnhandle.setBuildingArea(item.getBuildingCode());
        tblAlarmRecordUnhandle.setAlarmDescription(String.valueOf(alarmDetailDO.getId()));
        tblAlarmRecordUnhandle.setAlarmLevel(alarmOtherInfo.getAlarmLevel());
        tblAlarmRecordUnhandle.setAlarmType(alarmOtherInfo.getAlarmType());
        tblAlarmRecordUnhandle.setAlarmCategory(alarmOtherInfo.getAlarmCategory());
        tblAlarmRecordUnhandle.setViewLatitude(item.getViewLatitude());
        tblAlarmRecordUnhandle.setViewLongitude(item.getViewLongitude());
        // 新增报警
        alarmAPI.insertAlarmUnhandled(tblAlarmRecordUnhandle);
        // 更新设备状态
        itemAPI.updateAlarmOrFaultStatus(item.getCode(), AlarmConstants.ALARM_CATEGORY_INTEGER, null);
    }

}
