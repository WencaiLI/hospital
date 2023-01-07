package com.thtf.face_recognition.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.thtf.common.dto.itemserver.ItemParameterUpdateDTO;
import com.thtf.common.entity.itemserver.TblItem;
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
import java.util.Map;
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
        if(onlineItemCodeList.size()>0){
            String onlineItemCodeLists = String.join(",", onlineItemCodeList);
            itemAPI.updateAlarmOrFaultStatus(onlineItemCodeLists,null,0);
        }
        if(offlineItemCodeList.size()>0){
            String offlineItemCodeLists = String.join(",",offlineItemCodeList);
            itemAPI.updateAlarmOrFaultStatus(offlineItemCodeLists,null,1);
        }
    }

    @Scheduled(cron = "*/10 * * * * ?")  // 十分钟执行一次
    public void pullMegviiAlarm() throws Exception {
        megviiApiServiceImpl.listPushIntelligentData();
    }
    /**
     * @Author: liwencai
     * @Description: 测试修改状态
     * @Date: 2023/1/7
     * @Return: void
     */
    @Scheduled(cron = "0/10 * * * * ?")  // 十秒执行一次
    public void test(){
        List<TblItem> sub_face_recognition = itemAPI.searchItemBySysCodeAndAreaCode("sub_face_recognition", null).getData();
        if (null != sub_face_recognition){
            itemAPI.updateAlarmOrFaultStatus(sub_face_recognition.get(0).getCode(),0,null);
        }
    }
}
