package com.ablesky.asdeploy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/patchGroup")
public class PatchGroupController {

	@RequestMapping("/toList")
	public String toList() {
		return "patchGroup/list";
	}
	
	@RequestMapping("/toDetail")
	public String toDetail() {
		return "patchGroup/detail";
	}
}
