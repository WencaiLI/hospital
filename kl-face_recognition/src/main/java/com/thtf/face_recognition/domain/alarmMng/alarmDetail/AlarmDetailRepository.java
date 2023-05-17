package com.thtf.face_recognition.domain.alarmMng.alarmDetail;

/**
 * @author liwencai
 * @since 2023/5/16
 */
public interface AlarmDetailRepository {
    /**
     * 设备编码是否存在
     *
     * @param itemCode 设备编码
     * @return {@link boolean}
     * @author liwencai
     */
    boolean existByItemCode(String itemCode);

    /**
     * 插入报警信息
     *
     * @param alarmDetailDO AlarmDetailDO对象
     * @return {@link boolean}
     * @author liwencai
     */
    AlarmDetailDO insert(AlarmDetailDO alarmDetailDO);
}
