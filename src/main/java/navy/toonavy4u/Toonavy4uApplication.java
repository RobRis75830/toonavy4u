package navy.toonavy4u;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("entities")
@EnableJpaRepositories(basePackages = "repositories")
@SpringBootApplication
public class Toonavy4uApplication {

    public static void main(String[] args) {
        SpringApplication.run(Toonavy4uApplication.class, args);
    }


}
