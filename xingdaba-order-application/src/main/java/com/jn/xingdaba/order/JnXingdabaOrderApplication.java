package com.jn.xingdaba.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.jn.*"})
@SpringBootApplication
public class JnXingdabaOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(JnXingdabaOrderApplication.class, args);
    }

}
