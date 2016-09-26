package me.opl.apps.modelcreator2.panel;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import me.opl.apps.modelcreator2.viewport.ViewportComponentGL4.CameraMode;
import me.opl.apps.modelcreator2.viewport.ViewportComponentGL4.RenderMode;
import me.opl.apps.modelcreator2.viewport.ViewportComponentGL4;
import me.opl.libs.tablib.AbstractPanel;
import me.opl.libs.tablib.VariableProvider;

public class QuadViewportPanel extends AbstractPanel {
	private JPanel panel;

	private ViewportComponentGL4[] mvcs;

	public QuadViewportPanel(VariableProvider vp) {
		super(vp);

		mvcs = new ViewportComponentGL4[] {
			new ViewportComponentGL4(vp, CameraMode.ORTHO_WEST, RenderMode.MODEL),
			new ViewportComponentGL4(vp, CameraMode.ORTHO_UP, RenderMode.MODEL),
			new ViewportComponentGL4(vp, CameraMode.ORTHO_SOUTH, RenderMode.MODEL),
			new ViewportComponentGL4(vp, CameraMode.PERSPECTIVE_FREE, RenderMode.TEXTURED)
		};

		final JSplitPane topPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, mvcs[0], mvcs[1]);
		topPane.setDividerSize(1);
		topPane.setResizeWeight(0.5d);
		((BasicSplitPaneUI) topPane.getUI()).getDivider().setBorder(null);
		((BasicSplitPaneUI) topPane.getUI()).getSplitPane().setBorder(null);

		final JSplitPane bottomPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, mvcs[2], mvcs[3]);
		bottomPane.setDividerSize(1);
		bottomPane.setResizeWeight(0.5d);
		((BasicSplitPaneUI) bottomPane.getUI()).getDivider().setBorder(null);
		((BasicSplitPaneUI) bottomPane.getUI()).getSplitPane().setBorder(null);

		topPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent e) {
				bottomPane.setDividerLocation(topPane.getDividerLocation());
			}
		});

		bottomPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent e) {
				topPane.setDividerLocation(bottomPane.getDividerLocation());
			}
		});

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, topPane, bottomPane);
		splitPane.setDividerSize(1);
		splitPane.setResizeWeight(0.5d);
		((BasicSplitPaneUI) splitPane.getUI()).getDivider().setBorder(null);
		((BasicSplitPaneUI) splitPane.getUI()).getSplitPane().setBorder(null);

		panel = new JPanel(new BorderLayout());
		panel.add(splitPane, BorderLayout.CENTER);
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getTitle() {
		return "Quad Viewport";
	}
}
