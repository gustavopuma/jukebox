package com.jukebox;

import java.util.List;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import com.jukebox.entity.JukeBox;

@Configuration
@ComponentScan(basePackages = {"com.jukebox"})
public class JukeBoxApplicationMock { 
	@Mock
	private RestTemplate restTemplate;



	@Bean
	public RestTemplate getRestTemplate() {
		return Mockito.mock(RestTemplate.class);
	}
	@Bean
	public ParameterizedTypeReference<List<JukeBox>> getParametizedTypeReference(){
		return new ParameterizedTypeReference<List<JukeBox>>() {};
	}

}
