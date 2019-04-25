

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import com.jukebox.entity.JukeBox;

@SpringBootApplication
@ComponentScan(basePackages = {"com.jukebox"})
public class JukeBoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(JukeBoxApplication.class, args);
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public ParameterizedTypeReference<List<JukeBox>> parametizedTypeReference(){
		return new ParameterizedTypeReference<List<JukeBox>>() {};
	}

}
