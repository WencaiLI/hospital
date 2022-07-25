package com.thtf.hospital.office.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import com.thtf.common.response.PageResult;
import com.thtf.hospital.office.dto.TblContractDTO;
import com.thtf.hospital.office.dto.TblContractRemindDTO;
import com.thtf.hospital.office.entity.TblContract;
import com.thtf.hospital.office.entity.TblContractRemind;

/**
 * 
 * 合同表 服务类
 * 
 *
 * @author lvgch
 * @since 2022-07-05
 */
public interface TblContractService extends IService<TblContract> {

	/**
	 * 表头总览 
	 * @return all:合同总数  doing:执行中合同
	 * @author lvgch
	 * @date 2022-07-06
	 */
	public Map<String,Integer> countContract();
	/**
	 * 分页 按条件查询合同列表
	 * @param contractType 合同类别
	 * @param state  合同状态
	 * @param keyWord  关键词
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @author lvgch
	 * @date 2022-07-06
	 */
	PageResult<TblContract> getAllContractPage(Long contractTypeId,Integer state , String keyWord,int currentPage, int pageSize);
	/**
	 * 保存/ 更新
	 * @param dto 实体DTO
	 * @return
	 * @author lvgch
	 * @date 2022-07-06
	 */
	boolean saveOrUpdateEntity(TblContractDTO dto);
	/**
	 * 主键删除
	 * @param id 
	 * @return
	 * @author lvgch
	 * @date 2022-07-06
	 */
	boolean deleteEnityById(Long id);
	
	/**
	 * 主键查询
	 * @param id 
	 * @return
	 * @author lvgch
	 * @date 2022-07-06
	 */
	TblContractDTO getEntityById(Long id);
	/**
	 * 统一设置合同xx天开始  xx天结束提醒
	 * @param startRemind  xxx 天开始执行合同
	 * @param endRemind    xxx天 合同到期
	 * @return
	 * @author lvgch
	 * @date 2022-07-13
	 */
	boolean setContractRemind(Integer startRemind,Integer endRemind);
	
	/**
	 * 合同id 分页查询提醒事件
	 * @param contractId
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @author lvgch
	 * @date 2022-07-14
	 */
	PageResult<TblContractRemind> searchRemindListPageByContractId(Long contractId,int currentPage, int pageSize);
	
	/**
	 * 提醒事件处置
	 * @param dto
	 * @return
	 * @author lvgch
	 * @date 2022-07-14
	 */
	boolean  updateContractRemind(TblContractRemindDTO dto);
	
	/**
	 * 用户id  查询我的提醒事件
	 * @param userId
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	PageResult<TblContractRemind> getContractRemindByUserId(Long userId,int currentPage, int pageSize);
	
}
