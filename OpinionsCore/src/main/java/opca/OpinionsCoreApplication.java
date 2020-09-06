package opca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"statutes", "statutesca"})
public class OpinionsCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpinionsCoreApplication.class, args);
	}

}
