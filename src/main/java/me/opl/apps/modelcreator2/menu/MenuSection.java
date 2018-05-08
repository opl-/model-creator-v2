package me.opl.apps.modelcreator2.menu;

import java.util.ArrayList;

public class MenuSection extends MenuItem {
	private ArrayList<MenuItem> items = new ArrayList<>();

	public MenuSection(String text) {
		super(text);
	}

	public MenuItem[] getItems() {
		return items.toArray(new MenuItem[items.size()]);
	}

	public void addItem(MenuItem mi) {
		items.add(mi);
	}

	public void addItem(MenuItem mi, int insertBeforeIndex) {
		items.add(insertBeforeIndex, mi);
	}
}
