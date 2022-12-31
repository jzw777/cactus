package com.erebus.cactus.controller;

import com.alibaba.fastjson.JSONObject;
import com.erebus.cactus.utils.RocketSendUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {


    private final RocketSendUtil rocketSendUtil;


    @GetMapping("hi")
    public String sayHi(){
        return "hello world!";
    }

    @Tag(name = "首页模块")
    @RestController
    public class IndexController {

        @Parameter(name = "name",description = "姓名",required = true)
        @Operation(summary = "向客人问好")
        @GetMapping("/sayHi")
        public ResponseEntity<String> sayHi(@RequestParam(value = "name")String name){

            JSONObject object = new JSONObject();
            object.put("name","zhangsan");
            rocketSendUtil.sendMessage("aaa",object);

            return ResponseEntity.ok("Hi:"+name);
        }
    }
}
