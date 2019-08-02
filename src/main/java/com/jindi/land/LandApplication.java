package com.jindi.land;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.jindi.service", "com.jindi.land"})
@MapperScan("com.jindi.land.mapper")
@SpringBootApplication
public class LandApplication {
  public static void main(String[] args) {
    SpringApplication.run(LandApplication.class);
  }
}
