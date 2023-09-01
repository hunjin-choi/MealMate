package service.chat.mealmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class MealmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(MealmateApplication.class, args);
	}

}
