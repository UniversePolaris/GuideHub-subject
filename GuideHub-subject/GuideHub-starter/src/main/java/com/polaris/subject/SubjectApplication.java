package com.polaris.subject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 刷题微服务启动类
 *
 * @author Polaris
 * @date 2023/10/1
 */
@SpringBootApplication
@ComponentScan("com.polaris")
@MapperScan("com.polaris.**.mapper")
@EnableFeignClients(basePackages = "com.polaris")
public class SubjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubjectApplication.class);
    }

}
