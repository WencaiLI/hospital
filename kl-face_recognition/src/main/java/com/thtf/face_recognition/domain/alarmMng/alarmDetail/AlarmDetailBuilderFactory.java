package com.thtf.face_recognition.domain.alarmMng.alarmDetail;

import com.thtf.face_recognition.domain.alarmMng.validate.AlarmDetailValidate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liwencai
 * @since 2023/5/16
 */
@Component
public class AlarmDetailBuilderFactory {

    @Resource
    private AlarmDetailValidate alarmDetailValidate;

    public AlarmDetailBuilder create(){
        return new AlarmDetailBuilder(alarmDetailValidate);
    }
}
