package com.thtf.hospital.office.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 合同表
 * </p>
 *
 * @author lvgch
 * @since 2022-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TblContract implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 合同编号
	 */
	private String number;

	/**
	 * 合同名称
	 */
	private String name;

	/**
	 * 合同描述
	 */
	private String describe;

	/**
	 * 合同状态 1：待生效2：执行中：3已失效
	 */
	private Integer state;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;

	/**
	 * 合同生效时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	/**
	 * 合同终止时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	/**
	 * 合同变更
	 */
	private String change;

	/**
	 * 合同负责人ID
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long userId;

	/**
	 * 合同负责人
	 */
	private String userName;

	/**
	 * 合同名称
	 */
	private String fileName;

	/**
	 * 合同存储路径
	 */
	private String fileUrl;

	
	
	/**
	 * 合同生效前 xx天发出提醒
	 */
	private Integer startRemind ;
	/**
	 * 合同到期前xx天发出提醒
	 */
	private Integer endRemind ;
	
	/**
	 * 关联合同类别ID
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long contractTypeId;
	
	/**
	 * 合同是否有提醒事件  true  有  false 无
	 */
    @TableField(exist = false)
    private boolean remind ;

}
