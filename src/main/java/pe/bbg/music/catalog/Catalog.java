package pe.bbg.music.catalog;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pe.bbg.music.catalog.service.CatalogService;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class Catalog {

	public static void main(String[] args) {
		SpringApplication.run(Catalog.class, args);
	}

	@Bean
	public ApplicationRunner initializer(CatalogService catalogService) {
		return args -> {
			// Trigger scan asynchronously on startup
			CompletableFuture.runAsync(catalogService::scanLibrary);
		};
	}

}
