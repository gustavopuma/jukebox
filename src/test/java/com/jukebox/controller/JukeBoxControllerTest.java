package com.jukebox.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jukebox.JukeBoxApplicationMock;
import com.jukebox.entity.JukeBox;
import com.jukebox.entity.Setting;
import com.jukebox.service.JukeBoxService;
import com.jukebox.service.SettingService;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
@WebMvcTest(JukeBoxController.class)
@ContextConfiguration(classes = JukeBoxApplicationMock.class)
public class JukeBoxControllerTest {

	@Autowired
	private MockMvc mvc;
	private Setting setting = createSetting();
	private List<JukeBox> jukeList = createJukeList();
	@MockBean
	private SettingService settingService;
	@MockBean
	private JukeBoxService jukeBoxService;

	@Before
	public void createObjects() {
		MockitoAnnotations.initMocks(this);
		createSetting();
		createJukeList();

	}

	private Setting createSetting() {
		setting = new Setting();
		setting.setId("3a6423cf-f226-4cb1-bf51-2954bc0941d1");
		setting.setRequire(Arrays.asList("speaker", "money_receiver"));
		return setting;
	}

	private List<JukeBox> createJukeList() {
		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(ClassLoader.getSystemResourceAsStream("juke.json"),
					mapper.getTypeFactory().constructCollectionType(List.class, JukeBox.class));
		} catch (IOException e) {
			return Collections.EMPTY_LIST;
		}
	}

	@Test
	public void testErrorNoSettingId() throws Exception {
		when(settingService.getSettingById(setting.getId())).thenReturn(Optional.of(setting));
		when(jukeBoxService.getJukeBoxByModel(Optional.empty())).thenReturn(jukeList);
		mvc.perform(get("/jukesBySetting").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

	}

	@Test
	public void testWithNoModel() throws Exception {
		when(settingService.getSettingById(eq(setting.getId()))).thenReturn(Optional.of(setting));
		when(jukeBoxService.getJukeBoxByModel(Optional.empty())).thenReturn(jukeList);
		mvc.perform(get("/jukesBySetting?settingId=" + setting.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo("5ca94a8acc046e7aa8040605")));

	}

	@Test
	public void testSettingNoRequiredPaginated() throws Exception {
		setting.setRequire(new ArrayList<String>());
		jukeList.stream().map(j->j.getId()).skip(10).limit(10).forEach(System.out::println);
		when(settingService.getSettingById(eq(setting.getId()))).thenReturn(Optional.of(setting));
		when(jukeBoxService.getJukeBoxByModel(Optional.empty())).thenReturn(jukeList);
		mvc.perform(get("/jukesBySetting?settingId=" + setting.getId()+"&offset=1&limit=10").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$[*].id").value(Matchers.containsInAnyOrder(jukeList.stream().map(j->j.getId()).skip(10).limit(10).toArray())));

	}

	@Test
	public void testSettingNoRequiredWithModel() throws Exception {
		System.out.println("Testing no required with model");
		setting.setRequire(new ArrayList<String>());
		when(settingService.getSettingById(eq(setting.getId()))).thenReturn(Optional.of(setting));
		when(jukeBoxService.getJukeBoxByModel(Optional.of(jukeList.get(2).getModel()))).thenReturn(jukeList.stream()
				.filter(j -> j.getModel().equals(jukeList.get(2).getModel())).collect(Collectors.toList()));
		mvc.perform(get("/jukesBySetting?settingId=" + setting.getId() + "&model=" + jukeList.get(2).getModel())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$[*].id").value(Matchers.containsInAnyOrder("5ca94a8ac470d3e47cd4713c",
						"5ca94a8a75c231bb18715112", "5ca94a8a3227b0a360f41078", "5ca94a8ab592da6c6f2d562e",
						"5ca94a8ad2d584257d25ae50", "5ca94a8adb81479f94dda744", "5ca94a8a0735998f945f7276",
						"5ca94a8a59b8061f89644f43", "5ca94a8ae2b3a4fb2f0cfd78", "5ca94a8ab2c1285e53a89991")));

	}

}
