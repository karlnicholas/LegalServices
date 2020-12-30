package opjpa;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import opca.service.OpinionViewSingleton;

@SpringBootApplication(scanBasePackages = {"opca", "opjpa"})
@EnableJpaRepositories(basePackages = {"opca"})
public class TestSlipOpinionComplete implements ApplicationRunner {

	Logger logger = Logger.getLogger(TestSlipOpinionComplete.class.getName());

	public static void main(String[] args) throws Exception {
		SpringApplication.run(TestSlipOpinionComplete.class, args);
	}
	
	@Autowired
	private OpinionViewSingleton slipOpinionSingleton;

	@Override
	public void run(ApplicationArguments args) throws Exception {
//		StatutesService statutesService = new StatutesServiceClientImpl("http://localhost:8090/");
        System.out.println(slipOpinionSingleton.checkStatus());
	}
}