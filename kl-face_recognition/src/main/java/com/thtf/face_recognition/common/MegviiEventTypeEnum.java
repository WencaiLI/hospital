package com.thtf.face_recognition.common;

/**
 * @Author: liwencai
 * @Date: 2022/12/6 15:58
 * @Description:
 */
@SuppressWarnings("All")
public enum MegviiEventTypeEnum {

    // 人员管控
    EVENT_100(100L,"人员管控"),
    EVENT_101(101L,"重点人员"),
    EVENT_102(102L,"全局陌生人"),
    EVENT_103(103L,"高频陌生人"),
    EVENT_104(104L,"员工"),
    EVENT_105(105L,"访客"),
    EVENT_106(106L,"通道陌生人"),

    // 车辆管控
    EVENT_200(200L,"车辆管控"),
    EVENT_201(201L,"重点车辆"),
    EVENT_202(202L,"陌生车辆"),
    EVENT_203(203L,"已知车辆"),
    EVENT_204(204L,"访客车辆"),
    EVENT_205(205L,"员工车辆"),

    // 区域警戒
    EVENT_300(300L,"区域警戒"),
    EVENT_301(301L,"人员入侵"),
    EVENT_302(302L,"人员越界"),
    EVENT_303(303L,"车辆越界"),
    EVENT_304(304L,"车辆禁停"),
    EVENT_305(305L,"人员越界-翻墙检测"),
    EVENT_306(306L,"机动车越界"),
    EVENT_307(307L,"非机动车越界"),
    EVENT_308(308L,"机动车禁停"),
    EVENT_309(309L,"非机动车禁停"),
    EVENT_310(310L,"机动车离开"),
    EVENT_311(311L,"非机动车离开"),
    EVENT_312(312L,"人员徘徊"),
    EVENT_313(313L,"人员值岗/离岗"),
    EVENT_314(314L,"人员奔跑"),
    EVENT_315(315L,"人员摔倒"),
    EVENT_316(316L,"人员扭打"),
    EVENT_317(317L,"抽烟"),
    EVENT_318(318L,"看手机"),
    EVENT_319(319L,"接打电话"),
    EVENT_320(320L,"人员聚众"),
    EVENT_321(321L,"车辆逆行"),
    EVENT_322(322L,"防碰撞预警"),
    EVENT_323(323L,"人员值岗/离岗/超员"),
    EVENT_324(324L,"人员值岗/离岗/少员"),
    EVENT_325(325L,"睡岗检测"),
    EVENT_326(326L,"物品遗留"),
    EVENT_327(327L,"携带物品"),
    EVENT_328(328L,"杂物堆放"),
    EVENT_329(329L,"物品看守"),
    EVENT_330(330L,"翻越闸机"),
    EVENT_331(331L,"电动车入梯"),

    // 设备事件 父类型，不会产生事件，手误没有和其他保持一致
    EVENT_4000(4000L,"设备事件"),
    // 父类型，不会产生事件，
    EVENT_4001(4001L,"所有设备"),
    // 父类型，不会产生事件，
    EVENT_4002(4002L,"设备离线"),
    EVENT_400(400L,"门禁设备"),
    EVENT_401(401L,"设备拆除"),

    EVENT_402(402L,"门磁超时"),

    EVENT_403(403L,"密码破解"),
    EVENT_404(404L,"消防告警"),
    EVENT_405(405L,"存储空间告警"),
    EVENT_406(406L,"测温模块失效"),
    EVENT_407(407L,"人员底库超限"),
    EVENT_408(408L,"门被外力开启"),
    EVENT_16(16L,"密码破解"),

    // 出入事件 父类型，不会产生事件
    EVENT_500(500L,"出入事件"),
    EVENT_501(501L,"重点人员出入事件"),
    EVENT_502(502L,"高温事件"),
    EVENT_503(503L,"通道陌生人出入事件"),
    EVENT_504(504L,"健康吗检测未通过出入事件"),
    EVENT_505(505L,"未佩戴口罩报警事件"),

    // 生产安监
    EVENT_600(600L,"生产安监"),
    EVENT_601(601L,"未佩戴安全帽"),
    EVENT_602(602L,"未穿戴工服"),
    EVENT_603(603L,"未佩戴安全带"),
    EVENT_604(604L,"火焰报警"),
    EVENT_605(605L,"烟雾报警"),
    EVENT_606(606L,"未佩戴口罩"),
    EVENT_607(607L,"为穿戴反光衣报警"),
    EVENT_608(608L,"液体泄露检测"),
    EVENT_609(609L,"消防设施检测"),
    EVENT_610(610L,"设备指示灯异常报警"),
    EVENT_611(611L,"未佩戴绝缘手套报警"),
    EVENT_612(612L,"未佩戴气体检测仪报警"),
    EVENT_613(613L,"登高作业-无扶梯人员报警"),
    EVENT_614(614L,"未消除静电报警"),
    EVENT_615(615L,"气瓶摆放异常报警"),

    // 人数统计事件
    EVENT_700(700L,"人数统计事件"),
    EVENT_701(701L,"人数滞留超员"),
    EVENT_702(702L,"人员过密");

    private Long id;
    private String desc;
    MegviiEventTypeEnum(Long id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Long getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * @Author: liwencai
     * @Description: 根据事件id获取事件
     * @Date: 2022/12/6
     * @Param eventTypeId:
     * @Return: com.thtf.face_recognition.common.MegviiEventTypeEnum
     */
    public static MegviiEventTypeEnum getMegviiEventTypeEnumByTypeId(String eventTypeId){
        for (MegviiEventTypeEnum envMonitorItemLiveParameterEnum : MegviiEventTypeEnum.values()) {
            if(envMonitorItemLiveParameterEnum.getId().equals(eventTypeId)){
                return envMonitorItemLiveParameterEnum;
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }

    /**
     * @Author: liwencai
     * @Description: 根据事件id获取事件描述
     * @Date: 2022/12/6
     * @Param eventTypeId:
     * @Return: java.lang.String
     */
    public static String getMegviiEventTypeDescByTypeId(String eventTypeId){
        for (MegviiEventTypeEnum envMonitorItemLiveParameterEnum : MegviiEventTypeEnum.values()) {
            if(envMonitorItemLiveParameterEnum.getId().equals(eventTypeId)){
                return envMonitorItemLiveParameterEnum.getDesc();
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }
}
