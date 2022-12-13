package com.thtf.face_recognition.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/7 22:49
 * @Description:
 */
@Data
public class MegviiExtIntelligentAnalysisEventVO {

    /**
     * 画线详情:
     * firstNodeDetail--画线详情，第一个节点详情,
     * secondNodeDetail--第二个节点详情,双拌线并且是预警规则时存在，
     * inTheArea-- 目标.是否在此区域:true表示在此区域，false表示不在此区域
     */
    private String drawLineDetail;


    /**
     * 目标框在全景图中的位置:top:目标距离上间距，left:目标距离左方间距，bottom:目标距离下方间距，right:目 标距离右方间距
     */
    private String targetRect;

    /**
     * 告警类型，1:人员越界，2:人员入侵，3:车辆越界，31:机动车越界，32:非机动车越界，33:机动车和非机动车越界，4:车辆禁停，41:机动车禁停,
     * 42:非机动车禁停,43:机动车和非机动车禁停，5:人员越界-翻墙检测，6:人员徘徊，7:机动车离开，8:非机动车离开，9:机动车和非机动车离开，
     * 10:人员值岗/离岗-离岗，11:人员值岗/离岗-超员，12:人员值岗/离岗-少员，13: 人员奔跑，14:摔倒检测，15: 人员扭打，16:抽烟检测，
     * 17:看手机，18:接打电话，19:人员聚众，20:防碰撞预警,21:车辆逆行，22:睡岗，23:物品遗留，24:携带物品，25:杂物堆放，26:物品看守，
     * 27:翻越闸机，28:电动车入梯，100: 未佩戴安全帽报警，101:未穿戴工服报警，102: 未佩戴安全带报警、103:火焰报警，104:烟雾报警
     * 105:未佩戴口罩，106:未穿戴反光衣报警，107:液体泄漏检测，108:消防设施检测，109:设备指示灯异常报警，110:未佩戴绝缘手套报警,
     * 111:未佩戴气体检测仪报警，112: 登高作业-无扶梯人员报警,113:未消除静电报警，114:气瓶摆放异常报警
     */

    private Integer alarmType;

    /**
     * 警戒相机类型:1-警戒机，2-算法仓相机
     */
    private String alarmControlType;

    /**
     * 布控jsonString，其中stayTime表示停留时间，delayAlarmTime表示延迟报警时间，
     * personNumber表示值岗离岗-人员数量，包括人员.上限和人员下限;人员聚众，聚众人数例如:
     * {
     *    "stayTime":1,
     *    "delayAlarmTime":60,
     *    "personNumber":1
     * }
     */
    private String ruleConfig;

    /**
     * 屏蔽区域画线，里面的firstNodeDetail代码屏蔽区域画线
     */
    private String shieldLineDetail;

    /**
     * 工服颜色: 1-red, 2-green (无效数据) , 3-blue, 4-yellow, 5-gray,-1: no
     */
    private String color;

    /**
     * 消防设备数量
     */
    private String fireEquipmentNumber;

    /**
     * 气瓶检测: 1有气瓶，2气瓶倾斜
     */
    private String gasCylinderFunction;

    /**
     * 设备指示灯状态颜色，如:红灯亮
     */
    private String indicatorStatusColor;

    /**
     * 布控设置的:值岗离岗-人员数量，包括人员上限和人员下限;人员聚众，聚众人数
     */
    private String personNumber;

    /**
     * 实际报警人数，值岗离岗-人员数量，包括人员上限和人员下限;人员聚众，聚众人数
     */
    private String personCount;
    /**
     * 人员信息
     */
    private List<MegviiPersonInfo> personInfo;
}
