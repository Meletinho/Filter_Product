package com.saas.filtro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration.class
})
public class FilterProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(FilterProductApplication.class, args);
    }
}
