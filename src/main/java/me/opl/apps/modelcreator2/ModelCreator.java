package me.opl.apps.modelcreator2;

import javax.swing.UIManager;

public class ModelCreator {
	public static final String VERSION = "1.0.0";

	public ModelCreator() {
		init();
	}

	public static void main(String[] args) {
		new ModelCreator();
	}

	public void init() {
		// TODO: load settings
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {e.printStackTrace();}

		new ModelWindow();
	}
}
