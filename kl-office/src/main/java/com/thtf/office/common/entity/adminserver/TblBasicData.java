package com.thtf.office.common.entity.adminserver;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 基础数据
 *
 * @author 邓玉磊
 * @since 2020-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TblBasicData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 基础数据编码
     */
    private String basicCode;

    /**
     * 基础数据名称
     */
    private String basicName;


}
