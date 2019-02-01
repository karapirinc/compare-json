package com.tr.karapirinc.comparejson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/*
TODO improve README
TODO seperate integration ve unit tests with spring profilers
TODO add gradle support
 */
@SpringBootApplication(scanBasePackages = "com.tr.karapirinc")
public class CompareJsonApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompareJsonApplication.class, args);
	}

}

