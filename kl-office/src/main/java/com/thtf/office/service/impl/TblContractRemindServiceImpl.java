package com.thtf.office.service.impl;

import com.thtf.office.common.response.PageResult;
import com.thtf.office.common.util.PageUtil;
import com.thtf.office.entity.TblContractRemind;
import com.thtf.office.mapper.TblContractRemindMapper;
import com.thtf.office.service.TblContractRemindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



/**
 * <p>
 *  合同相关提醒表 服务实现类
 * </p>
 *
 * @author lvgch
 * @since 2022-07-05
 */
@Service
public class TblContractRemindServiceImpl extends ServiceImpl<TblContractRemindMapper, TblContractRemind> implements TblContractRemindService {
	@Autowired
	private TblContractRemindMapper contractRemindMapper ;
	/* (non-Javadoc)
	 * @see com.thtf.adminserver.service.TblContractRemindService#getContractRemindByUserId(java.lang.Long)
	 */
	@Override
	public PageResult<TblContractRemind> getContractRemindByUserId(Long userId, int currentPage, int pageSize) {
	
		     LambdaQueryWrapper<TblContractRemind> queryWrapper = Wrappers.lambdaQuery();
		     if(userId!=null) {
		    	 queryWrapper.eq(TblContractRemind::getUserId, userId);
		     }
		   
		     IPage<TblContractRemind> pageResult = this.contractRemindMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
			return PageUtil.buildPageResult(pageResult, pageSize);
	}

}
