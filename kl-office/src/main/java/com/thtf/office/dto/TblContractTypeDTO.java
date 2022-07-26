package com.thtf.office.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 合同类别表
 * </p>
 *
 * @author lvgch
 * @since 2022-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TblContractTypeDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String describe;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

}
