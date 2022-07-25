package com.thtf.hospital.office.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.hospital.office.entity.TblContractRemind;
import com.thtf.common.response.PageResult;

/**
 * <p>
 *  合同相关提醒表 服务类
 * </p>
 *
 * @author lvgch
 * @since 2022-07-05
 */
public interface TblContractRemindService extends IService<TblContractRemind> {
	
	/**
	 * 用户id  查询我的提醒事件
	 * @param userId
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	PageResult<TblContractRemind> getContractRemindByUserId(Long userId,int currentPage, int pageSize);
	


}
