package me.opl.apps.modelcreator2;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.json.JSONObject;

import me.opl.apps.modelcreator2.event.WindowClosedEvent;
import me.opl.apps.modelcreator2.event.WindowClosingEvent;
import me.opl.libs.tablib.TabLib;
import me.opl.libs.tablib.Template;
import me.opl.libs.tablib.Window;

public class ModelWindow implements WindowListener {
	public static final String VP_MODEL_WINDOW = "me.opl.apps.modelcreator2.ModelWindow.VP_MODEL_WINDOW";

	private ModelCreator modelCreator;

	private TabLib tabLib;
	private Window window;

	/**
	 * A window for model. Each model has it's own window that's used to edit
	 * it.
	 */
	public ModelWindow(ModelCreator modelCreator) {
		this.modelCreator = modelCreator;

		tabLib = new TabLib(new MCVariableProvider(modelCreator, this));

		createWindow();
	}

	/**
	 * Called only once in order to initiate the window.
	 *
	 * @throws IllegalStateException If the window is already initiated
	 */
	private void createWindow() throws IllegalStateException {
		if (window != null) throw new IllegalStateException("Window already initiated");

		// TODO: get the window from settings/resource file/disk/whatever

		// TODO: add default templates
		//Template template = new Template(tabLib, new JSONObject("{\"orientation\":1,\"bottom\":{\"panels\":[{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.PropertiesPanel\"}],\"type\":\"tpc\"},\"divider\":0.7,\"type\":\"splitPane\",\"dialogs\":[],\"templateFor\":\"window\",\"top\":{\"panels\":[{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.QuadModelViewPanel\"},{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.ModelViewPanel\"}],\"type\":\"tpc\"}}"));
		Template template = new Template(tabLib, new JSONObject("{\"orientation\":1,\"bottom\":{\"panels\":[{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.PropertiesPanel\"},{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.ModelListPanel\"}],\"type\":\"tpc\"},\"divider\":0.7,\"type\":\"splitPane\",\"dialogs\":[],\"templateFor\":\"window\",\"top\":{\"panels\":[{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.ViewportPanel\"},{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.QuadViewportPanel\"},{\"state\":null,\"class\":\"me.opl.apps.modelcreator2.panel.SettingsPanel\"}],\"type\":\"tpc\"}}"));

		window = tabLib.openNewWindow(template);

		window.setTitle("opl's Model Creator");

		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this);
	}

	public void closeWindow() {
		// TODO: add close confirmations, state saving, etc

		WindowClosedEvent event = new WindowClosedEvent(this);
		modelCreator.getEventDispatcher().fire(event);

		window.setVisible(false);
		window.dispose();
	}

	@Override
	public void windowClosing(WindowEvent event) {
		WindowClosingEvent closeEvent = new WindowClosingEvent(this);

		modelCreator.getEventDispatcher().fire(closeEvent);

		if (!closeEvent.isCancelled()) closeWindow();
	}

	@Override public void windowActivated(WindowEvent event) {}
	@Override public void windowClosed(WindowEvent event) {}
	@Override public void windowDeactivated(WindowEvent event) {}
	@Override public void windowDeiconified(WindowEvent event) {}
	@Override public void windowIconified(WindowEvent event) {}
	@Override public void windowOpened(WindowEvent event) {}
}
