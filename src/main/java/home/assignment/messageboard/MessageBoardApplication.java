package home.assignment.messageboard;

import home.assignment.messageboard.configuration.ApplicationProperties;
import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.ResourceBundle;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class MessageBoardApplication {

	public static void main(String[] args) {
		ResourceBundle applicationProperties = ResourceBundle.getBundle("application");
		Flyway flyway = Flyway.configure().dataSource(applicationProperties.getString("spring.datasource.url"),
				applicationProperties.getString("spring.datasource.username"),
				applicationProperties.getString("spring.datasource.password"))
				.load();
		flyway.migrate();

		SpringApplication.run(MessageBoardApplication.class, args);
	}

}
