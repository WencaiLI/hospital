package com.thtf.face_recognition.common.enums;

/**
 * @Author: liwencai
 * @Date: 2022/12/7 23:20
 * @Description:
 */
@SuppressWarnings("All")
public enum  MegviiAlarmTypeEnum {
    ALARM_1(1,"人员越界"),
    ALARM_2(2,"人员入侵"),
    ALARM_31(31,"非机动车越界"),
    Alarm_32(32,"机动车越界"),
    ALARM_33(33,"机动车和非机动车越界"),
    ALARM_4(4,"车辆禁停"),
    ALARM_41(41,"机动车禁停"),
    ALARM_42(42,"非机动车禁停"),
    ALARM_43(43,"机动车和非机动车禁停"),
    ALARM_5(6,"人员越界-翻墙检测"),
    ALARM_6(7,"人员徘徊"), ALARM_7(1,"机动车离开"),
    ALARM_8(8,"非机动车离开"),
    ALARM_9(9,"机动车和非机动车离开"),
    ALARM_10(10,"人员值岗/离岗-离岗"),
    ALARM_11(11,"人员值岗/离岗-超员"),
    ALARM_12(12,"人员值岗/离岗-少员"),
    ALARM_13(13,"人员奔跑"),
    ALARM_14(14,"摔倒检测"),
    ALARM_15(15,"人员扭打"),
    ALARM_16(16,"抽烟检测"),
    ALARM_17(17,"看手机"),
    ALARM_18(18,"接打电话"),
    ALARM_19(19,"人员聚众"),
    ALARM_20(20,"防碰撞预警"),
    ALARM_21(21,"车辆逆行"),
    ALARM_22(22,"睡岗"),
    ALARM_23(23,"物品遗留"),
    ALARM_24(24,"携带物品"),
    ALARM_25(25,"杂物堆放"),
    ALARM_26(26,"物品看守"),
    ALARM_27(27,"翻越闸机"),
    ALARM_28(28,"电动车入梯"),
    ALARM_100(100,"未佩戴安全帽报警"),
    ALARM_101(101,"未穿戴工服报警"),
    ALARM_102(102,"未佩戴安全带报警"),
    ALARM_103(103,"火焰报警"),
    ALARM_104(104,"烟雾报警"),
    ALARM_105(105,"未佩戴口罩"),
    ALARM_106(106,"未穿戴反光衣报警"),
    ALARM_107(107,"液体泄漏检测"),
    ALARM_108(108,"消防设施检测"),
    ALARM_109(109,"设备指示灯异常报警"),
    ALARM_110(110,"未佩戴绝缘手套报警"),
    ALARM_111(111,"未佩戴气体检测仪报警"),
    ALARM_112(112,"登高作业-无扶梯人员报警"),
    ALARM_113(113,"未消除静电报警"),
    ALARM_114(114,"气瓶摆放异常报警");

    private Integer id;
    private String desc;

    MegviiAlarmTypeEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * @Author: liwencai
     * @Description: 根据事件id获取事件描述
     * @Date: 2022/12/6
     * @Param eventTypeId:
     * @Return: java.lang.String
     */
    public static String getMegviiEventLevelDescByTypeId(Integer alarmTypeId){
        for (MegviiAlarmTypeEnum megviiAlarmTypeEnum : MegviiAlarmTypeEnum.values()) {
            if(megviiAlarmTypeEnum.getId().equals(alarmTypeId)){
                return megviiAlarmTypeEnum.getDesc();
            }
        }
        return null;
    }


}
