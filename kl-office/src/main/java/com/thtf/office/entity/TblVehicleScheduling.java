package com.thtf.office.entity;

    import java.time.LocalDateTime;
    import java.io.Serializable;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 车辆调度表
    * </p>
*
* @author guola
* @since 2022-07-26
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class TblVehicleScheduling implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * 调度流水号
            */
    private String code;

            /**
            * 关联的车辆类别id
            */
    private Long vehicleCategoryId;

            /**
            * 描述
            */
    private String description;

            /**
            * 创建时间
            */
    private LocalDateTime createTime;

            /**
            * 创建人
            */
    private String createBy;

            /**
            * 修改时间
            */
    private LocalDateTime updateTime;

            /**
            * 修改人
            */
    private String updateBy;

            /**
            * 删除时间
            */
    private LocalDateTime deleteTime;

            /**
            * 删除人
            */
    private String deleteBy;

            /**
            * 关联的车辆信息id
            */
    private Long vehicleInfoId;

            /**
            * 车牌号
            */
    private String carNumber;

            /**
            * 调度开始时间
            */
    private LocalDateTime startTime;

            /**
            * 调度结束时间
            */
    private LocalDateTime endTime;

            /**
            * 司机
            */
    private String driverName;

            /**
            * 调度用途 0：出车；1：维保；2：淘汰
            */
    private Integer purpose;

            /**
            * 关联的使用部门id
            */
    private Long organizationId;

            /**
            * 使用人姓名
            */
    private String userName;

            /**
            * 目的地
            */
    private String destination;

            /**
            * 司机关联的用户id
            */
    private Long driverId;

            /**
            * 关联的使用部门名称
            */
    private String organizationName;


}
