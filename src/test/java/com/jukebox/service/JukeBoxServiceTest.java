package com.jukebox.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jukebox.JukeBoxApplicationMock;
import com.jukebox.entity.JukeBox;

public class JukeBoxServiceTest {

	private List<JukeBox> mockedList;

	@InjectMocks
	@Autowired
	private JukeBoxService jukeBoxService;
	@Autowired
	@Mock
	private RestTemplate restTemplate;
	@Value("${jukebox.url}")
	private String jukeBoxUrl;
	@Autowired
	private ParameterizedTypeReference<List<JukeBox>> paramTypeJuke;
	private JukeBox selectedJukeBox;
	private List<JukeBox> expectedFilteredList;
	
	@Before
	public void initObjects() throws JsonParseException, JsonMappingException, IOException {
		MockitoAnnotations.initMocks(this);
		ObjectMapper mapper = new ObjectMapper();
		mockedList = mapper.readValue(ClassLoader.getSystemResourceAsStream("juke.json"), mapper.getTypeFactory().constructCollectionType(List.class, JukeBox.class));
		selectedJukeBox = mockedList.get(0);
		expectedFilteredList = mockedList.stream().filter(jb->selectedJukeBox.getModel().equals(jb.getModel())).collect(Collectors.toList());
	}

	@Test
	public void testFilterModel() {
		Mockito.when(restTemplate.exchange(eq(jukeBoxUrl), eq(HttpMethod.GET),eq(null),eq(paramTypeJuke))).thenReturn(new ResponseEntity<List<JukeBox>>(mockedList,HttpStatus.OK));
		List<JukeBox> list = jukeBoxService.getJukeBoxByModel(Optional.of(selectedJukeBox.getModel()));
		assertEquals(expectedFilteredList, list);
	}
	
	@Test
	public void testAll() {
		Mockito.when(restTemplate.exchange(eq(jukeBoxUrl), eq(HttpMethod.GET),eq(null),eq(paramTypeJuke))).thenReturn(new ResponseEntity<List<JukeBox>>(mockedList,HttpStatus.OK));
		List<JukeBox> list = jukeBoxService.getJukeBoxByModel(Optional.empty());
		assertEquals(mockedList, list);
	}

	@Configuration
	@Import({ JukeBoxApplicationMock.class })
	static class ContextConfiguration {
	}

}
