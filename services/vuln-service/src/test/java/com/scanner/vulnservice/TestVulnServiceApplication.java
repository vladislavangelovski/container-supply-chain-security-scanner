package com.scanner.vulnservice;

import org.springframework.boot.SpringApplication;

public class TestVulnServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(VulnServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
