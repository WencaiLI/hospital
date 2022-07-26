package com.thtf.office.entity;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.io.Serializable;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 车辆信息表
    * </p>
*
* @author guola
* @since 2022-07-26
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class TblVehicleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * 车牌号
            */
    private String carNumber;

            /**
            * 关联的车辆类别id
            */
    private Long vehicleCategoryId;

            /**
            * 厂牌型号
            */
    private String model;

            /**
            * 发动机号
            */
    private String engineNumber;

            /**
            * 车架号
            */
    private String frameNumber;

            /**
            * 车身颜色
            */
    private String color;

            /**
            * 车辆照片名称
            */
    private String carImage;

            /**
            * 车辆照片url
            */
    private String carImageUrl;

            /**
            * 车辆行驶本照片名称
            */
    private String drivingBookImage;

            /**
            * 车辆行驶本照片url
            */
    private String drivingBookImageUrl;

            /**
            * 经销商
            */
    private String distributor;

            /**
            * 出厂日期
            */
    private LocalDateTime outDate;

            /**
            * 购买日期
            */
    private LocalDateTime buyDate;

            /**
            * 购买价格
            */
    private BigDecimal price;

            /**
            * 保险说明
            */
    private String insurance;

            /**
            * 维保说明
            */
    private String maintenance;

            /**
            * 描述
            */
    private String description;

            /**
            * 车辆状态 0：待命中；1：出车中；2：维修中；3：已淘汰
            */
    private Integer status;

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


}
