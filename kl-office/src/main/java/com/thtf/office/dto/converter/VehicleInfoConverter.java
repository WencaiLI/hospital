package com.thtf.office.dto.converter;

import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.vo.VehicleInfoParamVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/7/26 23:04
 * @Description: 公车信息bean映射转换器
 */
@Mapper(componentModel = "spring")
public interface VehicleInfoConverter {
    TblVehicleInfo toVehicleInfo(VehicleInfoParamVO vehicleInfoParamVO);
    @Mappings(
            value = {
                    @Mapping(
                            source = "buyDate",
                            target = "buyDate",
                            dateFormat = "yyyy-MM-dd HH:mm:ss"
                    ),
                    @Mapping(
                            source = "outDate",
                            target = "outDate",
                            dateFormat = "yyyy-MM-dd HH:mm:ss"
                    ),
            }
    )
    TblVehicleInfo toVehicleInfo(VehicleInfoExcelImportDTO vehicleInfoExcelImportDTO);
}
