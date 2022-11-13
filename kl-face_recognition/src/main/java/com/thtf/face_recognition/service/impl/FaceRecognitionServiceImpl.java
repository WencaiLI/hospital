package com.thtf.face_recognition.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageParamDTO;
import com.thtf.common.dto.itemserver.ListItemByKeywordPageResultDTO;
import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.vo.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 09:56
 * @Description:
 */
@Service("face_recognition")
public class FaceRecognitionServiceImpl implements FaceRecognitionService {

    @Autowired
    private RedisOperationService redisOperationService;

    @Resource
    private ItemAPI itemAPI;


    @Override
    public FaceRecognitionDisplayVO getDisplayInfo(String sysCode) {
        return null;
    }

    @Override
    public List<FaceRecognitionItemResultVO> listFaceRecognitionItem(FaceRecognitionItemParamVO paramVO) {
        ListItemByKeywordPageParamDTO feignParam = new ListItemByKeywordPageParamDTO();
        BeanUtils.copyProperties(paramVO,feignParam);
        System.out.println(feignParam);
        //feignParam.setSysCode(paramVO.get);
        if(StringUtils.isNotBlank(paramVO.getKeyword())){
            feignParam.setKeywordOfName(paramVO.getKeyword());
            feignParam.setKeywordOfCode(paramVO.getKeyword());
            feignParam.setKeywordOfDesc(paramVO.getKeyword());
        }
        PageInfo<ListItemByKeywordPageResultDTO> data = itemAPI.listItemByKeywordPage(feignParam).getData();
        System.out.println(data);
        return null;
    }

//    @Override
//    public List<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO) {
//        return null;
//    }
}
