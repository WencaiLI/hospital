package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.common.dto.adminserver.UserInfo;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.office.common.exportExcel.ExcelVehicleUtils;
import com.thtf.office.common.util.HttpUtil;
import com.thtf.office.common.util.SplitListUtil;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.dto.converter.VehicleInfoConverter;
import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.mapper.TblVehicleCategoryMapper;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.TblVehicleInfoService;
import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.vo.VehicleSelectByDateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
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
    TblVehicleInfoMapper vehicleInfoMapper;

    @Resource
    TblVehicleSchedulingMapper vehicleSchedulingMapper;

    @Resource
    VehicleInfoConverter vehicleInfoConverter;

    @Autowired
    IdGeneratorSnowflake idGeneratorSnowflake;

    @Autowired
    TblVehicleCategoryMapper vehicleCategoryMapper;

    @Autowired
    AdminAPI adminAPI;

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
    public Map<String,Object> insert(TblVehicleInfo vehicleInfo) {
        /* 车牌号不能相同 */
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("car_number",vehicleInfo.getCarNumber());
        List<TblVehicleInfo> infoList = vehicleInfoMapper.selectList(queryWrapper);
        if (infoList.size() == 1){
            return getServiceResultMap("error","车牌号不能相同",null);
        }
        /* 设置初始值: 状态设置为待命中，创建日期设置为当前日期 */
        vehicleInfo.setId(this.idGeneratorSnowflake.snowflakeId());
        vehicleInfo.setStatus(0);
        vehicleInfo.setCreateTime(LocalDateTime.now());
        vehicleInfo.setCreateBy(getOperatorName());
        if(vehicleInfoMapper.insert(vehicleInfo) == 1){
            return getServiceResultMap("success",null,null);
        }
        return getServiceResultMap("error","车辆新增失败",null);
    }

    /**
     * @Author: liwencai
     * @Description: 批量导入公车信息
     * @Date: 2022/7/27
     * @Param list:
     * @return: boolean
     */
    @Override
    @Transactional
    public Map<String,Object> insertBatch(List<VehicleInfoExcelImportDTO> list)  {
        // 1 获取公车类别数据库所有的公车类别名称
        QueryWrapper<TblVehicleCategory> queryWrapper_category = new QueryWrapper<>();
        queryWrapper_category.isNull("delete_time");
        List<TblVehicleCategory> categoryInDB = vehicleCategoryMapper.selectList(queryWrapper_category);
        ArrayList<String> categoryNameInDB = vehicleCategoryMapper.selectList(queryWrapper_category).stream().map(TblVehicleCategory::getName).collect(Collectors.toCollection(ArrayList::new));
        // 2 获取数据库中所有的车牌号
        QueryWrapper<TblVehicleInfo> queryWrapper_info = new QueryWrapper<>();
        queryWrapper_category.isNull("delete_time").groupBy("car_number");
        ArrayList<String> carNumberExistInDB = vehicleInfoMapper.selectList(queryWrapper_info).stream().map(TblVehicleInfo::getCarNumber).collect(Collectors.toCollection(ArrayList::new));
        // 3 获取Excel中的所有的车牌号
        ArrayList<String> carNumberNeedInsertList = list.stream().map(VehicleInfoExcelImportDTO::getCarNumber).collect(Collectors.toCollection(ArrayList::new));
        // 4 需要入库的数据
        List<TblVehicleInfo> canInsertInfoList = new ArrayList<>();
        /* 5 Service层返回的结果集 */
        // 5.1 已经存在于数据库中的车牌号（不入库）
        List<String> carNumberHasExistInDB = new ArrayList<>();
        // 5.2 已经在Excel存在两次的车牌号（不入库）
        List<String> carNumberHasExistInExcelTwiceOrMore = new ArrayList<>();
        // 5.3 成功入库的数据的车牌号
        List<String> carNumberHasSuccessInsertDB = new ArrayList<>();
        for (VehicleInfoExcelImportDTO dto : list) {
            // 已经存在在数据库中的车牌号 5.1
            if(carNumberExistInDB.contains(dto.getCarNumber())){
                carNumberHasExistInDB.add(dto.getCarNumber());
                continue;
            }
            // 新增的数据已经在Excel中重复过两次，5.2
            if(carNumberNeedInsertList.stream().filter(e-> e.equals(dto.getCarNumber())).collect(Collectors.toCollection(ArrayList::new)).size() > 1){
                carNumberHasExistInExcelTwiceOrMore.add(dto.getCarNumber());
                continue;
            }
            // 需要新增的公车的类别在类别库中存在 5.3
            if (categoryNameInDB.contains(dto.getVehicleCategoryName())){
                TblVehicleInfo tblVehicleInfo = vehicleInfoConverter.toVehicleInfo(dto);
                ArrayList<TblVehicleCategory> ids = categoryInDB.stream().filter(e -> !e.getName().equals(tblVehicleInfo.getCarNumber())).collect(Collectors.toCollection(ArrayList::new));
                tblVehicleInfo.setVehicleCategoryId(ids.get(0).getId());
                tblVehicleInfo.setId(idGeneratorSnowflake.snowflakeId());
                canInsertInfoList.add(tblVehicleInfo);
                carNumberHasSuccessInsertDB.add(dto.getCarNumber());
            }
        }

        System.out.println("增加前的数据"+canInsertInfoList.toString());
        vehicleInsertBatch(canInsertInfoList);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("carNumberHasExistInDB",carNumberHasExistInDB);
        resultMap.put("carNumberHasExistInExcelTwiceOrMore",carNumberHasExistInExcelTwiceOrMore);
        resultMap.put("carNumberHasSuccessInsertDB",carNumberHasSuccessInsertDB);
        log.info(resultMap.toString());
        return resultMap;
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
            vehicleInfo.setDeleteBy(getOperatorName());
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
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("car_number",paramVO.getCarNumber()).ne("id",paramVO.getId());
        List<TblVehicleInfo> infoList = vehicleInfoMapper.selectList(queryWrapper);
        if (infoList.size() >= 1){
            log.error("公车数据库中出现多条重复数据，重复公车车牌为：{" + paramVO.getCarNumber() + "}"+",总条数："+infoList.size());
            return false;
        }
        TblVehicleInfo vehicleInfo = vehicleInfoConverter.toVehicleInfo(paramVO);
        vehicleInfo.setUpdateTime(LocalDateTime.now());
        vehicleInfo.setUpdateBy(getOperatorName());
        QueryWrapper<TblVehicleInfo> queryWrapper_update = new QueryWrapper<>();
        queryWrapper_update.isNull("delete_time").eq("id",paramVO.getId());
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
    @Transactional
    public boolean updateInfoStatus() {
        LocalDateTime now = LocalDateTime.now();
        // 更新为出车中状态
        QueryWrapper<TblVehicleScheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").lt("start_time",now).gt("end_time",now).orderByAsc("end_time");
        List<TblVehicleScheduling> schedulings = vehicleSchedulingMapper.selectList(queryWrapper);
        if(schedulings.size()>=1){
            // 修改该公车为出车中状态
            for (TblVehicleScheduling tblVehicleScheduling : schedulings) {
                vehicleInfoMapper.changeVehicleStatus(getUpdateInfoStatusMap(tblVehicleScheduling.getVehicleInfoId(),1,getOperatorName()));
            }
        }
        // 查询所有处在出车中和待命中的车
        QueryWrapper<TblVehicleInfo> queryWrapper_1 = new QueryWrapper<>();
        queryWrapper_1.isNull("delete_time").eq("status",0).or().eq("status",1);
        List<Long> ids = vehicleInfoMapper.selectList(queryWrapper_1).stream().map(TblVehicleInfo::getId).collect(Collectors.toList());
        ids.removeAll(schedulings.stream().map(TblVehicleScheduling::getVehicleInfoId).collect(Collectors.toList()));
        if(ids.size() == 0 ){
            return false;
        }
        for (Long id : ids) {
            vehicleInfoMapper.changeVehicleStatus(getUpdateInfoStatusMap(id,0,null));
        }
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
    public List<TblVehicleInfo> selectByKey(String keywords) {
       return vehicleInfoMapper.selectByKey(keywords);
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
        queryWrapper.isNull("delete_time").eq("vehicle_category_id",cid);
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
    Map<String,Object> getUpdateInfoStatusMap(Long vehicleId,Integer newStatus,String updateBy){
        Map<String,Object> map = new HashMap<>();
        map.put("vid", vehicleId);
        map.put("status",newStatus);
        map.put("updateBy",updateBy);
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

    /**
     * @Author: liwencai
     * @Description: 获取操作人姓名
     * @Date: 2022/8/2
     * @return: null
     */
    public String getOperatorName(){
        UserInfo userInfo = null;
        String realName = null;
        try {
            userInfo = adminAPI.userInfo(HttpUtil.getToken());
        }catch (Exception e){
           log.info("远程调用根据token查询用户信息失败失败");
        }
        if(null !=  userInfo){
            realName = userInfo.getRealname();
        }
        return realName;
    }
}
