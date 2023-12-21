package com.androcode.barteni.RTAICalendarReforgeAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@RestController
public class RtaiCalendarReforgeApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(RtaiCalendarReforgeApiApplication.class, args);
	}
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@RequestMapping(value = "/api/message", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> index() {
		System.out.println("Received request at: /api/message");
		return Collections.singletonMap("message", IcsDownload.getICalJson());
	}
}
