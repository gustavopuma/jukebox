package com.jukebox.controller;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.jukebox.entity.JukeBox;
import com.jukebox.entity.Setting;
import com.jukebox.service.JukeBoxService;
import com.jukebox.service.SettingService;

@RestController
public class JukeBoxController {

	@Autowired
	private SettingService settingService;
	@Autowired
	private JukeBoxService jukeboxService;

	@GetMapping("/jukesBySetting")
	public ResponseEntity<List<JukeBox>> getJukeBoxBySetting(@RequestParam String settingId,
			@RequestParam Optional<String> model, @RequestParam Optional<Integer> offset,
			@RequestParam Optional<Integer> limit) throws RestClientException, URISyntaxException {
		Optional<Setting> setting = settingService.getSettingById(settingId);
		if (setting.isPresent()) {
			List<JukeBox> jukes = jukeboxService.getJukeBoxByModel(model).stream()
					.filter(jb -> validateComponents(setting.get(), jb)).collect(Collectors.toList());
			System.out.println("model" + model);

			if (offset.isPresent() && limit.isPresent()) {
				PagedListHolder<JukeBox> holder = new PagedListHolder<JukeBox>(jukes);
				holder.setPage(offset.get());
				holder.setPageSize(limit.get());
				holder.getPageList().stream().map(j -> j.getId()).forEach(System.out::println);
				return new ResponseEntity<List<JukeBox>>(holder.getPageList(), HttpStatus.OK);
			} else if (offset.isPresent() || limit.isPresent()) {
				//todo message error when missing limit or offset
				//return new ResponseEntity<String>("offset or limit missing", HttpStatus.BAD_REQUEST);
				return null;
			} else {
				return new ResponseEntity<List<JukeBox>>(jukes, HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<List<JukeBox>>(Collections.EMPTY_LIST, HttpStatus.OK);
		}

	}

	private boolean validateComponents(Setting setting, JukeBox juke) {
		Map<String, Long> mapRequired = createMapComponents(setting.getRequires());
		Map<String, Long> mapComponents = createMapComponents(
				juke.getComponents().stream().map(c -> c.getName()).collect(Collectors.toList()));
		for (String component : setting.getRequires()) {
			if (!mapComponents.containsKey(component) || mapComponents.get(component) != mapRequired.get(component)) {
				return false;
			}
		}
		return true;
	}

	private Map<String, Long> createMapComponents(List<String> components) {
		return components.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
}
