package com.example;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class HelloController {

	private final AtomicInteger counter = new AtomicInteger();

	@Value("${hello.configMessage:No Config Message}")
	private String configMessage;

	@Autowired
	private DiscoveryClient discoveryClient;

	@RequestMapping("/")
	public String index() {
		return "Greetings from Omaha Spring Boot Starter App!";
	}

	@RequestMapping(value = "/hello-world")
	public @ResponseBody Greeting sayHello(@RequestParam(value = "name", defaultValue = "Stranger") String name) {
		return new Greeting(counter.incrementAndGet(), "Hello, " + name);
	}

	@RequestMapping(value = "/hello-config")
	public String helloConfig() {
		return configMessage;
	}

	@RequestMapping("/fortune")
	@HystrixCommand(fallbackMethod="getDefaultFortune")
	public String getFortune() throws Exception {
		URI fortuneURI = getServiceUrl();
		String fortuneURIFull = fortuneURI + "/random";
		RestTemplate restTemplate = new RestTemplate();

		// Note we are cheating by not mapping to a local Object
		String fortune = restTemplate.getForObject(fortuneURIFull, String.class);

		return fortune;
	}

	public URI getServiceUrl() throws Exception {
		List<ServiceInstance> list = discoveryClient.getInstances("fortunes");
		if (list == null || list.size() == 0) {
			throw new Exception("No service instances found!");
		}
		return list.get(0).getUri();
	}

	public String getDefaultFortune() {
		return "Things not looking so great!";
	}

}
