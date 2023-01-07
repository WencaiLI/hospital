package com.thtf.office.service.impl;

import com.thtf.face_recognition.dto.MegviiPushDataIntelligentDTO;
import com.thtf.face_recognition.service.impl.MegviiApiServiceImpl;
import com.thtf.office.OfficeServerMain9001;
import com.thtf.office.service.TblVehicleSchedulingService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = OfficeServerMain9001.class,webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TblVehicleSchedulingServiceImplTest {

    @Autowired
    MegviiApiServiceImpl megviiApiService;

    @Test
    public void x(){
        String jsonString = "{\n" +
                " \"alarmControlType\":1,\n" +
                " \"alarmEndTime\":1621497491318,\n" +
                " \"alarmRecordUuId\":\"5ce33c9accb94a978976b232958bdc88\",\n" +
                " \"alarmTime\":1621497491318,\n" +
                " \"alarmType\":1,\n" +
                " \"areaId\":0,\n" +
                " \"continueTime\":\"3.0\",\n" +
                " \"deviceName\":\"警ᡂ㇇⌅仓-车辆虚拟⍱\",\n" +
                " \"deviceUuid\":\"5ce33c9accb94a978976b232958bdc88\",\n" +
                " \"drawLineDetail\":\"[{\\\"firstNodeDetail\\\":\\\" [{\\\"x\\\":0.3242320819112628,\\\"y\n" +
                " \"targetId\":\"[\\\"person:fe0847ae-d004-11eb-9950-a4bf010cfe8d:10\\\"]\",\n" +
                " \"targetRect\":\"[{\\\"bottom\\\":82.376396,\\\"left\\\":46.54868,\\\"right\\\":51.40949,\\\"top\\\":\n" +
                " \"wholeImageUrl\":\"_ZzEwMF8zbQ==_455a8508b17b4683b49e3c78ba15ea26\",\n" +
                " \"arithmeticPackageType\":1,\n" +
                " \"color\":1,\n" +
                " \"fireEquipmentNumber\":3,\n" +
                " \"gasCylinderFunction\":2,\n" +
                " \"indicatorStatusColor\":\"㓒灯亮\"\n" +
                "}\n";
        MegviiPushDataIntelligentDTO megviiPushDataIntelligentDTO = megviiApiService.jsonToMegviiPushDataIntelligentDTO(jsonString);
        System.out.println(megviiPushDataIntelligentDTO);
        // return;
    }
    
}