package com.rosetta.image;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(value = "com.rosetta.image.mapper")
@EnableScheduling
public class PyramidApplication {

	public static void main(String[] args) {
		SpringApplication.run(PyramidApplication.class, args);
	}
}
