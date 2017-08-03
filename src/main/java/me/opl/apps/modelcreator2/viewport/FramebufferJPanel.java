package me.opl.apps.modelcreator2.viewport;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import me.opl.apps.modelcreator2.viewport.resource.FramebufferResource;
import me.opl.apps.modelcreator2.viewport.resource.FramebufferResource.FramebufferRenderer;

public abstract class FramebufferJPanel extends JPanel implements ComponentListener {
	private static final long serialVersionUID = -5368991730302813165L;

	private RenderManager renderManager;

	private FramebufferResource framebuffer;

	public FramebufferJPanel(RenderManager renderManager) {
		this(renderManager, null);
	}

	public FramebufferJPanel(RenderManager renderManager, FramebufferRenderer renderer) {
		this.renderManager = renderManager;

		framebuffer = renderManager.getResourceManager().createFramebuffer(renderer, Math.max(1, getWidth()), Math.max(1, getHeight()));

		addComponentListener(this);
	}

	public void setFramebufferRenderer(FramebufferRenderer framebufferRenderer) {
		framebuffer.setRenderer(framebufferRenderer);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		renderManager.renderFramebuffer(framebuffer);

		g.drawImage(framebuffer.getImage(), 0, 0, null);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		framebuffer.setSize(getWidth(), getHeight());
	}

	@Override public void componentHidden(ComponentEvent e) {}
	@Override public void componentMoved(ComponentEvent e) {}
	@Override public void componentShown(ComponentEvent e) {}
}
