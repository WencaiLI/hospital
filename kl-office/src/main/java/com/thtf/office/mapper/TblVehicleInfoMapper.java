package com.thtf.office.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.office.dto.VehicleNumberTypeCodeDTO;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆信息表 Mapper 接口
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleInfoMapper extends BaseMapper<TblVehicleInfo> {

    /**
     * @Author: liwencai
     * @Description: 条件查询公车信息
     * @Date: 2022/7/27
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.entity.TblVehicleInfo>
     */
    List<TblVehicleInfo> select(VehicleInfoParamVO paramVO);

    /**
     * @Author: liwencai
     * @Description: 将公车关联的公车类别置为空
     * @Date: 2022/7/27
     * @Param cid:
     * @return: java.lang.Integer
     */
    Integer setCidToNull(@Param("cid") Long cid);

    /**
     * @Author: liwencai
     * @Description: 修改公车状态
     * @Date: 2022/7/27
     * @Param map:
     * @return: java.lang.Integer
     */
    Integer changeVehicleStatus(Map<String, Object> map);

    /**
     * @Author: liwencai
     * @Description: 车辆调度状态实时统计(数据库存在的调度状态)
     * @Date: 2022/7/28
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.vo.VehicleStatisticsResultVO>
     */
    List<VehicleStatisticsResultVO> getVehicleStatus(Map<String,Object> map);

    /**
     * @Author: liwencai
     * @Description: 各类车辆出车统计
     * @Date: 2022/7/28
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.vo.VehicleStatisticsResultVO>
     */
    List<VehicleStatisticsResultVO> getVehicleCategory(VehicleStatisticsParamVO paramVO);

    /**
     * @Author: liwencai
     * @Description: 获取排行榜信息
     * @Date: 2022/7/28
     * @Param map:
     * @return: java.util.List<com.thtf.office.vo.VehicleRankingsResultVO>
     */
    List<VehicleRankingsResultVO> getRankings(Map<String, Object> map);

    /**
     * @Author: liwencai
     * @Description: 通过公车类别和按日或月查询公车信息
     * @Date: 2022/7/28
     * @Param selectByCidByDateMap:
     * @return: java.util.List<com.thtf.office.vo.VehicleSelectByDateResult>
     */
    List<VehicleSelectByDateResult> selectByCidByDate(Map<String, Object> selectByCidByDateMap);

    /**
     * @Author: liwencai
     * @Description: 给公车重新绑定车辆类别
     * @Date: 2022/7/28
     * @Param map:
     * @Return: java.lang.Integer
     */
    Integer changeBind(Map<String,Object> map);

    /**
     * @Author: liwencai
     * @Description: 模糊查询
     * @Date: 2022/8/4
     * @Param keywords: 关键词
     * @return: java.util.List<com.thtf.office.entity.TblVehicleInfo>
     */
    List<TblVehicleInfo> selectByKey(@Param("keywords") String keywords);

    /**
     * @Author: liwencai
     * @Description: 获取工作时长排行
     * @Date: 2023/3/8
     * @Param map:
     * @Return: java.util.List<com.thtf.office.vo.VehicleRankingsResultVO>
     */
    List<VehicleRankingsResultVO> getWorkingDurationRankings(Map<String, Object> map);

    /**
     * @Author: liwencai
     * @Description: 根据车牌号集 分别获取公车对应的类别信息
     * @Date: 2023/3/8
     * @Param carNumberList: 车牌号集
     * @Return: java.util.List<com.thtf.office.dto.VehicleNumberTypeCodeDTO>
     */
    List<VehicleNumberTypeCodeDTO> listCarTypeCodeList(@Param("carNumberList") List<String> carNumberList);
}
