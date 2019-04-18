package com.test.poc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/poc")

public class MessageController {

	@RequestMapping(value = "/uploadImage", headers = ("content-type=multipart/form-data"), method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public String uploadAnImage(@RequestParam("file") MultipartFile multipartFile) {

		return PortalCustomisationService.addImage(multipartFile);
	}

	@RequestMapping(value = "/updateImage", headers = ("content-type=multipart/form-data"), method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public String updateAnImage(@RequestParam("file") MultipartFile multipartFile) {

		return PortalCustomisationService.addImage(multipartFile);
	}
	
	@RequestMapping(value = "/loadImage", method = RequestMethod.GET, produces = {MediaType.ALL_VALUE})
	public ResponseEntity<byte[]> readImage(HttpServletRequest httpServletRequest) {

		try {
			return PortalCustomisationService.readImage(httpServletRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
