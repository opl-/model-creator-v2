package me.opl.apps.modelcreator2.panel;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import me.opl.libs.tablib.AbstractPanel;
import me.opl.libs.tablib.VariableProvider;

/*
 * == Viewport
 * - edit history length
 * - constant rendering
 * - Controls
 *   - Rotation sensitivity (x/y)
 *   - invert rotation (x/y)
 * - Appearance
 *   - bg color
 *   - boundary color (rgba)
 */

public class SettingsPanel extends AbstractPanel {
	private JPanel panel;

	public SettingsPanel(VariableProvider variableProvider) {
		super(variableProvider);

		createPanel();
	}

	private void createPanel() {
		panel = new JPanel(new BorderLayout());

		JList<String> categoryList = new JList<>(new String[] {"General", "Appearance", "Resource packs"});
		categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoryList.setSelectedIndex(0);

		JScrollPane settingsScrollPane = new JScrollPane(); 

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, categoryList, settingsScrollPane);
		splitPane.setDividerSize(3);
		panel.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(120);
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getTitle() {
		return "Settings";
	}
}
