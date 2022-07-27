package com.thtf.office.dto;

import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.vo.VehicleMaintenanceParamVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-27T17:04:34+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class VehicleMaintenanceConvertImpl implements VehicleMaintenanceConvert {

    @Override
    public TblVehicleMaintenance toVehicleMaintenance(VehicleMaintenanceParamVO vehicleMaintenanceParamVO) {
        if ( vehicleMaintenanceParamVO == null ) {
            return null;
        }

        TblVehicleMaintenance tblVehicleMaintenance = new TblVehicleMaintenance();

        tblVehicleMaintenance.setId( vehicleMaintenanceParamVO.getId() );
        tblVehicleMaintenance.setVehicleInfoId( vehicleMaintenanceParamVO.getVehicleInfoId() );
        tblVehicleMaintenance.setName( vehicleMaintenanceParamVO.getName() );
        tblVehicleMaintenance.setMaintenanceTime( vehicleMaintenanceParamVO.getMaintenanceTime() );
        tblVehicleMaintenance.setMoneySpent( vehicleMaintenanceParamVO.getMoneySpent() );
        tblVehicleMaintenance.setHandledBy( vehicleMaintenanceParamVO.getHandledBy() );
        tblVehicleMaintenance.setDescription( vehicleMaintenanceParamVO.getDescription() );

        return tblVehicleMaintenance;
    }
}
