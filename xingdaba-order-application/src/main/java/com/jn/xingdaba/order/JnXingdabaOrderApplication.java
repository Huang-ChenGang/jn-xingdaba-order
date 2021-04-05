package com.jn.xingdaba.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@ComponentScan(basePackages = {"com.jn.*"})
@SpringBootApplication
public class JnXingdabaOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(JnXingdabaOrderApplication.class, args);
    }

}
