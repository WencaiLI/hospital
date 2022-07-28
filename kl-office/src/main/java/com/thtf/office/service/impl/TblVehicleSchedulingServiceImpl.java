package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.entity.adminserver.TblBasicData;
import com.thtf.office.common.entity.adminserver.TblUser;
import com.thtf.office.common.dto.adminserver.TblUserScheduleDTO;
import com.thtf.office.feign.AdminAPI;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.TblVehicleSchedulingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 车辆调度表 服务实现类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Service
public class TblVehicleSchedulingServiceImpl extends ServiceImpl<TblVehicleSchedulingMapper, TblVehicleScheduling> implements TblVehicleSchedulingService {

    @Resource
    TblVehicleSchedulingMapper vehicleSchedulingMapper;

    @Autowired
    private AdminAPI adminAPI;

    @Override
    public boolean deleteById(Long sid) {
        TblVehicleScheduling scheduling = vehicleSchedulingMapper.selectById(sid);
        if(null != scheduling){
            scheduling.setDeleteTime(LocalDateTime.now());
            //todo scheduling.setDeleteBy();
            QueryWrapper<TblVehicleScheduling> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNull("delete_time").eq("id",sid);
            return vehicleSchedulingMapper.update(scheduling,queryWrapper) == 1;
        }
        return false;
    }

    @Override
    public List<TblVehicleScheduling> select(VehicleSchedulingParamVO paramVO) {
        return vehicleSchedulingMapper.select(paramVO);
    }

    @Override
    public List<TblUserScheduleDTO> findDriverForSchedule(String positionTitle) {
        //获取外部接口人员数据
        JsonResult<List<TblUser>> dataJsonResult = adminAPI.searchUserByPosition(positionTitle);
        List<TblUser> data = dataJsonResult.getData();
        //组装人员信息及出车次数信息
        return null;
    }

    @Override
    public String createSerialNumber() {
        ResponseEntity<JsonResult<List<TblBasicData>>> datas = adminAPI.searchBasicDataByType(30);
        List<TblBasicData> basicDatas = Objects.requireNonNull(datas.getBody()).getData();
        TblBasicData basicData = basicDatas.stream().filter(obj -> obj.getBasicName().contains("入库")).findFirst().get();
        String num = basicData.getBasicCode();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<TblVehicleScheduling> infos = vehicleSchedulingMapper.selectList(new QueryWrapper<TblVehicleScheduling>().like("update_time", formatter.format(LocalDateTime.now(ZoneId.of("+8")))).orderByDesc("update_time"));
        if(!infos.isEmpty()){
            DecimalFormat dft = new DecimalFormat("000");
            num += formatter2.format(LocalDateTime.now(ZoneId.of("+8"))) +
                    dft.format(Integer.parseInt(infos.get(0).getCode().substring(infos.get(0).getCode().length()-3))+1);
        } else {
            num += formatter2.format(LocalDateTime.now(ZoneId.of("+8"))) + "001";
        }
        return num;
    }
}
