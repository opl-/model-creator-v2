package me.opl.apps.modelcreator2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.json.JSONObject;
import org.json.JSONTokener;

import me.opl.apps.modelcreator2.event.WindowClosedEvent;
import me.opl.apps.modelcreator2.event.WindowClosingEvent;
import me.opl.apps.modelcreator2.menu.MenuItem;
import me.opl.apps.modelcreator2.menu.MenuItemSeparator;
import me.opl.apps.modelcreator2.menu.MenuSection;
import me.opl.libs.tablib.TabLib;
import me.opl.libs.tablib.Template;
import me.opl.libs.tablib.Window;

public class ModelWindow implements WindowListener {
	public static final String VP_MODEL_WINDOW = "me.opl.apps.modelcreator2.ModelWindow.VP_MODEL_WINDOW";

	private ModelCreator modelCreator;

	private TabLib tabLib;
	private Window window;

	/**
	 * A window for model. Each model has its own window that's used to edit
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
		Template template = new Template(tabLib, new JSONObject(new JSONTokener(getClass().getResourceAsStream("/windowlayout/default.json"))));

		window = tabLib.openNewWindow(template);

		window.setTitle("opl's Model Creator");

		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this);

		updateMenuBar();
	}

	private JMenu makeMenu(MenuSection section) {
		JMenu menu = new JMenu(section.getText());

		for (final MenuItem mi : section.getItems()) {
			if (mi instanceof MenuSection) {
				menu.add(makeMenu((MenuSection) mi));
			} else if (mi instanceof MenuItemSeparator) {
				menu.addSeparator();
			} else {
				JMenuItem jmi = new JMenuItem(mi.getText());

				jmi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						mi.fireActionEvent();
					}
				});

				menu.add(jmi);
			}
		}

		return menu;
	}

	public void updateMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		for (MenuSection ms : modelCreator.getWindowMenuManager().getSections()) {
			menuBar.add(makeMenu(ms));
		}

		window.setJMenuBar(menuBar);
		window.pack();
	}

	public void closeWindow() {
		// TODO: add close confirmations, state saving, etc

		WindowClosedEvent event = new WindowClosedEvent(this);
		modelCreator.getGlobalEventDispatcher().fire(event);

		window.setVisible(false);
		window.dispose();
	}

	@Override
	public void windowClosing(WindowEvent event) {
		WindowClosingEvent closeEvent = new WindowClosingEvent(this);

		modelCreator.getGlobalEventDispatcher().fire(closeEvent);

		if (!closeEvent.isCancelled()) closeWindow();
	}

	@Override public void windowActivated(WindowEvent event) {}
	@Override public void windowClosed(WindowEvent event) {}
	@Override public void windowDeactivated(WindowEvent event) {}
	@Override public void windowDeiconified(WindowEvent event) {}
	@Override public void windowIconified(WindowEvent event) {}
	@Override public void windowOpened(WindowEvent event) {}
}
