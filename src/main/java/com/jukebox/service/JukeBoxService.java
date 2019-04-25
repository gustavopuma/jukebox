package com.jukebox.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jukebox.entity.JukeBox;

@Service
public class JukeBoxService {

		@Autowired
		private RestTemplate restTemplate;
		@Value("${jukebox.url}")
		private String jukeBoxUrl;
		@Autowired
		private ParameterizedTypeReference<List<JukeBox>> parametizedTypeReference;
		
		public List<JukeBox> getJukeBoxByModel(Optional<String> modelName) {
		
			ResponseEntity<List<JukeBox>> response = restTemplate.exchange(jukeBoxUrl, HttpMethod.GET,null,parametizedTypeReference);
 			if(modelName.isPresent()) {
				return response.getBody().stream().filter(jb->modelName.get().equals(jb.getModel())).collect(Collectors.toList());
			}
			else {
				return response.getBody();
			}
			
		}
}
