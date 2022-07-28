package com.thtf.office.dto;

import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.vo.VehicleCategoryParamVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-28T10:57:07+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class VehicleCategoryConvertImpl implements VehicleCategoryConvert {

    @Override
    public TblVehicleCategory toVehicleCategory(VehicleCategoryParamVO vehicleCategoryParamVO) {
        if ( vehicleCategoryParamVO == null ) {
            return null;
        }

        TblVehicleCategory tblVehicleCategory = new TblVehicleCategory();

        tblVehicleCategory.setId( vehicleCategoryParamVO.getId() );
        tblVehicleCategory.setName( vehicleCategoryParamVO.getName() );
        tblVehicleCategory.setDescription( vehicleCategoryParamVO.getDescription() );

        return tblVehicleCategory;
    }
}
