package com.thtf.office.feign;


import cn.hutool.system.UserInfo;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.dto.TblOrganizationDTO;
import com.thtf.office.dto.TblUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service

@FeignClient(value = "admin-server",url="10.10.82.33:8001")
//@FeignClient(value = "admin-server")
@RequestMapping("/admin-server")
public interface AdminAPI {

    /**
     * 根据职位查询用户
     *
     * @param positionTitle 职位
     * @return 用户集合
     * @author ligh
     */
    @GetMapping(value = "/user/searchUserByPosition")
    JsonResult<List<TblUser>> searchUserByPosition(@RequestParam("positionTitle") String positionTitle);

    /**
     * 根据code查询 名称包括所有上级
     *
     * @param code 机构编码
     * @author 邓玉磊
     * @Date 2020/7/10 10:31
     */
    @GetMapping("/user/searchOrganizationNameByCode")
    ResponseEntity<JsonResult<String>> searchOrganizationNameByCode(@RequestParam("code") String code);

    /**
     * 根据组织机构查询用户
     *
     * @param organizationCode 组织机构编码
     * @return {@link TblUser} 用户信息对象集合
     * @author deng
     * @date 2022-06-14
     */
    @GetMapping("/user/searchUserByOrganization")
    ResponseEntity<JsonResult<List<TblUser>>> searchUserByOrganization(@RequestParam(value = "organizationCode") String organizationCode);

    /**
     * @Description 列出组织机构树
     * @author guola
     * @date 2022-07-27
     **/
    @GetMapping("/user/findOrganizationTree")
    JsonResult<List<TblOrganizationDTO>> findOrganizationTree();
}