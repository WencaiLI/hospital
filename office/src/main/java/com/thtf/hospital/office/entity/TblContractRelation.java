package com.thtf.hospital.office.entity;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 合同关联方
 * </p>
 *
 * @author lvgch
 * @since 2022-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TblContractRelation implements Serializable {

	private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

	/**
	 * 签约类型
	 */
	private String relationType;

	/**
	 * 签约单位
	 */
	private String relationName;

	/**
	 * 关联合同主键
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long contractId;

}
