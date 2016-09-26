package me.opl.apps.modelcreator2;

import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import me.opl.apps.modelcreator2.event.EventHandler;
import me.opl.apps.modelcreator2.event.EventListener;
import me.opl.apps.modelcreator2.event.WindowClosedEvent;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.BlockState;

public class ModelCreator implements EventListener {
	public static final String VERSION = "1.0.0";

	private static ModelCreator instance;

	private List<ModelWindow> windows;

	private List<BaseModel> models;
	private List<BlockState> blockStates;
	

	public ModelCreator() {
		if (instance != null) throw new IllegalStateException("An instance of this class already exists.");
		instance = this;

		windows = new ArrayList<ModelWindow>();

		models = new ArrayList<BaseModel>();
		blockStates = new ArrayList<BlockState>();

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

		createWindow();
	}

	@EventHandler
	public void onWindowClosed(WindowClosedEvent event) {
		windows.remove(event.getModelWindow());

		if (windows.size() == 0) System.exit(0);
	}

	public ModelWindow createWindow() {
		ModelWindow win = new ModelWindow();

		win.getEventBus().registerListeners(this);

		windows.add(win);

		return win;
	}
}
