package com.thtf.face_recognition.adapter.driven.persistence;

import com.thtf.face_recognition.application.dto.mapstruct.FaceRecognitionItemConvert;
import com.thtf.face_recognition.common.config.IdGeneratorSnowflake;
import com.thtf.face_recognition.domain.alarmMng.alarmDetail.AlarmDetailDO;
import com.thtf.face_recognition.domain.alarmMng.alarmDetail.AlarmDetailRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liwencai
 * @since 2023/5/16
 */
@Service
public class AlarmDetailRepositoryMP implements AlarmDetailRepository {

    @Resource
    private MegviiAlarmDataMapper megviiAlarmDataMapper;

    @Resource
    private FaceRecognitionItemConvert faceRecognitionItemConvert;

    @Resource(name = "megvii")
    private IdGeneratorSnowflake idGeneratorSnowflake;


    /**
     * 设备编码是否存在
     *
     * @param itemCode 设备编码
     * @return {@link boolean}
     * @author liwencai
     */
    @Override
    public boolean existByItemCode(String itemCode) {
        return false;
    }

    /**
     * 插入报警信息
     *
     * @param alarmDetailDO AlarmDetailDO对象
     * @return {@link boolean}
     * @author liwencai
     */
    @Override
    public AlarmDetailDO insert(AlarmDetailDO alarmDetailDO) {
        // TODO: 2023/5/16 新增，填充id并返回
        Long id = idGeneratorSnowflake.getId();
        megviiAlarmDataMapper.insert(faceRecognitionItemConvert.toAlarmDetail(alarmDetailDO));
        alarmDetailDO.setId(id);
        return alarmDetailDO;
    }
}
