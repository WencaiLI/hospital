package com.thtf.office.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.service.TblVehicleInfoService;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: liwencai
 * @Date: 2022/7/31 19:24
 * @Description: 公车Excel导入监听器 注意：Listener不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 */
public class VehicleExcelListener extends AnalysisEventListener<VehicleInfoExcelImportDTO> {

    /**
     * 临时存储解析数据
     */
    private final List<VehicleInfoExcelImportDTO> list = new ArrayList<>();

    /**
     * 每隔5条存储数据库（际使用中可以适当调整），然后清理list，方便内存回收
     */
    private static final int BATCH_COUNT = 5;

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private TblVehicleInfoService vehicleInfoService;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public VehicleExcelListener(TblVehicleInfoService vehicleInfoService) {
        this.vehicleInfoService = vehicleInfoService;
    }

    /**
     * @Author: liwencai
     * @Description: 每一条数据被解析后，都会来调用
     * @Date: 2022/8/1
     * @Param goods:
     * @Param analysisContext:
     * @return: void
     */
    @Override
    public void invoke(VehicleInfoExcelImportDTO goods, AnalysisContext analysisContext) {
        // 数据存储list中，供批量处理，或后续自己业务逻辑处理。
        list.add(goods);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if(list.size() >= BATCH_COUNT){
            saveData();
            list.clear();
        }
    }

    /**
     * @Author: liwencai
     * @Description: 所有数据解析完成了，都会调用
     * @Date: 2022/8/1
     * @Param analysisContext:
     * @return: void
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        vehicleInfoService.insertBatch(list);
    }
}
