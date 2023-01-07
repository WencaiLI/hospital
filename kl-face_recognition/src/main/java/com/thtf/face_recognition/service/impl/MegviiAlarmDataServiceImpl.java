package com.thtf.face_recognition.service.impl;

/**
 * @Author: liwencai
 * @Date: 2023/1/7 10:01
 * @Description:
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.thtf.face_recognition.entity.faceServer.MegviiAlarmData;
import com.thtf.face_recognition.mapper.MegviiAlarmDataMapper;
import com.thtf.face_recognition.service.MegviiAlarmDataService;
import org.springframework.stereotype.Service;

@Service
public class MegviiAlarmDataServiceImpl extends ServiceImpl<MegviiAlarmDataMapper, MegviiAlarmData> implements MegviiAlarmDataService {
}
