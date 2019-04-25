package com.jukebox.entity;

import java.util.ArrayList;
import java.util.List;

public class JukeBox {

	private String id;
	private String model;
	private List<Component> components = new ArrayList<Component>();

	public JukeBox() {
		
	}
	
	public JukeBox(String id, String model, String... components) {
		this.id = id;
		this.model = model;
		for (String component : components) {
			this.components.add(new Component(component));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

}
