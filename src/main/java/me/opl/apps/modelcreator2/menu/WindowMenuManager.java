package me.opl.apps.modelcreator2.menu;

import java.util.ArrayList;

public class WindowMenuManager {
	private ArrayList<MenuSection> sections = new ArrayList<>();

	public MenuSection[] getSections() {
		return sections.toArray(new MenuSection[sections.size()]);
	}

	public void addSection(MenuSection menuSection) {
		sections.add(menuSection);
	}
}
