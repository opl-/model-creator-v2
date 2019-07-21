package me.opl.apps.modelcreator2.menu;

import java.util.ArrayList;

public class MenuItem {
	private String text;
	private ArrayList<MenuActionListener> actionListeners = new ArrayList<>();

	public MenuItem(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void addActionListener(MenuActionListener listener) {
		actionListeners.add(listener);
	}

	public boolean removeActionListener(MenuActionListener listener) {
		return actionListeners.remove(listener);
	}

	public MenuActionListener[] getActionListeners() {
		return actionListeners.toArray(new MenuActionListener[actionListeners.size()]);
	}

	public void fireActionEvent() {
		for (MenuActionListener mal : actionListeners) {
			mal.onMenuAction(this);
		}
	}
}
