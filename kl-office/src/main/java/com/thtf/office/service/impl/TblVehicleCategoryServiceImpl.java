package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.common.dto.adminserver.UserInfo;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.security.SecurityContextHolder;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.office.common.util.HttpUtil;
import com.thtf.office.dto.SelectAllInfoResultDTO;
import com.thtf.office.dto.converter.VehicleCategoryConverter;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.vo.VehicleCategoryChangeBindVO;
import com.thtf.office.vo.VehicleCategoryParamVO;
import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.mapper.TblVehicleCategoryMapper;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.service.TblVehicleCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.office.vo.VehicleCategoryResultVO;
import com.thtf.office.vo.VehicleStatisticsResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 车辆类别表 服务实现类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Service
@Slf4j
public class TblVehicleCategoryServiceImpl extends ServiceImpl<TblVehicleCategoryMapper, TblVehicleCategory> implements TblVehicleCategoryService {

    @Resource
    private VehicleCategoryConverter vehicleCategoryConverter;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    @Resource
    private TblVehicleCategoryMapper vehicleCategoryMapper;

    @Resource
    private TblVehicleInfoMapper vehicleInfoMapper;

    @Autowired
    private AdminAPI adminAPI;
    /**
     * @Author: liwencai
     * @Description: 新增公车类别
     * @Date: 2022/7/27
     * @Param vehicleCategoryParamVO:
     * @return: java.lang.Boolean
     */
    @Override
    @Transactional
    public Map<String,Object> insert(VehicleCategoryParamVO vehicleCategoryParamVO) {
        // 查询公车名称是否重复
        QueryWrapper<TblVehicleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("name",vehicleCategoryParamVO.getName());
        // 名称不重复则新增
        if(vehicleCategoryMapper.selectList(queryWrapper).size() == 0){
            TblVehicleCategory vehicleCategory = vehicleCategoryConverter.toVehicleCategory(vehicleCategoryParamVO);
            vehicleCategory.setCreateTime(LocalDateTime.now());
            vehicleCategory.setCreateBy(getOperatorName());
            vehicleCategory.setId(this.idGeneratorSnowflake.snowflakeId());
            if(vehicleCategoryMapper.insert(vehicleCategory) == 1){
                return getServiceResultMap("success",null,null);
            }
            return getServiceResultMap("error","新增失败",null);
        }
        return getServiceResultMap("error","类别名称重复",null);
    }

    /**
     * @Author: liwencai
     * @Description: 删除类别信息
     * @Date: 2022/7/26
     * @Param cid:
     * @return: boolean
     */
    @Override
    @Transactional
    public boolean deleteById(Long cid) {
        TblVehicleCategory category = vehicleCategoryMapper.selectById(cid);
        if(null != category){
            category.setDeleteTime(LocalDateTime.now());
            category.setDeleteBy(getOperatorName());
            QueryWrapper<TblVehicleCategory> queryWrapper_category = new QueryWrapper<>();
            queryWrapper_category.isNull("delete_time").eq("id",cid);
            int effortRow = vehicleCategoryMapper.update(category, queryWrapper_category);
            // 同时将所有相关公车的关联的类别id和类别名称置为null
            List<TblVehicleInfo> tblVehicleInfos = vehicleInfoMapper.selectList(new QueryWrapper<TblVehicleInfo>().lambda().eq(TblVehicleInfo::getVehicleCategoryId, cid));

            tblVehicleInfos.forEach(e->{
                e.setVehicleCategoryId(null);
                e.setDeleteTime(LocalDateTime.now());
                e.setDeleteBy(getOperatorName());
                vehicleInfoMapper.updateById(e);
            });

            // vehicleInfoMapper.setCidToNull(cid);
            return effortRow == 1;
        }else {
            return false;
        }
    }

    /**
     * @Author: liwencai
     * @Description: 修改公车类别
     * @Date: 2022/7/27
     * @Param vehicleCategoryParamVO:
     * @return: boolean
     */
    @Override
    @Transactional
    public boolean updateSpec(VehicleCategoryParamVO vehicleCategoryParamVO) {
        // 查找修改的名称是否存在
        QueryWrapper<TblVehicleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("name",vehicleCategoryParamVO.getName()).ne("id",vehicleCategoryParamVO.getId());
        List<TblVehicleCategory> categoryList = vehicleCategoryMapper.selectList(queryWrapper);
        if(categoryList.size() >= 1){
            return false;
        }
        TblVehicleCategory category = vehicleCategoryConverter.toVehicleCategory(vehicleCategoryParamVO);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateBy(getOperatorName());
        QueryWrapper<TblVehicleCategory> queryWrapper_update = new QueryWrapper<>();
        queryWrapper_update.isNull("delete_time").eq("id", vehicleCategoryParamVO.getId());
        return vehicleCategoryMapper.update(category, queryWrapper_update) == 1;
    }

    /**
     * @Author: liwencai 
     * @Description: 移除绑定车辆
     * @Date: 2022/7/29
     * @Param vehicleCategoryChangeBindVO: 
     * @return: boolean 
     */
    @Override
    public boolean changeBind(VehicleCategoryChangeBindVO vehicleCategoryChangeBindVO) {
        // 移除操作
        if(vehicleCategoryChangeBindVO.getType() == 0){
            for (String vid : vehicleCategoryChangeBindVO.getVidList()) {
                deleteById(Long.valueOf(vid));
            }
            return true;
        }
        // 重新绑定操作
        if(vehicleCategoryChangeBindVO.getType() == 1){
            for (String vid : vehicleCategoryChangeBindVO.getVidList()) {
                vehicleInfoMapper.changeBind(getChangeBindMap(Long.valueOf(vid),vehicleCategoryChangeBindVO.getTargetId(),vehicleCategoryChangeBindVO.getOriginId()));
            }
            return true;
        }
        return false;
    }

    /**
     * @Author: liwencai
     * @Description: 查询所有类别对应的各个公车的数量（以公车状态）
     * @Date: 2022/8/2
     * @return: java.util.List<com.thtf.office.dto.SelectAllInfoResultDTO>
     */
    @Override
    public List<SelectAllInfoResultDTO> selectInfoNumberByCategory() {
        // 查询所有类别
        QueryWrapper<TblVehicleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").groupBy("id").orderByAsc("id");
        List<TblVehicleCategory> categories = vehicleCategoryMapper.selectList(queryWrapper);
        // 返回值对象
        List<SelectAllInfoResultDTO> resultDTOS = new ArrayList<>();
        for (TblVehicleCategory o : categories) {
            SelectAllInfoResultDTO selectAllInfoResultDTO = new SelectAllInfoResultDTO();
            selectAllInfoResultDTO.setCategoryName(o.getName());
            // 类别对应的查询数量
            QueryWrapper<TblVehicleInfo> queryWrapper_info = new QueryWrapper<>();
            queryWrapper_info.isNull("delete_time").eq("vehicle_category_id",o.getId()).groupBy("vehicle_category_id");
            Integer totalNumber = vehicleInfoMapper.selectCount(queryWrapper_info);
            if(totalNumber == null){
                selectAllInfoResultDTO.setTotalNumber(0);
            }else {
                selectAllInfoResultDTO.setTotalNumber(totalNumber);
            }
            // 查询各个车所处的状态
            Map<String,Object> map = new HashMap<>();
            map.put("cid",o.getId());
            List<VehicleStatisticsResultVO> vehicleStatus = vehicleInfoMapper.getVehicleStatus(map);
            ArrayList<String> attributes = vehicleStatus.stream().map(VehicleStatisticsResultVO::getAttribute).collect(Collectors.toCollection(ArrayList::new));
            Stream.of("待命中","出车中","维修中").forEach(e->{
                if(!attributes.contains(e)){
                    VehicleStatisticsResultVO vehicleStatisticsResultVO = new VehicleStatisticsResultVO();
                    vehicleStatisticsResultVO.setAttribute(e);
                    vehicleStatisticsResultVO.setNumber(0L);
                    vehicleStatus.add(vehicleStatisticsResultVO);
                }
            });
            selectAllInfoResultDTO.setData(vehicleStatus);
            resultDTOS.add(selectAllInfoResultDTO);
        }
        return resultDTOS;
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车类别信息（包括该类别的汽车总数）
     * @Date: 2022/7/26
     * @Param vehicleCategoryParamVO:
     * @return: java.awt.List
     */
    @Override
    public List<VehicleCategoryResultVO> select(VehicleCategoryParamVO vehicleCategoryParamVO) {
        // 查询类别集合
        List<TblVehicleCategory> vehicleCategoryList = vehicleCategoryMapper.select(vehicleCategoryParamVO);
        List<VehicleCategoryResultVO> vehicleCategoryResultVOS = vehicleCategoryConverter.categoryListToOtherList(vehicleCategoryList);
        // 查询该类别集合分别对应的汽车总数
        for (VehicleCategoryResultVO o : vehicleCategoryResultVOS) {
            QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNull("delete_time").eq("vehicle_category_id",o.getId());
            o.setTotalNumber(vehicleInfoMapper.selectCount(queryWrapper));
        }
        return vehicleCategoryResultVOS;
    }

    /* ***************复用代码***************** */

    /**
     * @Author: liwencai
     * @Description: 获取更新类别传参map
     * @Date: 2022/7/29
     * @Param vid:
     * @Param newCid:
     * @Param oldCid:
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> getChangeBindMap(Long vid,Long newCid,Long oldCid){
        Map<String,Object> map = new HashMap<>();
        map.put("vid",vid);
        map.put("newCid",newCid);
        map.put("oldCid",oldCid);
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
        /*String userName = SecurityContextHolder.getUserName();
        System.out.println("XXXXXXX"+userName);*/
        return realName;
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
