package com.jukebox.entity;

import java.util.List;


public class Setting {
	private String id;
	private List<String> requires;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getRequires() {
		return requires;
	}
	public void setRequire(List<String> requires) {
		this.requires = requires;
	}
	
}
