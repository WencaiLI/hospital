package com.thtf.office.dto.converter;

import com.thtf.office.dto.VehicleInfoExcelErrorImportDTO;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @Author: liwencai
 * @Date: 2022/8/8 20:48
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface VehicleInfoExcelErrorImportConverter {
    @Mappings(
        value = {
            @Mapping(
                source = "buyDate",
                target = "buyDate",
                dateFormat = "yyyy-MM-dd"
            ),
            @Mapping(
                    source = "outDate",
                    target = "outDate",
                    dateFormat = "yyyy-MM-dd"
            ),
        }
    )
    VehicleInfoExcelErrorImportDTO toErrorImport(VehicleInfoExcelImportDTO vehicleInfoExcelImportDTO);
}
