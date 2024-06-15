package net.security.data.microservicesocr;

import net.security.data.microservicesocr.Utils.DataBaseInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MicroservicesOcrApplication implements ApplicationRunner {

	@Autowired
	private DataBaseInitializer dataBaseInitializer;

	public static void main(String[] args) {
		SpringApplication.run(MicroservicesOcrApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		dataBaseInitializer.catalogInitializer();
	}
}
