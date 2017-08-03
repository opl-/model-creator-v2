package me.opl.apps.modelcreator2.panel;

import javax.swing.JPanel;

import me.opl.apps.modelcreator2.MCVariableProvider;
import me.opl.apps.modelcreator2.panel.viewport.ViewportJPanel;
import me.opl.libs.tablib.AbstractPanel;

public class ViewportPanel extends AbstractPanel {
	private ViewportJPanel viewportComponent;

	public ViewportPanel(MCVariableProvider vp) {
		super(vp);

		viewportComponent = new ViewportJPanel(vp);
	}

	@Override
	public JPanel getPanel() {
		return viewportComponent;
	}

	@Override
	public String getTitle() {
		return "Viewport";
	}
}
