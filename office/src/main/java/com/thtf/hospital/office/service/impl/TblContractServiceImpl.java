package com.thtf.hospital.office.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.thtf.common.response.PageResult;
import com.thtf.common.util.DateUtil;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.common.util.PageUtil;
import com.thtf.hospital.office.dto.TblContractDTO;
import com.thtf.hospital.office.dto.TblContractRelationDTO;
import com.thtf.hospital.office.dto.TblContractRemindDTO;
import com.thtf.hospital.office.entity.TblContract;
import com.thtf.hospital.office.entity.TblContractRelation;
import com.thtf.hospital.office.entity.TblContractRemind;
import com.thtf.hospital.office.mapper.TblContractMapper;
import com.thtf.hospital.office.mapper.TblContractRelationMapper;
import com.thtf.hospital.office.mapper.TblContractRemindMapper;
import com.thtf.hospital.office.service.TblContractRelationService;
import com.thtf.hospital.office.service.TblContractRemindService;
import com.thtf.hospital.office.service.TblContractService;

/**
 * 
 * 合同表 服务实现类
 * 
 *
 * @author lvgch
 * @since 2022-07-05
 */
@Service
public class TblContractServiceImpl extends ServiceImpl<TblContractMapper, TblContract> implements TblContractService {
	@Autowired
	private TblContractMapper contractMapper;

	@Autowired
	private TblContractRelationMapper contractRelationMapper;

	@Autowired
	private TblContractRemindMapper contractRemindMapper;

	@Autowired
	private TblContractRelationService contractRelationService;

	@Autowired
	private TblContractRemindService contractRemindService;

	@Autowired
	private IdGeneratorSnowflake idGeneratorSnowflake;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thtf.service.TblContractService#countContract()
	 */

	@Override
	public Map<String, Integer> countContract() {
		int all = this.count();
		LambdaQueryWrapper<TblContract> query = Wrappers.lambdaQuery();
		query.eq(TblContract::getState, 2);
		int doing = this.count(query);
		Map<String, Integer> map = new HashMap<>();
		map.put("all", all);
		map.put("doing", doing);
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thtf.service.TblContractService#getAllContractTypePage(java.lang.Long,
	 * java.lang.Integer, java.lang.String, int, int)
	 */
	@Override
	public PageResult<TblContract> getAllContractPage(Long contractTypeId, Integer state, String keyWord,
			int currentPage, int pageSize) {
		this.updateContractState();
		this.actionRemind();
		LambdaQueryWrapper<TblContract> queryWrapper = Wrappers.lambdaQuery();
		if (contractTypeId != null) {
			queryWrapper.eq(TblContract::getContractTypeId, contractTypeId);
		}
		if (state != null) {
			queryWrapper.eq(TblContract::getState, state);
		}
		if (!StringUtils.isEmpty(keyWord)) {
			queryWrapper.like(TblContract::getNumber, keyWord).or().like(TblContract::getName, keyWord).or()
					.like(TblContract::getDescribe, keyWord);
		}
		IPage<TblContract> pageResult = this.contractMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
		// 查询是否有提醒事件
		List<TblContract> list = pageResult.getRecords();
		for (TblContract entity : list) {
			LambdaQueryWrapper<TblContractRemind> query = Wrappers.lambdaQuery();
			query.eq(TblContractRemind::getContractId, entity.getId());
			Integer[] args = { 1, 3 };
			query.in(TblContractRemind::getState, Arrays.asList(args));
			int count = contractRemindMapper.selectCount(query);
			if (count > 0) {
				entity.setRemind(true);
			} else {
				entity.setRemind(false);
			}
		}
		return PageUtil.buildPageResult(pageResult, pageSize);
	}

	/**
	 * 
	 * 查询 设置所有合同的提醒事件
	 * 
	 * @author lvgch
	 * @date 2022-07-24
	 */
	private void actionRemind() {
		// 1 合同生效前 xx天发出提醒 合同到期前xx天发出提醒

		// 2 当前时间月下次处置时间对比

		LambdaQueryWrapper<TblContract> queryWrapper = Wrappers.lambdaQuery();
		Integer[] args = { 1, 2 };
		queryWrapper.in(TblContract::getState, Arrays.asList(args));
		List<TblContract> list = this.list(queryWrapper);
		List<TblContractRemind> remindUpdateList = new ArrayList<>();
		// 标识是否有合同生效前 xx天发出提醒 合同到期前xx天发出提醒
		boolean startRemindFlag = true;
		boolean endRemindFlag = true;
		for (TblContract entity : list) {
			LambdaQueryWrapper<TblContractRemind> query = Wrappers.lambdaQuery();
			query.eq(TblContractRemind::getContractId, entity.getId());
			List<TblContractRemind> remindList = this.contractRemindMapper.selectList(query);
			for (TblContractRemind remind : remindList) {
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime time = remind.getNextRemindDate();
				long day = DateUtil.betweenTwoTime(now, time, ChronoUnit.DAYS);
				if (day >= 0) {
					remind.setState(1);
					remindUpdateList.add(remind);
				}
				if (remind.getTopic().contains("合同生效前")) {
					startRemindFlag = false;

				} else if (remind.getTopic().contains("合同到期前")) {
					endRemindFlag = false;
				}
			}

			if (startRemindFlag) { // 无 合同生效前 xx天发出提醒
				Integer day = entity.getStartRemind();
				TblContractRemind remind = new TblContractRemind();
				remind.setId(this.idGeneratorSnowflake.snowflakeId());
				remind.setContractId(entity.getId());
				remind.setTopic("合同生效前提醒");
				remind.setTopic("合同生效前" + day + "提醒");

				LocalDateTime now = LocalDateTime.now();
				LocalDateTime start = entity.getStartTime();
				long num = DateUtil.betweenTwoTime(start, now, ChronoUnit.DAYS);
				if (num > day) {
					remind.setState(0);
				} else {
					remind.setState(1);
				}
				remind.setNextRemindDate(DateUtil.minu(start, day, ChronoUnit.DAYS));
				this.contractRemindService.save(remind);
			}
			if (endRemindFlag) { // 无 合同到期前xx天发出提醒
				Integer day = entity.getEndRemind();
				TblContractRemind remind = new TblContractRemind();
				remind.setId(this.idGeneratorSnowflake.snowflakeId());
				remind.setContractId(entity.getId());
				remind.setTopic("合同到期前提醒");
				remind.setTopic("合同到期前" + day + "提醒");

				LocalDateTime now = LocalDateTime.now();
				LocalDateTime end = entity.getEndTime();
				long num = DateUtil.betweenTwoTime(end, now, ChronoUnit.DAYS);
				if (num > day) {
					remind.setState(0);
				} else {
					remind.setState(1);
				}
				remind.setNextRemindDate(DateUtil.minu(end, day, ChronoUnit.DAYS));
				this.contractRemindService.save(remind);
			}
		}
		if (!remindUpdateList.isEmpty()) {
			this.contractRemindService.updateBatchById(remindUpdateList);
		}
	}

	private void updateContractState() {
		List<TblContract> list = this.list();
		for (TblContract entity : list) {
			LocalDateTime start = entity.getStartTime();
			LocalDateTime end = entity.getEndTime();
			LocalDateTime now = LocalDateTime.now();
			long day1 = DateUtil.betweenTwoTime(now, start, ChronoUnit.DAYS);
			long day2 = DateUtil.betweenTwoTime(now, end, ChronoUnit.DAYS);
			if (day1 < 0) {
				entity.setState(1);
			} else if (day1 > 0 && day2 <= 0) {
				entity.setState(2);
			} else {
				entity.setState(3);
			}
		}
		this.updateBatchById(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thtf.service.TblContractService#saveOrUpdateEntity(com.thtf.dto.
	 * TblContractDTO)
	 */
	@Override
	public boolean saveOrUpdateEntity(TblContractDTO dto) {
		// 查询统一的合同开始xx天结束xxx天提醒事件
		// 判断合同执行状态 更新状态信息
		TblContract entity = new TblContract();
		BeanUtils.copyProperties(dto, entity);
		if (entity.getId() != null) {
			// 删除关联的合同提醒事件，合同关联方
			LambdaQueryWrapper<TblContractRelation> query1 = Wrappers.lambdaQuery();
			query1.eq(TblContractRelation::getContractId, entity.getId());
			this.contractRelationMapper.delete(query1);

			LambdaQueryWrapper<TblContractRemind> query2 = Wrappers.lambdaQuery();
			query2.eq(TblContractRemind::getContractId, entity.getId());
			this.contractRemindMapper.delete(query2);
			entity.setUpdateTime(LocalDateTime.now());

		} else {
			entity.setId(this.idGeneratorSnowflake.snowflakeId());
			entity.setCreateTime(LocalDateTime.now());
			List<TblContract> list = this.list();
			if (!list.isEmpty()) {
				TblContract contract = list.get(0);
				if (contract.getStartRemind() != null) {
					entity.setStartRemind(contract.getStartRemind());
				}
				if (contract.getEndRemind() != null) {
					entity.setEndRemind(contract.getEndRemind());
				}
			}
		}
		this.saveOrUpdate(entity); // 合同
		// 合同关联方
		List<TblContractRelationDTO> relationDTOList = dto.getRelationList();

		List<TblContractRelation> relationList = new ArrayList<>();
		for (TblContractRelationDTO relationDTO : relationDTOList) {
			TblContractRelation relation = new TblContractRelation();
			relation.setId(this.idGeneratorSnowflake.snowflakeId());
			relation.setContractId(entity.getId());
			relation.setRelationType(relationDTO.getRelationType());
			relation.setRelationName(relationDTO.getRelationName());
			relationList.add(relation);
		}
		if (!relationList.isEmpty()) {
			this.contractRelationService.saveBatch(relationList);
		}
		// 合同提醒事件
		List<TblContractRemindDTO> remindDTOList = dto.getRemindList();
		List<TblContractRemind> remindList = new ArrayList<>();
		for (TblContractRemindDTO remindDTO : remindDTOList) {
			TblContractRemind remind = new TblContractRemind();

			BeanUtils.copyProperties(remindDTO, remind);
			remind.setContractId(entity.getId());
			remind.setId(this.idGeneratorSnowflake.snowflakeId());
			remind.setState(0);
			// 设置首次提醒时间
			String unit = remind.getUnit();
			int period = Integer.parseInt(remind.getPeriod());
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime time = LocalDateTime.now();
			if (unit.equals("day")) {
				int hour = 24 / period;
				time = DateUtil.plus(now, hour, ChronoUnit.HOURS);
			}

			if (unit.equals("month")) {
				int day = 30 / period;
				time = DateUtil.plus(now, day, ChronoUnit.HOURS);
			}
			if (unit.equals("year")) {
				int day = 365 / period;
				time = DateUtil.plus(now, day, ChronoUnit.HOURS);
			}
			remind.setTime(time);
			remind.setNextRemindDate(time);
			remindList.add(remind);
		}
		if (!remindList.isEmpty()) {
			this.contractRemindService.saveBatch(remindList);
		}
		updateContractState();
		this.actionRemind();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thtf.service.TblContractService#deleteEnityById(java.lang.Long)
	 */
	@Override
	public boolean deleteEnityById(Long id) {
		// 删除提醒事件
		// 删除 关联信息
		Map<String, Object> map = new HashMap<>();
		map.put("contract_id", id);
		this.contractRemindMapper.deleteByMap(map);
		this.contractRelationMapper.deleteByMap(map);
		return this.removeById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thtf.service.TblContractService#getEntityById(java.lang.Long)
	 */
	@Override
	public TblContractDTO getEntityById(Long id) {
		TblContractDTO dto = new TblContractDTO();
		TblContract entity = this.getById(id);
		BeanUtils.copyProperties(entity, dto);

		List<TblContractRemindDTO> remindDTOList = new ArrayList<>();
		List<TblContractRelationDTO> relationDTOList = new ArrayList<>();
		dto.setRelationList(relationDTOList);
		dto.setRemindList(remindDTOList);
		Map<String, Object> map = new HashMap<>();
		map.put("contract_id", id);
		List<TblContractRelation> relationList = this.contractRelationMapper.selectByMap(map);
		for (TblContractRelation relation : relationList) {
			TblContractRelationDTO relationDto = new TblContractRelationDTO();
			BeanUtils.copyProperties(relation, relationDto);
			relationDTOList.add(relationDto);
		}
		List<TblContractRemind> remindList = this.contractRemindMapper.selectByMap(map);
		for (TblContractRemind remind : remindList) {
			TblContractRemindDTO remindDto = new TblContractRemindDTO();
			BeanUtils.copyProperties(remind, remindDto);
			remindDTOList.add(remindDto);
		}
		return dto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thtf.service.TblContractService#setContractRemind(java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public boolean setContractRemind(Integer startRemind, Integer endRemind) {
		// Map<String, Object> map = new HashMap<>();
		// map.put("start_remind", startRemind);
		// map.put("end_remind", endRemind);
		TblContract entity = new TblContract();
		entity.setStartRemind(startRemind);
		entity.setEndRemind(endRemind);
		this.update(entity, null);
		this.actionRemind();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thtf.adminserver.service.TblContractService#
	 * searchRemindListPageByContractId(java.lang.Long, int, int)
	 */
	@Override
	public PageResult<TblContractRemind> searchRemindListPageByContractId(Long contractId, int currentPage,
			int pageSize) {
		LambdaQueryWrapper<TblContractRemind> queryWrapper = Wrappers.lambdaQuery();
		if (contractId != null) {
			queryWrapper.eq(TblContractRemind::getContractId, contractId);
		}

		IPage<TblContractRemind> pageResult = this.contractRemindMapper.selectPage(new Page<>(currentPage, pageSize),
				queryWrapper);
		return PageUtil.buildPageResult(pageResult, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thtf.adminserver.service.TblContractService#updateContractRemind(com.thtf
	 * .common.dto.adminserver.TblContractRemindDTO)
	 */
	@Override
	public boolean updateContractRemind(TblContractRemindDTO dto) {
		TblContractRemind entity = this.contractRemindMapper.selectById(dto.getId());
		if (dto.getState() == 2) { // 已读 已处置 计算下一次处置时间
			String unit = entity.getUnit();
			int period = Integer.parseInt(entity.getPeriod());
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime nextTime = LocalDateTime.now();
			if (unit.equals("day")) {
				int hour = 24 / period;
				nextTime = DateUtil.plus(now, hour, ChronoUnit.HOURS);
			}

			if (unit.equals("month")) {
				int day = 30 / period;
				nextTime = DateUtil.plus(now, day, ChronoUnit.HOURS);
			}
			if (unit.equals("year")) {
				int day = 365 / period;
				nextTime = DateUtil.plus(now, day, ChronoUnit.HOURS);
			}
			entity.setNextRemindDate(nextTime);
		} else if (dto.getState() == 3) {
			entity.setNextRemindDate(dto.getNextRemindDate()); // 延迟提醒时间
		}
		entity.setState(dto.getState());
		this.contractRemindMapper.updateById(entity);
		return true;
	}

	@Override
	public PageResult<TblContractRemind> getContractRemindByUserId(Long userId, int currentPage, int pageSize) {
		this.actionRemind();
		LambdaQueryWrapper<TblContractRemind> queryWrapper = Wrappers.lambdaQuery();
		if (userId != null) {
			queryWrapper.eq(TblContractRemind::getUserId, userId);
		}

		IPage<TblContractRemind> pageResult = this.contractRemindMapper.selectPage(new Page<>(currentPage, pageSize),
				queryWrapper);
		return PageUtil.buildPageResult(pageResult, pageSize);
	}
}
