package me.opl.apps.modelcreator2.panel.viewport;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import me.opl.apps.modelcreator2.MCVariableProvider;
import me.opl.apps.modelcreator2.event.EventHandler;
import me.opl.apps.modelcreator2.event.EventListener;
import me.opl.apps.modelcreator2.event.ModelOpenedEvent;
import me.opl.apps.modelcreator2.viewport.FramebufferJPanel;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer.CameraMode;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer.RenderMode;

public class ViewportJPanel extends FramebufferJPanel implements EventListener {
	private static final long serialVersionUID = 2777075656197343144L;

	private ViewportFramebufferRenderer viewportFramebufferRenderer;

	private ViewportController controller;

	public ViewportJPanel(MCVariableProvider vp) {
		this(vp, CameraMode.PERSPECTIVE_FREE, RenderMode.TEXTURED);
	}

	public ViewportJPanel(MCVariableProvider vp, CameraMode cameraMode, RenderMode renderMode) {
		super(vp.getModelCreator().getRenderManager());

		setTransferHandler(new ModelTransferHandler());

		viewportFramebufferRenderer = new ViewportFramebufferRenderer(vp.getModelCreator(), cameraMode, renderMode);
		setFramebufferRenderer(viewportFramebufferRenderer);

		vp.getModelCreator().getGlobalEventDispatcher().registerListeners(this);

		controller = new ViewportController(vp.getModelCreator(), this, viewportFramebufferRenderer);

		// TODO: make a single global repaint call?
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					repaint();

					try {Thread.sleep(33);}
					catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}).start();
	}

	// XXX: temporary way to "open" a model
	@EventHandler
	public void onModelOpened(ModelOpenedEvent event) {
		viewportFramebufferRenderer.setDisplayedModel(event.getModel());
	}

	// TODO: change this name. just please. change it
	public ViewportFramebufferRenderer getViewportFramebufferRenderer() {
		return viewportFramebufferRenderer;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		String cameraModeButtonText = viewportFramebufferRenderer.getCameraMode().getDisplayName() + " \u25be";
		g.setColor(Color.BLACK);
		g.drawString(cameraModeButtonText, 4, 13);
		g.setColor(Color.WHITE);
		g.drawString(cameraModeButtonText, 3, 12);
	}

	// TODO: finish drag and drop
	public static class ModelTransferHandler extends TransferHandler {
		private static final long serialVersionUID = -5320773440019500573L;

		@Override
		public boolean canImport(TransferSupport support) {
			System.out.println(support);

			for (DataFlavor f : support.getDataFlavors()) {
				System.out.println(f.getHumanPresentableName() + " "  + f.getMimeType());
			}

			return false;
		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			return false;
		}
	}
}
