package home.assignment.messageboard;

import home.assignment.messageboard.configuration.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class MessageBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageBoardApplication.class, args);
	}

}
