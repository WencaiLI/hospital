package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.util.IdGeneratorSnowflake;
import com.thtf.office.dto.VehicleCategoryConvert;
import com.thtf.office.vo.VehicleCategoryParamVO;
import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.mapper.TblVehicleCategoryMapper;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.service.TblVehicleCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.time.LocalDateTime;

/**
 * <p>
 * 车辆类别表 服务实现类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Service
public class TblVehicleCategoryServiceImpl extends ServiceImpl<TblVehicleCategoryMapper, TblVehicleCategory> implements TblVehicleCategoryService {

    @Resource
    VehicleCategoryConvert vehicleCategoryConvert;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    @Resource
    TblVehicleCategoryMapper vehicleCategoryMapper;

    @Resource
    TblVehicleInfoMapper vehicleInfoMapper;

    /**
     * @Author: liwencai
     * @Description: 新增公车类别
     * @Date: 2022/7/27
     * @Param vehicleCategoryParamVO:
     * @return: java.lang.Boolean
     */
    @Override
    @Transactional
    public Boolean insert(VehicleCategoryParamVO vehicleCategoryParamVO) {
        // 查询公车名称是否重复
        QueryWrapper<TblVehicleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("name",vehicleCategoryParamVO.getName());
        log.error(String.valueOf(vehicleCategoryMapper.selectList(queryWrapper)));
        if(0 == vehicleCategoryMapper.selectList(queryWrapper).size()){
            // 名称不重复则新增
            TblVehicleCategory vehicleCategory = vehicleCategoryConvert.toVehicleCategory(vehicleCategoryParamVO);
            // todo category.setCreateBy()
            vehicleCategory.setCreateTime(LocalDateTime.now());
            vehicleCategory.setId(this.idGeneratorSnowflake.snowflakeId());
            return vehicleCategoryMapper.insert(vehicleCategory) == 1;
        }
        return false;
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
            // todo category.setDeleteBy()
            QueryWrapper<TblVehicleCategory> queryWrapper_category = new QueryWrapper<>();
            queryWrapper_category.isNull("delete_time").eq("id",cid);
            int effortRow = vehicleCategoryMapper.update(category, queryWrapper_category);
            // 同时将所有相关公车的关联的类别id置为null
            vehicleInfoMapper.setCidToNull(cid);
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
        queryWrapper.isNull("delete_time").eq("name",vehicleCategoryParamVO.getName());
        List<TblVehicleCategory> categoryList = vehicleCategoryMapper.selectList(queryWrapper);
        if(categoryList.size()>1){
            return false;
        }
        // 数据库存在一条数据与要修改的名称一致时，即可能重复
        if(categoryList.size() == 1 && !vehicleCategoryParamVO.getId().equals(categoryList.get(0).getId()))
        {
            return false;
        }
        TblVehicleCategory category = vehicleCategoryConvert.toVehicleCategory(vehicleCategoryParamVO);
        category.setUpdateTime(LocalDateTime.now());
        // todo category.setUpdateBy()
        QueryWrapper<TblVehicleCategory> queryWrapper_update = new QueryWrapper<>();
        queryWrapper_update.isNull("delete_time").eq("id", vehicleCategoryParamVO.getId());
        return vehicleCategoryMapper.update(category, queryWrapper_update) == 1;
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车类别信息
     * @Date: 2022/7/26
     * @Param vehicleCategoryParamVO:
     * @return: java.awt.List
     */
    @Override
    public List<TblVehicleCategory> select(VehicleCategoryParamVO vehicleCategoryParamVO) {
        return vehicleCategoryMapper.select(vehicleCategoryParamVO);
    }
}
