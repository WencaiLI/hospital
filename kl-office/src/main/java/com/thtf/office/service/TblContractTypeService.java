package com.thtf.office.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.office.common.response.PageResult;
import com.thtf.office.entity.TblContractType;


/**
 *  
 * 合同类别表 服务类
 *  
 *
 * @author lvgch
 * @since 2022-07-05
 */
public interface TblContractTypeService extends IService<TblContractType> {

	/**
	 * 统计合同类型数量  
	 * @return allType:类别总数；allContract:合同总数
	 * @author lvgch
	 * @date 2022-07-05
	 */
	public Map<String,Integer> countContractType();
	/**
	 * 分页查询所有合同类型
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @author lvgch
	 * @date 2022-07-05
	 */
	public PageResult<TblContractType> getAllContractTypePage(int currentPage, int pageSize);
	
	/**
	 * 查询所有合同类别
	 * @return
	 * @author lvgch
	 * @date 2022-07-12
	 */
	public List<TblContractType>getAllContractType();
	/**
	 * 保存  更新 合同类别
	 * @param entity
	 * @return
	 * @author lvgch
	 * @date 2022-07-05
	 */
	public boolean saveOrUpdateEntity(TblContractType entity);
	
	/**
	 * 删除合同类别  同时删除类别下的合同
	 * @param id
	 * @return
	 * @author lvgch
	 * @date 2022-07-05
	 */
	public boolean deleteEntityById(Long id);
	
	/**
	 * 移动多个合同 到其他类别下
	 * @param contractTypeId  移动到合同类别ID
	 * @param contractIds 合同id 逗号分隔
	 * @return
	 * @author lvgch
	 * @date 2022-07-05
	 */
	public boolean changeContractType(Long contractTypeId, String contractIds);
	
}
