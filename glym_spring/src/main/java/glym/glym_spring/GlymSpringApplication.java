package glym.glym_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GlymSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlymSpringApplication.class, args);
	}

}
