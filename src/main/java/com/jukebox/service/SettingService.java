package com.jukebox.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jukebox.entity.Setting;
import com.jukebox.entity.SettingList;

@Service
public class SettingService {
	@Autowired
	private RestTemplate restTemplate;
	@Value("${settings.url}")
	private String settingUrl;
	
	
	public Optional<Setting> getSettingById(String id) {
		 
		SettingList response = restTemplate.getForObject(settingUrl, SettingList.class);
		return response.getSettings().stream().filter(s->s.getId().equals(id)).findAny();
	}
}
