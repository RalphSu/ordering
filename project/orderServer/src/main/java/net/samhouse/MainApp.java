package net.samhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

@SpringBootApplication
public class MainApp implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(MainApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    // main logic
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            System.out.println(args[0].toString());
        } else {
            System.out.println("welcome");
        }

        exit(0);
    }
}
