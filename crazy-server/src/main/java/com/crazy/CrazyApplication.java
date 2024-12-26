package com.crazy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CrazyApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrazyApplication.class, args);
    }
}
