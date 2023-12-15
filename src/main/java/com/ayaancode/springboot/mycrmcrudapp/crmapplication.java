package com.ayaancode.springboot.mycrmcrudapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ayaancode.springboot.mycrmcrudapp")
public class crmapplication {

	public static void main(String[] args) {
		SpringApplication.run(crmapplication.class, args);
	}

}
