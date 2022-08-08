/**
 * @Description:
 * @author: lvgch
 * @date 2022-07-22 03:03:34 
 */
package com.thtf.office.controller;

import com.thtf.common.response.JsonResult;
import com.thtf.office.common.response.PageResult;
import com.thtf.office.common.util.FileUtil;
import com.thtf.office.dto.TblContractDTO;
import com.thtf.office.dto.TblContractRemindDTO;
import com.thtf.office.entity.TblContract;
import com.thtf.office.entity.TblContractRemind;
import com.thtf.office.entity.TblContractType;
import com.thtf.office.service.TblContractRemindService;
import com.thtf.office.service.TblContractService;
import com.thtf.office.service.TblContractTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 合同管理 controller
 * 
 * @author lvgch 
 * @date 2022-07-22 03:03:34
 */
@Slf4j
@RestController
@RequestMapping("/basic/contract/")
public class ContractController {
	@Resource
	private TblContractTypeService contractType;
	@Resource
	private TblContractService contractService;
	@Resource
	private TblContractRemindService contractRemindService;
	
	@Resource
	private FileUtil minioFileUtil;

	@Value("${minio.bucketName}")
	private String bucketName;
	// 合同类别

	/**
	 * 统计合同类型数量 allType:类别总数；allContract:合同总数
	 * 
	 * @return allType:类别总数；allContract:合同总数
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@GetMapping("/countContractType")
	public ResponseEntity<JsonResult<Map<String, Integer>>> countContractType() {

		JsonResult<Map<String, Integer>> jsonResult = new JsonResult<>();
		try {
			Map<String, Integer> result = contractType.countContractType();
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}

	/**
	 * 分页查询所有合同类型
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@GetMapping("/getAllContractTypePage")
	public ResponseEntity<JsonResult<PageResult<TblContractType>>> getAllContractTypePage(int currentPage,
																						  int pageSize) {

		JsonResult<PageResult<TblContractType>> jsonResult = new JsonResult<>();
		try {
			PageResult<TblContractType> result = contractType.getAllContractTypePage(currentPage, pageSize);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}

	/**
	 * 查询所有合同类别
	 * 
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@GetMapping("/getAllContractType")
	public ResponseEntity<JsonResult<List<TblContractType>>> getAllContractType() {

		JsonResult<List<TblContractType>> jsonResult = new JsonResult<>();
		try {
			List<TblContractType> result = contractType.getAllContractType();
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}

	/**
	 * 保存 更新 合同类别
	 * 
	 * @param entity
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@PostMapping("/saveOrUpdateContractType")
	public ResponseEntity<JsonResult<Boolean>> saveOrUpdateContractType(TblContractType entity) {

		JsonResult<Boolean> jsonResult = new JsonResult<>();
		try {
			Boolean result = contractType.saveOrUpdateEntity(entity);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}

	/**
	 * 删除合同类别 同时删除类别下的合同
	 * 
	 * @param id
	 *            合同类别id
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@DeleteMapping("/deleteContractTypeById")
	public ResponseEntity<JsonResult<Boolean>> deleteContractTypeById(Long id) {

		JsonResult<Boolean> jsonResult = new JsonResult<>();
		try {
			Boolean result = contractType.deleteEntityById(id);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}

	/**
	 * 移动多个合同 到其他类别下
	 * 
	 * @param contractTypeId
	 *            移动到合同类别ID
	 * @param contractIds
	 *            合同id 逗号分隔
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@PostMapping("/changeContractType")
	public ResponseEntity<JsonResult<Boolean>> changeContractType(Long contractTypeId, String contractIds) {

		JsonResult<Boolean> jsonResult = new JsonResult<>();
		try {
			Boolean result = contractType.changeContractType(contractTypeId, contractIds);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}

	// 合同
	/**
	 * 表头总览 all:合同总数 doing:执行中合同
	 * 
	 * @return all:合同总数 doing:执行中合同
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@GetMapping("/countContract")
	public ResponseEntity<JsonResult<Map<String, Integer>>> countContract() {

		JsonResult<Map<String, Integer>> jsonResult = new JsonResult<>();
		try {
			Map<String, Integer> result = contractService.countContract();
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
	/**
	 * 分页 按条件查询合同列表 
	 * @param contractTypeId 合同类别id
	 * @param state 合同状态
	 * @param keyWord  关键词
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@GetMapping("/getAllContractPage")
	public ResponseEntity<JsonResult<PageResult<TblContract>>> getAllContractPage(Long contractTypeId, Integer state , String keyWord, int currentPage, int pageSize) {

		JsonResult<PageResult<TblContract>> jsonResult = new JsonResult<>();
		try {
			PageResult<TblContract> result = contractService.getAllContractPage(contractTypeId,
					state,keyWord,currentPage, pageSize);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
	/**
	 * 保存/ 更新 合同信息
	 * @param dto 合同 dto
	 * @param file 合同 文件附件
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@PostMapping("/saveOrUpdateContract")
	public ResponseEntity<JsonResult<Boolean>> saveOrUpdateContract(TblContractDTO dto, MultipartFile file) {

		JsonResult<Boolean> jsonResult = new JsonResult<>();
		try {
			if (file != null) {

				InputStream inputStream = file.getInputStream();
				// 获取原始文件名
				String fileOriginalName = file.getOriginalFilename();

				// 上传文件
				boolean flag = minioFileUtil.uploadFileToMinio(inputStream, fileOriginalName);
				if (flag) {
					String url = minioFileUtil.getUrlByObjectName(fileOriginalName);
					dto.setFileUrl(url);
					dto.setFileName(fileOriginalName);
				}
			
			}
			Boolean result = contractService.saveOrUpdateEntity(dto);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
	/**
	 * 主键删除合同
	 * @param id 合同主键
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@DeleteMapping("/deleteContractById")
	public ResponseEntity<JsonResult<Boolean>> deleteContractById(Long id) {

		JsonResult<Boolean> jsonResult = new JsonResult<>();
		try {
			Boolean result = contractService.deleteEnityById(id);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
	
	/**
	 * 主键查询合同信息
	 * @param id 合同主键
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@GetMapping("/getContractById")
	public ResponseEntity<JsonResult<TblContractDTO>> getContractById(Long id) {

		JsonResult<TblContractDTO> jsonResult = new JsonResult<>();
		try {
			TblContractDTO result = contractService.getEntityById(id);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
	/**
	 *  统一设置合同xx天开始  xx天结束提醒
	 * @param startRemind  xxx 天开始执行合同
	 * @param endRemind  xxx天 合同到期
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@PostMapping("/setContractRemind")
	public ResponseEntity<JsonResult<Boolean>> setContractRemind(Integer startRemind,Integer endRemind) {

		JsonResult<Boolean> jsonResult = new JsonResult<>();
		try {
			Boolean result = contractService.setContractRemind(startRemind,endRemind);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
	// 合同提醒
	/**
	 * 根据 合同id 分页查询提醒事件
	 * @param contractId 合同id
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@GetMapping("/searchRemindListPageByContractId")
	public ResponseEntity<JsonResult<PageResult<TblContractRemind>>> searchRemindListPageByContractId(Long contractId, int currentPage, int pageSize) {

		JsonResult<PageResult<TblContractRemind>> jsonResult = new JsonResult<>();
		try {
			PageResult<TblContractRemind> result = contractService.searchRemindListPageByContractId(contractId,
					currentPage, pageSize);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
	/**
	 * 提醒事件处置
	 * @param dto
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@PostMapping("/updateContractRemind")
	public ResponseEntity<JsonResult<Boolean>> updateContractRemind(TblContractRemindDTO dto) {

		JsonResult<Boolean> jsonResult = new JsonResult<>();
		try {
			Boolean result = contractService.updateContractRemind(dto);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
	/**
	 * 用户id  查询我的提醒事件
	 * @param userId 用户id
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @author lvgch
	 * @date 2022-07-24
	 */
	@GetMapping("/getContractRemindByUserId")
	public ResponseEntity<JsonResult<PageResult<TblContractRemind>>> getContractRemindByUserId(Long userId,int currentPage, int pageSize) {

		JsonResult<PageResult<TblContractRemind>> jsonResult = new JsonResult<>();
		try {
			PageResult<TblContractRemind> result = contractService.getContractRemindByUserId(userId,
					currentPage, pageSize);
			jsonResult.setData(result);
			jsonResult.setStatus("success");
			jsonResult.setCode(200);
		} catch (Exception e) {
			jsonResult.setStatus("error");
			jsonResult.setCode(500);
			jsonResult.setDescription(e.getMessage());
			log.error((e.getMessage()));
		}
		return ResponseEntity.ok(jsonResult);
	}
}
