package kr.co.solpick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // 테스트 위해 Security 자동 설정 제외
@SpringBootApplication
@EnableScheduling // 스케줄링 기능 활성화
public class SolPickApplication {
	public static void main(String[] args) {
		SpringApplication.run(SolPickApplication.class, args);
	}
}