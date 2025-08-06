package com.smhrd.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//아래 어노테이션이 있는 클래스를 기준으로
//하위에 있는 클래스들만 실제 동작을 시킬 수 있음 (객체화 시켜줌)
@SpringBootApplication
public class BootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootApplication.class, args);
	}

}
