package com.thtf.office.dto;

import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.vo.VehicleInfoParamVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-28T10:57:07+0800",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class VehicleInfoConvertImpl implements VehicleInfoConvert {

    @Override
    public TblVehicleInfo toVehicleInfo(VehicleInfoParamVO vehicleInfoParamVO) {
        if ( vehicleInfoParamVO == null ) {
            return null;
        }

        TblVehicleInfo tblVehicleInfo = new TblVehicleInfo();

        tblVehicleInfo.setId( vehicleInfoParamVO.getId() );
        tblVehicleInfo.setCarNumber( vehicleInfoParamVO.getCarNumber() );
        tblVehicleInfo.setVehicleCategoryId( vehicleInfoParamVO.getVehicleCategoryId() );
        tblVehicleInfo.setModel( vehicleInfoParamVO.getModel() );
        tblVehicleInfo.setEngineNumber( vehicleInfoParamVO.getEngineNumber() );
        tblVehicleInfo.setFrameNumber( vehicleInfoParamVO.getFrameNumber() );
        tblVehicleInfo.setColor( vehicleInfoParamVO.getColor() );
        tblVehicleInfo.setCarImage( vehicleInfoParamVO.getCarImage() );
        tblVehicleInfo.setCarImageUrl( vehicleInfoParamVO.getCarImageUrl() );
        tblVehicleInfo.setDrivingBookImage( vehicleInfoParamVO.getDrivingBookImage() );
        tblVehicleInfo.setDrivingBookImageUrl( vehicleInfoParamVO.getDrivingBookImageUrl() );
        tblVehicleInfo.setDistributor( vehicleInfoParamVO.getDistributor() );
        tblVehicleInfo.setOutDate( vehicleInfoParamVO.getOutDate() );
        tblVehicleInfo.setBuyDate( vehicleInfoParamVO.getBuyDate() );
        tblVehicleInfo.setPrice( vehicleInfoParamVO.getPrice() );
        tblVehicleInfo.setInsurance( vehicleInfoParamVO.getInsurance() );
        tblVehicleInfo.setMaintenance( vehicleInfoParamVO.getMaintenance() );
        tblVehicleInfo.setDescription( vehicleInfoParamVO.getDescription() );
        tblVehicleInfo.setStatus( vehicleInfoParamVO.getStatus() );

        return tblVehicleInfo;
    }
}
