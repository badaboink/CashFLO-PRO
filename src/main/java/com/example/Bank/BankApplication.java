package com.example.Bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) {

		ApplicationContext applicationContext = SpringApplication.run(BankApplication.class, args);
//		for (String name : applicationContext.getBeanDefinitionNames()) {
//			System.out.println(name);
//		}
	}

}
