package com.thtf.office.controller;

import com.thtf.office.common.response.JsonResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: liwencai
 * @Date: 2022/7/26 15:03
 * @Description: 公车管理Controller
 */
@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @PostMapping("/insert")
    public ResponseEntity<JsonResult> insert(){
        JsonResult jsonResult = new JsonResult();

        return ResponseEntity.ok(jsonResult);
    }
}
