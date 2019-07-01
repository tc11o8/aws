package com.smalleyes.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@ImportResource(locations = {"classpath:spring/applicationContext.xml"})
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableTransactionManagement(order = 2)
public class StartApplication {
	
	public static void main(String[] args) {
		
		SpringApplication.run(StartApplication.class, args);
	}
}
