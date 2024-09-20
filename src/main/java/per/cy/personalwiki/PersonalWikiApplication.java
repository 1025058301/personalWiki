package per.cy.personalwiki;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("per.cy.personalwiki.mapper")
@EnableScheduling
public class PersonalWikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalWikiApplication.class, args);
    }

}
