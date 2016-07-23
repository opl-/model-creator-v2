package me.opl.apps.modelcreator2;

import javax.swing.JFrame;

import me.opl.apps.modelcreator2.event.EventDispatcher;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.libs.tablib.TabLib;
import me.opl.libs.tablib.Template;
import me.opl.libs.tablib.Window;

import org.json.JSONObject;

public class ModelWindow {
	public static final String VP_MODEL_WINDOW = "me.opl.apps.modelcreator2.ModelWindow.VP_MODEL_WINDOW";

	private TabLib tabLib;
	private Window window;

	private EventDispatcher eventBus;

	private BaseModel model;

	/**
	 * A window for model. Each model has it's own window that's used to edit
	 * it.
	 */
	public ModelWindow() {
		tabLib = new TabLib(new MCVariableProvider(this));
		tabLib.getVariableProvider().setObject(VP_MODEL_WINDOW, this);

		eventBus = new EventDispatcher();

		createWindow();
	}

	/**
	 * Called only once in order to initiate the window.
	 *
	 * @throws IllegalStateException If the window is already initiated
	 */
	private void createWindow() throws IllegalStateException {
		if (window != null) throw new IllegalStateException("Window already initiated.");

		// TODO: get the window from settings/file/disk/whatever

		// TODO: add default templates
		Template template = new Template(tabLib, new JSONObject("{\"orientation\":1,\"bottom\":{\"panels\":[{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.PropertiesPanel\"}],\"type\":\"tpc\"},\"divider\":0.7,\"type\":\"splitPane\",\"dialogs\":[],\"templateFor\":\"window\",\"top\":{\"panels\":[{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.QuadModelViewPanel\"},{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.ModelViewPanel\"}],\"type\":\"tpc\"}}"));

		window = tabLib.openNewWindow(template);

		window.setTitle("opl's Model Creator");

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // TODO: kill when last window closes
	}

	public void setModel(BaseModel model) {
		// TODO: fire event?
		this.model = model;
	}

	public BaseModel getModel() {
		return model;
	}

	public EventDispatcher getEventBus() {
		return eventBus;
	}
}
