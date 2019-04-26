package com.jukebox.controller;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

import com.jukebox.entity.JukeBox;
import com.jukebox.entity.Setting;
import com.jukebox.service.JukeBoxService;
import com.jukebox.service.SettingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "JukeboxControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/api")
public class JukeBoxController {

	@Autowired
	private SettingService settingService;
	@Autowired
	private JukeBoxService jukeboxService;

	@GetMapping("/jukesBySetting")
	@ApiOperation("Get the paginated list of jukebox by settingId and model")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = JukeBox.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Required String parameter 'settingId' is not present"),
			@ApiResponse(code = 400, message = "Required int parameter 'limit or offset' is not present") })
	public ResponseEntity<List<JukeBox>> getJukeBoxBySetting(@RequestParam String settingId,
			@RequestParam Optional<String> model, @RequestParam Optional<Integer> offset,
			@RequestParam Optional<Integer> limit)
			throws RestClientException, URISyntaxException, MissingServletRequestParameterException {
		Optional<Setting> setting = settingService.getSettingById(settingId);
		if (setting.isPresent()) {
			List<JukeBox> jukes = jukeboxService.getJukeBoxByModel(model).stream()
					.filter(jb -> validateComponents(setting.get(), jb)).collect(Collectors.toList());
			

			if (offset.isPresent() && limit.isPresent()) {
				PagedListHolder<JukeBox> holder = new PagedListHolder<JukeBox>(jukes);
				holder.setPage(offset.get());
				holder.setPageSize(limit.get());
				return new ResponseEntity<List<JukeBox>>(holder.getPageList(), HttpStatus.OK);
			} else if (offset.isPresent() || limit.isPresent()) {
				throw new MissingServletRequestParameterException("limit or offset", "int");

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
			if (!mapComponents.containsKey(component) || mapComponents.get(component) < mapRequired.get(component)) {
				return false;
			}
		}
		return true;
	}

	@ExceptionHandler({ HttpClientErrorException.BadRequest.class })
	public ResponseEntity<Object> handleConstraintViolation(BadRequest ex, WebRequest request) {

		return new ResponseEntity<Object>(ex.getLocalizedMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	private Map<String, Long> createMapComponents(List<String> components) {
		return components.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
}
