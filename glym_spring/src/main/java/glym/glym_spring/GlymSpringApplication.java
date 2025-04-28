package glym.glym_spring;

import glym.glym_spring.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "glym.glym_spring.domain.user.repository")
public class GlymSpringApplication implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(GlymSpringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("UserRepository: " + userRepository);
	}

}
