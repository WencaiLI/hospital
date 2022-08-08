package com.thtf.office.dto.converter;

import com.thtf.office.dto.VehicleInfoExcelErrorImportDTO;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import org.mapstruct.Mapper;

/**
 * @Author: liwencai
 * @Date: 2022/8/8 20:48
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface VehicleInfoExcelErrorImportConverter {
    VehicleInfoExcelErrorImportDTO toErrorImport(VehicleInfoExcelImportDTO vehicleInfoExcelImportDTO);
}
