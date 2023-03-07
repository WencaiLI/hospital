package com.thtf.office.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.security.SecurityContextHolder;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.office.common.enums.VehicleSchedulingPurposeEnum;
import com.thtf.office.common.enums.VehicleSchedulingStatusEnum;
import com.thtf.office.common.enums.VehicleStatusEnum;
import com.thtf.office.common.exportExcel.EasyExcelStyleUtils;
import com.thtf.office.common.exportExcel.ExcelVehicleUtils;
import com.thtf.office.common.util.SplitListUtil;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.dto.converter.VehicleInfoConverter;
import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.mapper.TblVehicleCategoryMapper;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.TblVehicleCategoryService;
import com.thtf.office.service.TblVehicleInfoService;
import com.thtf.office.service.TblVehicleMaintenanceService;
import com.thtf.office.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆信息表 服务实现类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Service
@Slf4j
public class TblVehicleInfoServiceImpl extends ServiceImpl<TblVehicleInfoMapper, TblVehicleInfo> implements TblVehicleInfoService {

    @Resource
    private TblVehicleMaintenanceService vehicleMaintenanceService;

    @Resource
    private TblVehicleInfoMapper vehicleInfoMapper;

    @Resource
    private TblVehicleSchedulingMapper vehicleSchedulingMapper;

    @Resource
    private VehicleInfoConverter vehicleInfoConverter;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    @Autowired
    private TblVehicleCategoryMapper vehicleCategoryMapper;


    @Resource
    private TblVehicleCategoryService categoryService;

    @Autowired
    private AdminAPI adminAPI;

    /**
     * 实时统计导入进度最大100
     */
    private static BigDecimal countData = new BigDecimal(0);

    /**
     * @Author: liwencai
     * @Description: 新增公车信息
     * @Date: 2022/8/2
     * @Param vehicleInfo:
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    @Transactional
    public String insert(TblVehicleInfo vehicleInfo) throws Exception {
        /* 车牌号不能相同 */
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(TblVehicleInfo::getDeleteTime).eq(TblVehicleInfo::getCarNumber,vehicleInfo.getCarNumber());
        List<TblVehicleInfo> infoList = vehicleInfoMapper.selectList(queryWrapper);
        if (infoList.size() == 1){
            throw new Exception("车牌号重复");
        }
        /* 设置初始值: 状态设置为待命中，创建日期设置为当前日期，使用时长为0 */
        long id = this.idGeneratorSnowflake.snowflakeId();
        vehicleInfo.setId(id);
        vehicleInfo.setStatus(0);
        vehicleInfo.setWorkingDuration(0L);
        vehicleInfo.setCreateTime(LocalDateTime.now());
        vehicleInfo.setCreateBy( SecurityContextHolder.getUserName());
        if(vehicleInfoMapper.insert(vehicleInfo) == 1){
            // return getServiceResultMap("success",null,null);
            return String.valueOf(id);
        }else {
            throw new Exception("新增失败");
        }
        // return getServiceResultMap("error","车辆新增失败",null);
    }

    /**
     * @Author: liwencai
     * @Description: 批量导入公车信息(公车信息的验证在导入阶段已经完成)
     * @Date: 2022/7/27
     * @Param list:
     * @return: boolean
     */
    @Override
    @Transactional
    public Map<String,Object> insertBatch(List<VehicleInfoExcelImportDTO> list)  {
        for (VehicleInfoExcelImportDTO dto :list) {
            String vehicleCategoryName = dto.getVehicleCategoryName();
            LambdaQueryWrapper<TblVehicleCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.select(TblVehicleCategory::getId);
            lambdaQueryWrapper.eq(TblVehicleCategory::getName,vehicleCategoryName);
            Long id = vehicleCategoryMapper.selectOne(lambdaQueryWrapper).getId();
            TblVehicleInfo tblVehicleInfo = vehicleInfoConverter.toVehicleInfo(dto);
            tblVehicleInfo.setId(idGeneratorSnowflake.snowflakeId());
            if(null != id){
                tblVehicleInfo.setVehicleCategoryId(id);
            }
            tblVehicleInfo.setCreateTime(LocalDateTime.now());
            tblVehicleInfo.setCreateBy( SecurityContextHolder.getUserName());
            vehicleInfoMapper.insert(tblVehicleInfo);
        }
        return null;
    }

    /**
     * @Author: liwencai
     * @Description: 批量导入公车信息
     * @Date: 2022/7/27
     * @Param totalList:
     * @return: void
     */
    void vehicleInsertBatch(List<TblVehicleInfo> totalList)  {
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
                        e.printStackTrace();
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
//            throw new Exception();
        }
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
            vehicleInfo.setDeleteBy( SecurityContextHolder.getUserName());
            QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().isNull(TblVehicleInfo::getDeleteTime).eq(TblVehicleInfo::getId,vid);
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
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(TblVehicleInfo::getDeleteTime).eq(TblVehicleInfo::getCarNumber,paramVO.getCarNumber()).ne(TblVehicleInfo::getId,paramVO.getId());
        List<TblVehicleInfo> infoList = vehicleInfoMapper.selectList(queryWrapper);
        if (infoList.size() >= 1){
            log.error("公车数据库中出现多条重复数据，重复公车车牌为：{" + paramVO.getCarNumber() + "}"+",总条数："+infoList.size());
            return false;
        }
        TblVehicleInfo vehicleInfo = vehicleInfoConverter.toVehicleInfo(paramVO);
        vehicleInfo.setUpdateTime(LocalDateTime.now());
        vehicleInfo.setUpdateBy( SecurityContextHolder.getUserName());
        QueryWrapper<TblVehicleInfo> queryWrapper_update = new QueryWrapper<>();
        queryWrapper_update.lambda().isNull(TblVehicleInfo::getDeleteTime).eq(TblVehicleInfo::getId,paramVO.getId());
        return vehicleInfoMapper.update(vehicleInfo,queryWrapper_update) == 1;
    }

    /**
     * @Author: liwencai
     * @Description: 更新所有公车的状态
     * 注意：原符号       <       <=      >       >=      <>
     *      对应函数    lt()     le()    gt()    ge()    ne()
     * @Date: 2022/7/28
     * @return: boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInfoStatus() {
        LocalDateTime now = LocalDateTime.now();
        /* 1.应处于调度状态的调度记录 (从未开始和已结束的调度中筛选)*/
        QueryWrapper<TblVehicleScheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(TblVehicleScheduling::getDeleteTime)
                .lt(TblVehicleScheduling::getStartTime,now)
                .gt(TblVehicleScheduling::getEndTime,now).orderByAsc(TblVehicleScheduling::getStartTime)
                .and(e->e.eq(TblVehicleScheduling::getStatus, VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus())
                        .or()
                        .eq(TblVehicleScheduling::getStatus, VehicleSchedulingStatusEnum.NOT_START_SCHEDULING.getStatus())
                );
        List<TblVehicleScheduling> schedulingList = vehicleSchedulingMapper.selectList(queryWrapper);
        // 1.1 修改公车状态为出车中
        for (TblVehicleScheduling tblVehicleScheduling : schedulingList) {
            // 修改调度状态
            tblVehicleScheduling.setStatus(VehicleSchedulingStatusEnum.IN_SCHEDULING.getStatus());
            UpdateWrapper<TblVehicleScheduling> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().isNull(TblVehicleScheduling::getDeleteTime).eq(TblVehicleScheduling::getId,tblVehicleScheduling.getId());
            vehicleSchedulingMapper.update(tblVehicleScheduling,updateWrapper);
            // 车辆应该处于维修中状态
            Long workingDuration = tblVehicleScheduling.getWorkingDuration();
            Integer newVehicleStatus;
            if(VehicleSchedulingPurposeEnum.MAINTAIN.getStatus().equals(tblVehicleScheduling.getPurpose())){
                newVehicleStatus = VehicleStatusEnum.MAINTAIN.getStatus();
            }
            // 车辆应处于出车中状态
            else {
                newVehicleStatus = VehicleStatusEnum.OUT.getStatus();
            }
            vehicleInfoMapper.changeVehicleStatus(getUpdateInfoStatusMap(tblVehicleScheduling.getVehicleInfoId(),newVehicleStatus,SecurityContextHolder.getUserName(),-workingDuration));
        }

        /* 2. 查询应结束公车调度的调度 查询应调度目的待命中的出车中或维修中的车辆 */
        QueryWrapper<TblVehicleScheduling> queryWrapper_1 = new QueryWrapper<>();
        // 已经结束的调度
        queryWrapper_1.lambda().isNull(TblVehicleScheduling::getDeleteTime)
                .le(TblVehicleScheduling::getEndTime,now)
                .and(e->e.eq(TblVehicleScheduling::getStatus,VehicleSchedulingStatusEnum.IN_SCHEDULING.getStatus())
                        .or()
                        .eq(TblVehicleScheduling::getStatus,VehicleSchedulingStatusEnum.NOT_START_SCHEDULING.getStatus())
                );
        List<TblVehicleScheduling> tblVehicleSchedulingList = vehicleSchedulingMapper.selectList(queryWrapper_1);
        for (TblVehicleScheduling tblVehicleScheduling : tblVehicleSchedulingList) {
            Integer newVehicleSchedulingStatus;
            if (VehicleSchedulingPurposeEnum.ELIMINATED.getStatus().equals(tblVehicleScheduling.getPurpose())){
                // 修改公车状态为被淘汰状态
                newVehicleSchedulingStatus = VehicleStatusEnum.ELIMINATED.getStatus();
            }else {
                // 修改公车为待命中状态
                newVehicleSchedulingStatus = VehicleStatusEnum.STANDBY.getStatus();
            }
            // 修改公车状态
            vehicleInfoMapper.changeVehicleStatus(getUpdateInfoStatusMap(tblVehicleScheduling.getVehicleInfoId(),newVehicleSchedulingStatus, SecurityContextHolder.getUserName(),tblVehicleScheduling.getWorkingDuration()));
            // 修改调度状态
            UpdateWrapper<TblVehicleScheduling> updateWrapper = new UpdateWrapper<>();
            tblVehicleScheduling.setStatus(VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus());
            updateWrapper.lambda().isNull(TblVehicleScheduling::getDeleteTime).eq(TblVehicleScheduling::getId,tblVehicleScheduling.getId());
            vehicleSchedulingMapper.update(tblVehicleScheduling,updateWrapper);
            // 如果调度目的为维保的话，新增维保记录
            if(VehicleSchedulingPurposeEnum.MAINTAIN.getStatus().equals(tblVehicleScheduling.getPurpose())){
                // 新增一条维保记录
                VehicleMaintenanceParamVO vehicleMaintenanceParamVO = new VehicleMaintenanceParamVO();
                vehicleMaintenanceParamVO.setVehicleInfoId(tblVehicleScheduling.getVehicleInfoId());
                vehicleMaintenanceParamVO.setMaintenanceTime(tblVehicleScheduling.getStartTime());
                vehicleMaintenanceParamVO.setHandledBy(tblVehicleScheduling.getDriverName());
                vehicleMaintenanceParamVO.setDescription(tblVehicleScheduling.getDescription());
                vehicleMaintenanceParamVO.setName(VehicleSchedulingPurposeEnum.MAINTAIN.getDesc());
                // todo 花费没有字段表示
                vehicleMaintenanceParamVO.setMoneySpent(null);
                vehicleMaintenanceService.insert(vehicleMaintenanceParamVO);
            }
        }
        /* 3.尚未开始调度的调度 修改公车为待命中状态，修改调度状态为尚未开始调度 */
        QueryWrapper<TblVehicleScheduling> queryWrapper_2 = new QueryWrapper<>();
        queryWrapper_2.lambda().isNull(TblVehicleScheduling::getDeleteTime)
                .gt(TblVehicleScheduling::getStartTime,now)
                .and(e->e.eq(TblVehicleScheduling::getStatus,VehicleSchedulingStatusEnum.IN_SCHEDULING.getStatus())
                        .or()
                        .eq(TblVehicleScheduling::getStatus,VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus())
                );
        List<TblVehicleScheduling> tblVehicleSchedulingList2 = vehicleSchedulingMapper.selectList(queryWrapper_2);
        tblVehicleSchedulingList2.forEach(tblVehicleScheduling->{

            Long workDuration = null;

            if(VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus().equals(tblVehicleScheduling.getStatus())){
                workDuration = -(tblVehicleScheduling.getWorkingDuration());
            }
            vehicleInfoMapper.changeVehicleStatus(getUpdateInfoStatusMap(tblVehicleScheduling.getVehicleInfoId(),VehicleStatusEnum.STANDBY.getStatus(),SecurityContextHolder.getUserName(),workDuration));
            UpdateWrapper<TblVehicleScheduling> updateWrapper = new UpdateWrapper<>();
            tblVehicleScheduling.setStatus(VehicleSchedulingStatusEnum.NOT_START_SCHEDULING.getStatus());
            updateWrapper.lambda().isNull(TblVehicleScheduling::getDeleteTime).eq(TblVehicleScheduling::getId,tblVehicleScheduling.getId());
            vehicleSchedulingMapper.update(tblVehicleScheduling,updateWrapper);
        });
        return true;
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
        // 目前只针对厂商和车牌号进行模糊查询
        if(StringUtils.isNotBlank(paramVO.getKeyword())){
            paramVO.setModelKeyword(paramVO.getKeyword());
            paramVO.setCarNumberKeyword(paramVO.getKeyword());
        }
        if(StringUtils.isNotBlank(paramVO.getModel())){
            paramVO.setModelKeyword(null);
        }
        if(StringUtils.isNotBlank(paramVO.getCarNumber())){
            paramVO.setCarNumberKeyword(null);
        }
        return vehicleInfoMapper.select(paramVO);
    }


    /**
     * @Author: liwencai
     * @Description: 关键词模糊查询
     * @Date: 2022/8/4
     * @Param keywords:
     * @return: java.lang.Object
     */
    @Override
    @Deprecated
    public List<TblVehicleInfo> selectByKey(String keywords) {
       return vehicleInfoMapper.selectByKey(keywords);
    }

    /**
     * @Author: liwencai
     * @Description: 验证车牌是否可入库（数据库中不能存在相同车牌）
     * @Date: 2022/8/11
     * @Param carNumber:
     * @return: boolean
     */
    @Override
    public boolean verifyCarNumberForInsert(String carNumber) {
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(TblVehicleInfo::getDeleteTime).eq(TblVehicleInfo::getCarNumber,carNumber);
        List<TblVehicleInfo> infoList = vehicleInfoMapper.selectList(queryWrapper);
        return infoList.size() == 0;
    }

    /**
     * @Author: liwencai
     * @Description: 验证车类别是否可入库
     * @Date: 2022/8/11
     * @Param vehicleCategoryName:
     * @return: boolean
     */
    @Override
    public boolean verifyCategoryForInsert(String vehicleCategoryName) {
        QueryWrapper<TblVehicleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(TblVehicleCategory::getDeleteTime).eq(TblVehicleCategory::getName,vehicleCategoryName);
        return vehicleCategoryMapper.selectList(queryWrapper).size() >= 1;
    }

    @Override
    public void importTemplateDownloadNew(HttpServletResponse response,List<?> list,Class<?> clazz) {
        // 查询所有类别
        List<VehicleCategoryResultVO> itemType = categoryService.select(new VehicleCategoryParamVO());
        List<String> collect = itemType.stream().map(VehicleCategoryResultVO::getName).collect(Collectors.toList());
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream(), clazz)
                    .registerWriteHandler(new EasyExcelStyleUtils.CustomSheetWriteHandler2())
                    .registerWriteHandler(new EasyExcelStyleUtils.CustomSheetWriteHandler(collect))
                    .relativeHeadRowIndex(3)
                    .registerWriteHandler(ExcelVehicleUtils.getStyleStrategy())
                    .build();
            WriteSheet writeSheet = new WriteSheet();
            writeSheet.setSheetName("sheet");
            excelWriter.write(list, writeSheet);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 千万别忘记关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    /**
     * @Author: liwencai
     * @Description: 查询未淘汰的车辆
     * @Date: 2023/3/7
     * @Param vehicleCategoryId: 车辆类别id
     * @Return: java.util.List<com.thtf.office.entity.TblVehicleInfo>
     */
    @Override
    public List<TblVehicleInfo> listUnEliminate(String vehicleCategoryId) {
        LambdaQueryWrapper<TblVehicleInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNull(TblVehicleInfo::getDeleteTime);
        lambdaQueryWrapper.ne(TblVehicleInfo::getStatus, VehicleStatusEnum.ELIMINATED.getStatus());
        lambdaQueryWrapper.eq(TblVehicleInfo::getVehicleCategoryId,vehicleCategoryId);
        lambdaQueryWrapper.orderByAsc(TblVehicleInfo::getWorkingDuration);
        return vehicleInfoMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * @Author: liwencai
     * @Description: 查询统计某类公车的当月和当日使用情况
     * @Date: 2022/7/28
     * @Param cid: 公车类别id
     * @return: java.util.List<com.thtf.office.vo.VehicleSelectByDateResult>
     */
    @Override
    public List<VehicleSelectByDateResult> selectByCidByDate(Long cid) {
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(TblVehicleInfo::getDeleteTime).eq(TblVehicleInfo::getVehicleCategoryId,cid);
        if(vehicleInfoMapper.selectList(queryWrapper).size() == 0){
            return null;
        }
        // 每月的类别为cid的公车调用情况
        List<VehicleSelectByDateResult> monthResult = vehicleInfoMapper.selectByCidByDate(getSelectByCidByDateMap("monthNumber", "%Y-%m", cid));
        // 每日的类别为cid的公车调用情况
        List<VehicleSelectByDateResult> dayResult = vehicleInfoMapper.selectByCidByDate(getSelectByCidByDateMap("dayNumber", "%Y-%m-%d", cid));

        // 两个结果集合
        if(monthResult.size() < dayResult.size()){
            return null;
        }
        // 填补id在按月查时不为空，按日却为空时的数据
        for (VehicleSelectByDateResult vehicleSelectByDateResult : dayResult) {
            int j = 0;
            while (!monthResult.get(j).getId().equals(vehicleSelectByDateResult.getId())) {
                monthResult.get(j).setDayNumber(0L);
                j = j + 1;
                if (monthResult.get(j).getId().equals(vehicleSelectByDateResult.getId())) {
                    break;
                }
            }
            monthResult.get(j).setDayNumber(vehicleSelectByDateResult.getDayNumber());
        }
        // 排序
        monthResult.sort((o1, o2) -> {
            int i = o1.getMonthNumber().compareTo(o2.getMonthNumber());
            if (i == 0) {
                i = o1.getDayNumber().compareTo(o2.getDayNumber());
                if (i == 0) {
                    i = o1.getDayNumber().compareTo(o2.getDayNumber());
                }
            }
            return i;
        });
        return monthResult;
    }

    /**
     * @Author: guola
     * @Description: 批量导入进度
     * @Date: 2022/7/31
     * @return: java.math.BigDecimal
     */
    @Override
    public BigDecimal importProgress() {
        return countData;
    }

    /**
     * @Author: guola
     * @Description: 批量导入公车信息
     * @Date: 2022/7/31
     * @Param uploadFile:
     * @Param originalFilename:
     * @Param type:
     * @Param user:
     * @return: java.lang.String
     */
    @Override
    public String batchImport(MultipartFile uploadFile, String originalFilename, String type, String user){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder buffer = new StringBuilder();
        StringBuilder buffer2 = new StringBuilder();
        countData = new BigDecimal(0);
        try {
            List<String[]> list = ExcelVehicleUtils.importExtendToList(uploadFile, originalFilename);
            int num = 0;
            int fail = 0;
            TblVehicleCategory vehicleCategory = vehicleCategoryMapper.selectOne(new QueryWrapper<TblVehicleCategory>().eq("id", type));
            for (String[] strings : list) {
                if(strings[0] == null || strings[1] == null || strings[2] == null || strings[3] == null || strings[0].equals("") || strings[1].equals("") || strings[2].equals("") || strings[3].equals("")){
                    fail++;
                    countData = new BigDecimal(fail).add(new BigDecimal(num)).divide(new BigDecimal(list.size()),
                            2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
                    buffer.append("第").append(num + fail + 1).append("行存在必填项未填写").append("；<br />");
                }  else {
                    List<TblVehicleInfo> tblVehicleInfos = vehicleInfoMapper.selectList(new QueryWrapper<TblVehicleInfo>().eq("car_number", strings[0]));
                    if(!tblVehicleInfos.isEmpty()){
                        fail++;
                        countData = new BigDecimal(fail).add(new BigDecimal(num)).divide(new BigDecimal(list.size()),
                                2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
                        buffer.append("第").append(num + fail + 1).append("行车牌号在本服务中已存在，略过本条").append("；<br />");
                        continue;
                    }
                    TblVehicleInfo item = new TblVehicleInfo();
                    item.setId(idGeneratorSnowflake.snowflakeId());
                    item.setCreateBy(user);
                    item.setCreateTime(LocalDateTime.now());
                    item.setUpdateBy(user);
                    item.setUpdateTime(LocalDateTime.now());
                    item.setCarNumber(strings[0]);
                    item.setModel(strings[1]);
                    item.setEngineNumber(strings[2]);
                    item.setFrameNumber(strings[3]);
                    item.setVehicleCategoryId(Long.valueOf(type));
                    if(strings[4] != null && !"".equals(strings[4])){
                        item.setColor(strings[4]);
                    }
                    if(strings[5] != null && !"".equals(strings[5])){
                        item.setDistributor(strings[5]);
                    }
                    if(strings[6] != null && !"".equals(strings[6])){
                        if(!StringUtils.isNumeric(strings[6])){
                            fail++;
                            countData = new BigDecimal(fail).add(new BigDecimal(num)).divide(new BigDecimal(list.size()),
                                    2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
                            buffer.append("第").append(num + fail + 1).append("行购买价格格式错误，请参照模板中红色字体").append("；<br />");
                            continue;
                        }
                    }
                    if(strings[7] != null && !"".equals(strings[7])){
                        try{
                            //增加强判断条件，否则 诸如2022-02-29也可判断出去
                            sdf.setLenient(false);
                            Date date = sdf.parse(strings[7]);
                        } catch(Exception e){
                            fail++;
                            countData = new BigDecimal(fail).add(new BigDecimal(num)).divide(new BigDecimal(list.size()),
                                    2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
                            buffer.append("第").append(num + fail + 1).append("行出厂日期格式错误，请参照模板中红色字体").append("；<br />");
                        }
                    }
                    if(strings[8] != null && !"".equals(strings[8])){
                        try{
                            sdf.setLenient(false);
                            Date date = sdf.parse(strings[8]);
                        } catch(Exception e){
                            fail++;
                            countData = new BigDecimal(fail).add(new BigDecimal(num)).divide(new BigDecimal(list.size()),
                                    2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
                            buffer.append("第").append(num + fail + 1).append("行购买日期格式错误，请参照模板中红色字体").append("；<br />");
                        }
                    }
                    if(strings[9] != null && !"".equals(strings[9])){
                        item.setInsurance(strings[9]);
                    }
                    if(strings[10] != null && !"".equals(strings[10])){
                        item.setMaintenance(strings[10]);
                    }
                    if(strings[11] != null && !"".equals(strings[11])){
                        item.setDescription(strings[11]);
                    }

                    vehicleInfoMapper.insert(item);

                    num++;
                    countData = new BigDecimal(fail).add(new BigDecimal(num)).divide(new BigDecimal(list.size()),
                            2, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
                }
            }
            buffer2.append("共导入").append(list.size()).append("条仪表信息，成功导入").append(num).append("条，失败").append(fail).append("条。");
            if(fail > 0){
                buffer2.append("<br />失败原因：").append(buffer);
            } else if(buffer.toString().length() > 0){
                buffer2.append("<br />结果：").append(buffer);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        countData = new BigDecimal(100);
        return buffer2.toString();
    }


    /* ********************【复用代码】**********************　*/

    /**
     * @Author: liwencai
     * @Description: 获取修改公车状态的参数map
     * @Date: 2022/7/29
     * @Param vehicleId:
     * @Param newStatus:
     * @Param updateBy:
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> getUpdateInfoStatusMap(Long vehicleId,Integer newStatus,String updateBy,Long workingDuration){
        Map<String,Object> map = new HashMap<>();
        map.put("vid", vehicleId);
        map.put("status",newStatus);
        map.put("updateBy",updateBy);
        map.put("workingDuration",workingDuration);
        return map;
    }

    /**
     * @Author: liwencai
     * @Description: 根据日期和类别查询该类别下汽车的调度排行的参数Map
     * @Date: 2022/7/28
     * @Param numberType: 每月：monthNumber 每日：dayNumber
     * @Param dateTemplate: 每月："%Y-%m" 每日："%Y-%m-%d"
     * @Param categoryId: 类别id
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> getSelectByCidByDateMap(String numberType,String dateTemplate,Long categoryId) {
        Map<String, Object> map = new HashMap<>();
        map.put("dateTemplate", dateTemplate);
        map.put("dateType", numberType);
        map.put("categoryId", categoryId);
        return map;

    }

    /**
     * @Author: liwencai
     * @Description: service层结果集封装，需要在service层返回controller层详细信息时使用
     * @Date: 2022/7/31
     * @Param status: 状态（“success”,"error"）
     * @Param errorCause: 错误原因
     * @Param result: 正确结果
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> getServiceResultMap(String status,String errorCause,Object result){
        Map<String,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("errorCause",errorCause);
        map.put("result",result);
        return map;
    }
}
