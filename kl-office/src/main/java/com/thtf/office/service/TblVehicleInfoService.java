package com.thtf.office.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.vo.VehicleSelectByDateResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆信息表 服务类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleInfoService extends IService<TblVehicleInfo> {

    List<TblVehicleInfo> select(VehicleInfoParamVO paramVO);

    boolean deleteById(Long vid);

    Map<String,Object> insert(TblVehicleInfo paramVO);

    Map<String,Object> insertBatch(List<VehicleInfoExcelImportDTO> list);

    boolean updateSpec(VehicleInfoParamVO paramVO);

    List<VehicleSelectByDateResult> selectByCidByDate(Long cid);

    boolean updateInfoStatus();

    /**
     * @Dsscription 批量导入进度
     * @return
     * @author guola
     */
    BigDecimal importProgress();

    /**
     * Excel批量导入车辆信息
     *
     * @param type 类型id
     * @param uploadFile 导入文件
     * @param user 操作用户主键
     * @param originalFilename 导入文件名称
     * @return {@link String} 导入情况说明
     * @author guola
     * @date 2022-06-14
     */
    String batchImport(MultipartFile uploadFile, String originalFilename, String type, String user);

    /**
     * @Author: liwencai
     * @Description: 模糊查询数据
     * @Date: 2022/8/4
     * @Param keywords:
     * @return: java.lang.Object
     */
    List<TblVehicleInfo> selectByKey(String keywords);

    /**/
    boolean verifyCarNumberForInsert(String carNumber);

    boolean verifyCategoryForInsert(String vehicleCategoryName);

    void importTemplateDownloadNew(HttpServletResponse response, List<?> list, Class<?> clazz);
}
