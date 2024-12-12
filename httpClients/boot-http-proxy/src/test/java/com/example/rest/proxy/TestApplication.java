package com.example.rest.proxy;

import com.example.rest.proxy.common.ContainersConfig;
import org.springframework.boot.SpringApplication;

class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(BatchApplication::main).with(ContainersConfig.class).run(args);
    }
}
