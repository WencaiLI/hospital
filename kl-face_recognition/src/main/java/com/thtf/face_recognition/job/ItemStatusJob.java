package com.thtf.face_recognition.job;

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

/**
 * @Author: liwencai
 * @Date: 2022/12/29 09:51
 * @Description:
 */
@Component
@Slf4j
public class ItemStatusJob {
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
        MegviiPage<MegviiDeviceDTO> megviiDeviceDTOMegviiPage = megviiApiServiceImpl.listMegviiDeviceDTO(listDeviceParamDTO);
        List<MegviiDeviceDTO> totalItems = megviiDeviceDTOMegviiPage.getList();
        // 在线状态设备uuid
        totalItems.stream().filter(e->e.getStatus().equals(""));
        // 离线状态设备uuid

    }
}
