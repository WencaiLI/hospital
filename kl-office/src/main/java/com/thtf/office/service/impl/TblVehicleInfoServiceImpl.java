package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.util.IdGeneratorSnowflake;
import com.thtf.office.common.util.SplitListUtil;
import com.thtf.office.dto.VehicleInfoConvert;
import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.service.TblVehicleInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.office.vo.VehicleSelectByDateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 车辆信息表 服务实现类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Service
public class TblVehicleInfoServiceImpl extends ServiceImpl<TblVehicleInfoMapper, TblVehicleInfo> implements TblVehicleInfoService {

    @Resource
    TblVehicleInfoMapper vehicleInfoMapper;

    @Resource
    VehicleInfoConvert vehicleInfoConvert;

    @Autowired
    IdGeneratorSnowflake idGeneratorSnowflake;

    /**
     * @Author: liwencai
     * @Description: 新增公车信息
     * @Date: 2022/7/27
     * @Param vehicleInfo:
     * @return: boolean
     */
    @Override
    @Transactional
    public boolean insert(TblVehicleInfo vehicleInfo) {
        vehicleInfo.setId(this.idGeneratorSnowflake.snowflakeId());
        // 车牌号不能相同
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("car_number",vehicleInfo.getCarNumber());
        List<TblVehicleInfo> infoList = vehicleInfoMapper.selectList(queryWrapper);
        if (infoList.size() == 1){
            return false;
        }
        /* 设置初始值 状态设置为待命中，创建日期设置为当前日期 */
        vehicleInfo.setStatus(0);
        vehicleInfo.setCreateTime(LocalDateTime.now());
        // todo vehicleInfo.setCreateBy();
        return vehicleInfoMapper.insert(vehicleInfo)== 1;
    }

    /**
     * @Author: liwencai
     * @Description: 批量导入公车信息
     * @Date: 2022/7/27
     * @Param list:
     * @return: boolean
     */
    @Override
    public boolean insertBatch(List<TblVehicleInfo> list)  {
        if(list.size() == 0) {
            return false;
        }
        try {
            vehicleInsertBatch(list);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * @Author: liwencai
     * @Description: 删除公车信息
     * @Date: 2022/7/27
     * @Param vid:
     * @return: boolean
     */
    @Override
    public boolean deleteById(Long vid) {
        TblVehicleInfo vehicleInfo = vehicleInfoMapper.selectById(vid);
        if(null != vehicleInfo){
            vehicleInfo.setDeleteTime(LocalDateTime.now());
            // todo vehicleInfo.setDeleteBy();
            QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNull("delete_time").eq("id",vid);
            return vehicleInfoMapper.update(vehicleInfo,queryWrapper) == 1;
        }
        return false;
    }

    /**
     * @Author: liwencai
     * @Description: 修改公车信息
     * @Date: 2022/7/27
     * @Param paramVO:
     * @return: boolean
     */
    @Override
    @Transactional
    public boolean updateSpec(VehicleInfoParamVO paramVO) {
        System.out.println(paramVO.toString());
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("car_number",paramVO.getCarNumber());
        List<TblVehicleInfo> infoList = vehicleInfoMapper.selectList(queryWrapper);
        if (infoList.size() > 1){
            log.error("公车数据库中出现多条重复数据，重复公车车牌为：{" + paramVO.getCarNumber() + "}"+",总条数："+infoList.size());
            return false;
        }
        if(infoList.size() == 1 && !paramVO.getId().equals(infoList.get(0).getId())) {
            return false;
        }
        TblVehicleInfo vehicleInfo = vehicleInfoConvert.toVehicleInfo(paramVO);
        vehicleInfo.setUpdateTime(LocalDateTime.now());
        QueryWrapper<TblVehicleInfo> queryWrapper_update = new QueryWrapper<>();
        queryWrapper_update.isNull("delete_time").eq("id",paramVO.getId());
        // todo vehicleInfo.setUpdateBy();
        return vehicleInfoMapper.update(vehicleInfo,queryWrapper_update) == 1;
    }

    /**
     * @Author: liwencai
     * @Description: 查询统计某类公车的当月和当日使用情况
     * @Date: 2022/7/28
     * @Param selectByCidByDateMap:
     * @return: java.util.List<com.thtf.office.vo.VehicleSelectByDateResult>
     */
    @Override
    public List<VehicleSelectByDateResult> selectByCidByDate(Map<String, Object> selectByCidByDateMap) {
        return null;
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车信息
     * @Date: 2022/7/27
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.entity.TblVehicleInfo>
     */
    @Override
    public List<TblVehicleInfo> select(VehicleInfoParamVO paramVO) {
        return vehicleInfoMapper.select(paramVO);
    }

    /**
     * @Author: liwencai
     * @Description: 批量导入公车信息
     * @Date: 2022/7/27
     * @Param totalList:
     * @return: void
     */
    void vehicleInsertBatch(List<TblVehicleInfo> totalList) throws Exception {
        // 初始化线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 20,
                4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.AbortPolicy());
        // 大集合拆分成N个小集合，以保证多线程异步执行, 过大容易回到单线程
        List<List<TblVehicleInfo>> splitList = SplitListUtil.split(totalList, 10);
        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        // 对拆分的集合进行批量处理, 先拆分的集合, 再多线程执行
        for (List<TblVehicleInfo> singleList : splitList) {
            // 线程池执行
            threadPool.execute(new Thread(() -> {
                for (TblVehicleInfo single : singleList) {
                    try {
                        insert(single);
                    }catch (Exception e){
                        log.error("添加出错");
                    }
                }
            }));
            // 任务个数 - 1, 直至为0时唤醒await()
            countDownLatch.countDown();
        }
        try {
            // 让当前线程处于阻塞状态，直到锁存器计数为零
            countDownLatch.await();
        } catch (InterruptedException e) {
            // todo 自定义注解
            throw new Exception();
        }
    }

}
