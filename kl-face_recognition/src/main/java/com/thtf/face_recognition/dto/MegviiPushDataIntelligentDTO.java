package com.thtf.face_recognition.dto;

import com.thtf.face_recognition.vo.MegviiPersonInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/1/5 00:31
 * @Description: 旷世智能分析事件
 */

@Data
public class MegviiPushDataIntelligentDTO {
    /**
     * 智能分析id 必须
     */
    private String alarmRecordUuId;
    /**
     * 智能布控相机类型：1-警戒机，2-算法仓相机 必须
     */
    private Integer alarmControlType;

    /**
     * 全景图url 非必须
     */
    private String wholeImageUrl;

    /**
     * 警类型：1：人员越界，
     * 2：人员入侵，3：车辆越
     * 界，31：机动车越界，32：非机动车越界，33：机动车和非机动车越界，4：车辆禁停，41：机动车禁停，
     * 42：非机动车禁停，43:机动车和非机动车禁停，5：人员越界-翻墙检测，6：人员徘徊，7：机动车离开，
     * 8：非机动车离开，9:机动车和非机动车离开，10：人员值岗/离岗-离岗，11：人员值岗/离岗-超员，
     * 12：人员值岗/离岗-少员，13：人员奔跑，14：摔倒检测，15：人员扭打，16：抽烟检测，
     * 17：看手机，18：接打电
     * 话，19：人员聚众，20：防碰撞预警，21：车辆逆行，22：睡岗，23：物品遗留，24：携带物品，25：杂物堆放，26：物品看守，100：未佩戴安全帽报警，101：未穿戴工服报警， 102：未佩戴安全带报警， 103：火焰报警，104：烟雾报警，
     * 105：未佩戴口罩，106：未穿戴反光衣报警，107：液体泄漏检测，108：消防设施检测，109：设备指示灯异常报警，110：未佩戴绝缘手套报警，111：未佩戴气体检测仪报警， 112：登高作业-无扶梯人员报警，
     * 113：未消除静电报警，
     * 114：气瓶摆放异常报警
     */
    private Integer alarmType;

    /**
     * 设备Uuid 必须
     */
    private String deviceUuid;

    /**
     * 设备名 必须
     */
    private String deviceName;

    /**
     * 画线详情：firstNodeDetail--画线详情,第一个节点详情，secondNodeDetail--第二个节点详情,双拌线并且是预警规则时存在，
     * inTheArea--目标是否在此区域：true表示在此区域，false表示不在此区域
     */
    private String drawLineDetail;

    /**
     * 目标框在全景图中的位置：top：目标距离上方间距，
     * left：目标距离左方间距，
     * bottom：目标距离下方间 距，right：目标距离右方间距
     */
    private String targetRect;

    /**
     * 告警时间,算法仓设备为告警开始时间
     */
    private Date alarmTime;

    /**
     * 告警结束时间
     */
    private Date alarmEndTime;

    /**
     * 告警持续时间，单位：秒
     */
    private Double continueTime;

    /**
     * 报警对象的id集合
     */
    private String targetId;

    /**
     * 场景类型id：0-类型1，1-类型2，2-类型3，3-类型4...
     */
    private String sceneId;

    /**
     * 布控画线所属区域id，同一个场景下的画线规则不允许重复
     */
    private Integer areaId;

    /**
     * 算法仓类型,0.警戒 1.安监，默认警戒
     */
    private Integer arithmeticPackageType;

    /**
     * 工服颜色：1-red, 2-
     * green（无效数据）, 3-blue,4-yellow, 5-gray,-1：no
     */
    private Integer color;

    /**
     * 消防设备数量
     */
    private Integer fireEquipmentNumber;

    /**
     * 气瓶检测：1有气瓶， 2气瓶倾斜
     */
    private Integer gasCylinderFunction;

    /**
     * 设备指示灯状态颜色,如：红灯亮
     */
    private String indicatorStatusColor;

    /**
     * 布控设置的值岗离岗-人员数量，包括人员上限和人员下限;人员聚众，聚众人数
     */
    private Integer personNumber;

    /**
     * 实际报警人数，值岗离岗-人员数量，包括人员上限和人员下限;人员聚众，聚众人数
     */
    private Integer count;

    /**
     * 人员信息
     */
    private List<MegviiPersonInfo> personInfo;
}
