package com.polaris.subject.application.controller;

import com.alibaba.fastjson.JSON;
import com.polaris.subject.common.entity.PageResult;
import com.polaris.subject.infra.basic.entity.SubjectInfoEs;
import com.polaris.subject.infra.basic.service.SubjectEsService;
import com.polaris.subject.infra.entity.UserInfo;
import com.polaris.subject.infra.rpc.UserRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 刷题分类controller
 *
 * @author Polaris
 * @date 2023/10/1
 */
@RestController
@RequestMapping("/subject/category")
@Slf4j
public class TestFeignController {

    @Resource
    private UserRpc userRpc;

    @GetMapping("testFeign")
    public void testFeign() {
        UserInfo userInfo = userRpc.getUserInfo("jichi");
        log.info("testFeign.userInfo:{}", userInfo);
    }

}
