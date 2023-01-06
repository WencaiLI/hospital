package com.thtf.face_recognition.job;

import com.thtf.common.dto.itemserver.ItemParameterUpdateDTO;
import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.common.constant.MegviiConfig;
import com.thtf.face_recognition.common.constant.ParameterConstant;
import com.thtf.face_recognition.common.enums.MegviiItemStatus;
import com.thtf.face_recognition.common.util.HttpUtil;
import com.thtf.face_recognition.dto.MegviiDeviceDTO;
import com.thtf.face_recognition.dto.MegviiListDeviceParamDTO;
import com.thtf.face_recognition.dto.MegviiPage;
import com.thtf.face_recognition.service.impl.MegviiApiServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/12/29 09:51
 * @Description:
 */
@Component
@Slf4j
public class ItemStatusJob {
    @Autowired
    private ItemAPI itemAPI;
    @Autowired
    MegviiConfig megviiConfig;
    @Resource
    MegviiApiServiceImpl megviiApiServiceImpl;
    // 更新设备状态
    // @Scheduled(cron = "0/10 * * * * ?")  // 十秒执行一次
    public void updateItemStatus(){
        MegviiListDeviceParamDTO listDeviceParamDTO = new MegviiListDeviceParamDTO();
        // listDeviceParamDTO.setDeviceType(); //设置设备类型
        // 取出全部设备信息
        listDeviceParamDTO.setPageSize(1);
        listDeviceParamDTO.setPageSize(9999);
        MegviiPage<MegviiDeviceDTO> megviiDevicePage = megviiApiServiceImpl.listMegviiDeviceDTO(listDeviceParamDTO);
        List<MegviiDeviceDTO> totalItems = megviiDevicePage.getList();
        if(null == totalItems || totalItems.size() == 0){
            return;
        }
        // 在线状态设备uuid
        List<String> onlineItemCodeList = totalItems.stream().filter(e -> e.getStatus().equals(MegviiItemStatus.ONLINE.getId())).map(MegviiDeviceDTO::getUuid).collect(Collectors.toList());
        List<String> offlineItemCodeList = totalItems.stream().filter(e -> e.getStatus().equals(MegviiItemStatus.OFFLINE.getId())).map(MegviiDeviceDTO::getUuid).collect(Collectors.toList());
        // 离线状态设备uuid
        ItemParameterUpdateDTO onlineChange = new ItemParameterUpdateDTO();
        onlineChange.setItemCodeList(onlineItemCodeList);
        onlineChange.setParameterCode(ParameterConstant.FACE_RECOGNITION_ONLINE);
        onlineChange.setNewValue("1");
        itemAPI.updateParameterValue(onlineChange);
        ItemParameterUpdateDTO offlineChange = new ItemParameterUpdateDTO();
        offlineChange.setItemCodeList(offlineItemCodeList);
        offlineChange.setParameterCode(ParameterConstant.FACE_RECOGNITION_ONLINE);
        offlineChange.setNewValue("0");
        itemAPI.updateParameterValue(offlineChange);
    }
    // @Scheduled(cron = "0/10 * * * * ?")  // 十秒执行一次
    public void pullMegviiAlarm(){
        String uri = "/v1/api/device/list";
        // String jsonResult = HttpUtil.httpPostJson(megviiConfig.getBaseUrl() + uri, jsonParam);
    }
}
