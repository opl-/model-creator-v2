package me.opl.apps.modelcreator2.panel;

import javax.swing.JPanel;

import me.opl.libs.tablib.AbstractPanel;
import me.opl.libs.tablib.VariableProvider;

public class PropertiesPanel extends AbstractPanel {
	private JPanel panel;

	public PropertiesPanel(VariableProvider variableProvider) {
		super(variableProvider);

		panel = new JPanel();
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getTitle() {
		return "Properties";
	}
}
