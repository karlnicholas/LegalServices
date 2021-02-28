package opinionsrestca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"opinionsrestca", "opca"})
public class OpinionsRestCaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpinionsRestCaApplication.class, args);
	}

}
