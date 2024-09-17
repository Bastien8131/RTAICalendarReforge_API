package com.androcode.barteni.RTAICalendarReforgeAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RequestController {
	public static void main(String[] args) {
		SpringApplication.run(RequestController.class, args);
	}

	@RequestMapping(value = "/API-RTAICalReforge/events", produces = MediaType.APPLICATION_JSON_VALUE)
	public String allEvents() {
		System.out.println("Received request at: /API-RTAICalReforge/events");
		return IcsManager.getICalData().toString();
	}

	@RequestMapping(value = "/API-RTAICalReforge/events/days", produces = MediaType.APPLICATION_JSON_VALUE)
	public String eventsGroupeByDays() {
		System.out.println("Received request at: /API-RTAICalReforge/events/days");
		return IcsManager.getVEventsByDays().toString();
	}

	@RequestMapping(value = "/API-RTAICalReforge/events/months", produces = MediaType.APPLICATION_JSON_VALUE)
	public String eventsGroupeByMonths() {
		System.out.println("Received request at: /API-RTAICalReforge/events/months");
		return IcsManager.getVEventsByMonths().toString();
	}


	@RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
	public String test() {
		System.out.println("Received request at: /test");
		return "Hello World!";
	}


}
