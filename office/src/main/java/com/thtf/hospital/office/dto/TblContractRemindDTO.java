package com.thtf.hospital.office.dto;

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
 * 合同相关提醒表
 * </p>
 *
 * @author lvgch
 * @since 2022-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TblContractRemindDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 提醒主题
	 */
	private String topic;

	/**
	 * 执行负责人ID
	 */
	private Long userId;

	/**
	 * 执行负责人名称
	 */
	private String userName;

	/**
	 * 提醒内容
	 */
	private String content;

	/**
	 * 首次提醒时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime time;

	/**
	 * 提醒周期
	 */
	private String period;

	/**
	 * 提醒单位 year month day
	 */
	private String unit;
	/**
	 * 0 : 正常 1 待处置2：已处置3:延时中
	 */
	private Integer state;
	/**
	 * 下次提醒时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime nextRemindDate;
	/**
	 * 关联合同主键
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long  contractId;

}
