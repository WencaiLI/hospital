package com.thtf.office.entity;

    import java.time.LocalDateTime;
    import java.io.Serializable;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 车辆类别表
    * </p>
*
* @author guola
* @since 2022-07-26
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class TblVehicleCategory implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * 名称
            */
    private String name;

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


}
