package opjpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import load.LoadHistoricalOpinions;

@SpringBootApplication(scanBasePackages = {"apimodel", "load", "loadmodel", "opca" })
@ConditionalOnProperty(name = "LoadOpinions.active", havingValue = "true", matchIfMissing = false)
public class LoadOpinions implements ApplicationRunner {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(LoadOpinions.class, args);
	}
	
	@Autowired
	private LoadHistoricalOpinions loadHistoricalOpinions;
	
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
    	loadHistoricalOpinions.initializeDB();
System.out.println("loadHistoricalOpinions.initializeDB(): DONE");
	}
        
}
