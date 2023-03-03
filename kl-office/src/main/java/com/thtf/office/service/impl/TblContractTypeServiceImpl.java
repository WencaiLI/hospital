package com.thtf.office.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.thtf.common.response.PageResult;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.common.util.PageUtil;
import com.thtf.office.entity.TblContract;
import com.thtf.office.entity.TblContractType;
import com.thtf.office.mapper.TblContractMapper;
import com.thtf.office.mapper.TblContractTypeMapper;
import com.thtf.office.service.TblContractTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;


/**
 * 
 * 合同类别表 服务实现类
 * 
 *
 * @author lvgch
 * @since 2022-07-05
 */
@Service
public class TblContractTypeServiceImpl extends ServiceImpl<TblContractTypeMapper, TblContractType> implements TblContractTypeService {
	@Resource
	private TblContractTypeMapper contractTypeMapper ;
	
	@Resource
	private TblContractMapper contractMapper ;
	
    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

	/* (non-Javadoc)
	 * @see com.thtf.service.TblContractTypeService#countContractType()
	 */
	@Override
	public Map<String, Integer> countContractType() {
		Map<String, Integer> hashMap = new HashMap<String, Integer>();
		int allType = 0;
		int allContract = 0;
		List<TblContractType> list = contractTypeMapper.selectList(null);
		if(!list.isEmpty()) {
			allType = list.size();
			List<Long> idList = list.stream().map(TblContractType::getId).collect(Collectors.toList());
			LambdaQueryWrapper<TblContract> query =  Wrappers.lambdaQuery();
			query.in(TblContract::getContractTypeId, idList);
			allContract = this.contractMapper.selectCount(query);
		}
		hashMap.put("allType", allType);
		hashMap.put("allContract", allContract);
		return hashMap;
	}

	/* (non-Javadoc)
	 * @see com.thtf.service.TblContractTypeService#getAllContractTypePage(int, int)
	 */
	@Override
	public PageResult<TblContractType> getAllContractTypePage(int currentPage, int pageSize) {
		    Wrapper<TblContractType> queryWrapper = Wrappers.<TblContractType>lambdaQuery();
	        IPage<TblContractType> pageResult = contractTypeMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
	        // 统计合同类别下的合同数量
	        List<TblContractType> list =  pageResult.getRecords();
	        if(!list.isEmpty()) {
	        	for(TblContractType entity : list) {
	        		LambdaQueryWrapper<TblContract> query = Wrappers.lambdaQuery();
	        		query.eq(TblContract::getContractTypeId, entity.getId());
	        		entity.setContractNum(this.contractMapper.selectCount(query));
	        	}
	        }
	        return PageUtil.buildPageResult(pageResult, pageSize);
	}

	/* (non-Javadoc)
	 * @see com.thtf.service.TblContractTypeService#saveOrUpdateEntity(com.thtf.entity.TblContractType)
	 */
	@Override
	public boolean saveOrUpdateEntity(TblContractType entity) {
	    if(entity.getId() == null) {
	    	entity.setId(this.idGeneratorSnowflake.snowflakeId());
	    }
		return this.saveOrUpdate(entity);
	}

	/* (non-Javadoc)
	 * @see com.thtf.service.TblContractTypeService#deleteEntityById(java.lang.Long)
	 */
	@Override
	public boolean deleteEntityById(Long id) {
		// 删除关联的合同列表
		LambdaQueryWrapper<TblContract> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.eq(TblContract::getContractTypeId, id);
		this.contractMapper.delete(queryWrapper);
		return this.removeById(id);
	}

	/* (non-Javadoc)
	 * @see com.thtf.service.TblContractTypeService#changeContractType(java.lang.Long, java.lang.String)
	 */
	@Override
	public boolean changeContractType(Long contractTypeId, String contractIds) {
		List<Long> ids = new ArrayList<>();
		String args []  = contractIds.split(",");
		for(String str : args) {
			ids.add(Long.parseLong(str));
		}
		LambdaQueryWrapper<TblContract> updateWrapper = Wrappers.lambdaQuery();
		updateWrapper.in(TblContract::getContractTypeId, ids);
		TblContract  entity = new TblContract();
		entity.setContractTypeId(contractTypeId);
		this.contractMapper.update(entity, updateWrapper);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.thtf.service.TblContractTypeService#getAllContractType()
	 */
	@Override
	public List<TblContractType> getAllContractType() {
		 return this.list();
	}

}
