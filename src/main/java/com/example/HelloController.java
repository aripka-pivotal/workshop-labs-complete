package com.example;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	private final AtomicInteger counter = new AtomicInteger();

	@RequestMapping("/")
	public String index() {
		return "Greetings from Omaha Spring Boot Starter App!";
	}

	@RequestMapping(value = "/hello-world")
	public @ResponseBody Greeting sayHello(@RequestParam(value = "name", defaultValue = "Stranger") String name) {
		return new Greeting(counter.incrementAndGet(), "Hello, " + name);
	}

}
