package com.thtf.office.dto;

import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-28T10:57:07+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class VehicleSchedulingConvertImpl implements VehicleSchedulingConvert {

    @Override
    public TblVehicleScheduling toVehicleScheduling(VehicleSchedulingParamVO vehicleSchedulingParamVO) {
        if ( vehicleSchedulingParamVO == null ) {
            return null;
        }

        TblVehicleScheduling tblVehicleScheduling = new TblVehicleScheduling();

        tblVehicleScheduling.setId( vehicleSchedulingParamVO.getId() );
        tblVehicleScheduling.setCode( vehicleSchedulingParamVO.getCode() );
        tblVehicleScheduling.setVehicleCategoryId( vehicleSchedulingParamVO.getVehicleCategoryId() );
        tblVehicleScheduling.setDescription( vehicleSchedulingParamVO.getDescription() );
        tblVehicleScheduling.setVehicleInfoId( vehicleSchedulingParamVO.getVehicleInfoId() );
        tblVehicleScheduling.setCarNumber( vehicleSchedulingParamVO.getCarNumber() );
        tblVehicleScheduling.setStartTime( vehicleSchedulingParamVO.getStartTime() );
        tblVehicleScheduling.setEndTime( vehicleSchedulingParamVO.getEndTime() );
        tblVehicleScheduling.setDriverName( vehicleSchedulingParamVO.getDriverName() );
        tblVehicleScheduling.setPurpose( vehicleSchedulingParamVO.getPurpose() );
        tblVehicleScheduling.setOrganizationId( vehicleSchedulingParamVO.getOrganizationId() );
        tblVehicleScheduling.setUserName( vehicleSchedulingParamVO.getUserName() );
        tblVehicleScheduling.setDestination( vehicleSchedulingParamVO.getDestination() );
        tblVehicleScheduling.setDriverId( vehicleSchedulingParamVO.getDriverId() );
        tblVehicleScheduling.setOrganizationName( vehicleSchedulingParamVO.getOrganizationName() );

        return tblVehicleScheduling;
    }
}
