package com.thtf.face_recognition.domain.alarmMng.validate;

import com.thtf.common.exception.BusinessException;
import com.thtf.face_recognition.domain.alarmMng.alarmDetail.AlarmDetailRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liwencai
 * @since 2023/5/16
 */
@Component
public class AlarmDetailValidate {
    @Resource
    private AlarmDetailRepository alarmDetailRepository;

    public void shouldExistByItemCode(String itemCode){
        if(!alarmDetailRepository.existByItemCode(itemCode)){
            throw new BusinessException(400,"人脸识别设备接收到未接入系统的设备");
        }
    }

    public void shouldNotEmptyByItemCode(String itemCode) {
        if(StringUtils.isBlank(itemCode)){
            throw new BusinessException(400,"设备编码必须存在");
        }
    }
}
