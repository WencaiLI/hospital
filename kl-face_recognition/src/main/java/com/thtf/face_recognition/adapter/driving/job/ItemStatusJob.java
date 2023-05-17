package com.thtf.face_recognition.adapter.driving.job;

import com.thtf.common.feign.ItemAPI;
import com.thtf.face_recognition.adapter.driving.rpc.megvii.MegviiRpc;
import com.thtf.face_recognition.application.dto.MegviiDeviceDTO;
import com.thtf.face_recognition.application.dto.MegviiListDeviceParamDTO;
import com.thtf.face_recognition.application.dto.MegviiPage;
import com.thtf.face_recognition.common.enums.MegviiItemStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/12/29 09:51
 * @Description: 旷世人脸识别数据推送
 */
@Component
@Slf4j
public class ItemStatusJob {

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private MegviiRpc megviiRpc;


    // 更新设备状态
    // @Scheduled(cron = "0/10 * * * * ?")  // 十秒执行一次
    public void updateItemStatus() {
        MegviiListDeviceParamDTO listDeviceParamDTO = new MegviiListDeviceParamDTO();
        // listDeviceParamDTO.setDeviceType(); //设置设备类型
        // 取出全部设备信息
        listDeviceParamDTO.setPageSize(1);
        listDeviceParamDTO.setPageSize(9999);
        MegviiPage<MegviiDeviceDTO> megviiDevicePage = megviiRpc.listMegviiDeviceDTO(listDeviceParamDTO);
        List<MegviiDeviceDTO> totalItems = megviiDevicePage.getList();
        if (null == totalItems || totalItems.size() == 0) {
            return;
        }
        // 在线状态设备uuid
        List<String> onlineItemCodeList = totalItems.stream().filter(e -> e.getStatus().equals(MegviiItemStatus.ONLINE.getId())).map(MegviiDeviceDTO::getUuid).collect(Collectors.toList());
        List<String> offlineItemCodeList = totalItems.stream().filter(e -> e.getStatus().equals(MegviiItemStatus.OFFLINE.getId())).map(MegviiDeviceDTO::getUuid).collect(Collectors.toList());
        // 离线状态设备uuid
        if (onlineItemCodeList.size() > 0) {
            String onlineItemCodeLists = String.join(",", onlineItemCodeList);
            itemAPI.updateAlarmOrFaultStatus(onlineItemCodeLists, null, 0);
        }
        if (offlineItemCodeList.size() > 0) {
            String offlineItemCodeLists = String.join(",", offlineItemCodeList);
            itemAPI.updateAlarmOrFaultStatus(offlineItemCodeLists, null, 1);
        }
        // todo 向前端推送编码数据
    }

    /**
     * @Author: liwencai
     * @Description: 拉取报警信息, 并向数据库写入报警信息
     * @Date: 2023/2/20
     * @Return: void
     */
    @Scheduled(cron = "0 */1 * * * ?") // 30秒执行一次
    public void pullMegviiAlarm() throws Exception {
        megviiRpc.listPushIntelligentData();
    }


    /**
     * @Author: liwencai
     * @Description: （测试）测试修改状态
     * @Date: 2023/1/7
     * @Return: void
     */
    @Deprecated
//     @Scheduled(cron = "0/10 * * * * ?")  // 十秒执行一次
    public void test() {
        int min = 0;
        int max = 9;
        Random random = new Random();
        int i = random.nextInt(min + max) + min;
        itemAPI.updateAlarmOrFaultStatus("RLSB_TYPE_" + i, 0, null);
    }
}
