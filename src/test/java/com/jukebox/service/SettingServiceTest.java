package com.jukebox.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jukebox.JukeBoxApplicationMock;
import com.jukebox.entity.Setting;
import com.jukebox.entity.SettingList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestPropertySource("classpath:application-test.properties")
public class SettingServiceTest {

	private SettingList settingList;
	@InjectMocks
	@Autowired
	private SettingService settingService;
	@Autowired
	@Mock
	private RestTemplate restTemplate;
	@Value("${settings.url}")
	private String settingUrl;
	private Setting selectSetting;
	@Before
	public void initObjects() throws JsonParseException, JsonMappingException, IOException {
		MockitoAnnotations.initMocks(this);
		ObjectMapper mapper = new ObjectMapper();
		settingList = mapper.readValue(ClassLoader.getSystemResourceAsStream("settings.json"), SettingList.class);
		selectSetting = settingList.getSettings().get(0);
	}

	@Test
	public void test() {
		Mockito.when(restTemplate.getForObject(eq(settingUrl), eq(SettingList.class))).thenReturn(settingList);
		Setting setting = settingService.getSettingById(selectSetting.getId()).get();
		assertEquals(selectSetting.getId(), setting.getId());
		assertEquals(selectSetting.getRequires(), setting.getRequires());
	}

	@Configuration
	@Import({ JukeBoxApplicationMock.class })
	static class ContextConfiguration {
	}
}
