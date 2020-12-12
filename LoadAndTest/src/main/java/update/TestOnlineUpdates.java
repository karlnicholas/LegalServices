package update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TestOnlineUpdates implements ApplicationRunner {
	public static void main(String... args) {
		new SpringApplicationBuilder(TestOnlineUpdates.class).run(args);
	}

	@Autowired
	private TestOnlineUpdatesService service;
	@Override
	public void run(ApplicationArguments args) {
		service.testOnlineUpdates();
	}
}