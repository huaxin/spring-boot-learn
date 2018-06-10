package me.lianghuaxin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"me.lianghuaxin.dao"})
public class SpringBootDatasourcesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDatasourcesApplication.class, args);
    }
}
