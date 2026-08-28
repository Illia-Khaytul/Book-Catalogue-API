package io.github.khaytul.illia.book_catalogue_api.user;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvTest {

    public EnvTest(Environment environment) {
        
        System.out.println("Spring:");
        System.out.println("DEV_DATABASE_USERNAME = " + environment.getProperty("DEV_DATABASE_USERNAME"));
        System.out.println("DEV_DATABASE_PASSWORD = " + environment.getProperty("DEV_DATABASE_PASSWORD"));
        System.out.println("DEV_DATABASE_NAME = " + environment.getProperty("DEV_DATABASE_NAME"));
        System.out.println("PROD_DATABASE_USERNAME = " + environment.getProperty("PROD_DATABASE_USERNAME"));
        System.out.println("PROD_DATABASE_PASSWORD = " + environment.getProperty("PROD_DATABASE_PASSWORD"));
        System.out.println("PROD_DATABASE_NAME = " + environment.getProperty("PROD_DATABASE_NAME"));

        System.out.println("OS:");
        System.out.println("DEV_DATABASE_NAME = " + System.getenv("DEV_DATABASE_NAME"));
        System.out.println("DEV_DATABASE_USERNAME = " + System.getenv("DEV_DATABASE_USERNAME"));
        System.out.println("DEV_DATABASE_PASSWORD = " + System.getenv("DEV_DATABASE_PASSWORD"));
        System.out.println("PROD_DATABASE_NAME = " + System.getenv("PROD_DATABASE_NAME"));
        System.out.println("PROD_DATABASE_USERNAME = " + System.getenv("PROD_DATABASE_USERNAME"));
        System.out.println("PROD_DATABASE_PASSWORD = " + System.getenv("PROD_DATABASE_PASSWORD"));
    }
}
