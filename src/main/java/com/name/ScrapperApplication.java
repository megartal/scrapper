package com.name;

import com.name.scrappers.Scrapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScrapperApplication implements CommandLineRunner{

	private final Scrapper scrapper;

	public ScrapperApplication(Scrapper scrapper) {
		this.scrapper = scrapper;
	}

	@Override
	public void run(String... strings) throws Exception {
		scrapper.start();
	}

	public static void main(String[] args) {
		SpringApplication.run(ScrapperApplication.class, args);
	}
}
