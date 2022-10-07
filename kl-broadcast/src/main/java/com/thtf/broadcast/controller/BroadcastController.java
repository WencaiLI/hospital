package com.thtf.broadcast.controller;

import com.thtf.broadcast.service.BroadcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 13:43
 * @Description:
 */
@RestController
@RequestMapping("/broadcast")
public class BroadcastController {

    @Autowired
    BroadcastService broadcastService;

}
