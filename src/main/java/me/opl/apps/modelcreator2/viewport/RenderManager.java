package me.opl.apps.modelcreator2.viewport;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.jogamp.opengl.DebugGL3;
import com.jogamp.opengl.DefaultGLCapabilitiesChooser;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Fragment;
import me.opl.apps.modelcreator2.model.PlaybackState;
import me.opl.apps.modelcreator2.tool.Tool;
import me.opl.apps.modelcreator2.viewport.renderer.Renderer;
import me.opl.apps.modelcreator2.viewport.renderer.ToolRenderer;
import me.opl.apps.modelcreator2.viewport.resource.FramebufferResource;

// TODO: storing the renderes here doesnt work. do something with it
public class RenderManager implements GLEventListener {
	private ResourceManager resourceManager;
	private PlaybackState playbackState = new PlaybackState();

	public WeakHashMap<Fragment, Renderer> fragmentRenderers = new WeakHashMap<>();
	public WeakHashMap<Tool, ToolRenderer> toolRenderers = new WeakHashMap<>();
	public Map<Class<? extends Renderer>, Renderer> rendererInstances;

	private GLAutoDrawable sharedDrawable;
	private ContextInitializer contextInitializer;

	private FramebufferResource currentFramebuffer;

	public RenderManager(ContextInitializer contextInitializer) {
		this.contextInitializer = contextInitializer;

		resourceManager = new ResourceManager();

		rendererInstances = new HashMap<>();

		sharedDrawable = createAutoDrawable();
		sharedDrawable.addGLEventListener(this);

		sharedDrawable.display();
	}

	private GLAutoDrawable createAutoDrawable() {
		GLProfile p = GLProfile.getDefault();

		GLCapabilities caps = new GLCapabilities(p);
		caps.setOnscreen(false);
		caps.setHardwareAccelerated(true);
		caps.setDoubleBuffered(false);
		caps.setBackgroundOpaque(false);

		return GLDrawableFactory.getFactory(p).createOffscreenAutoDrawable(null, caps, new DefaultGLCapabilitiesChooser(), 5, 5);
	}
	
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void registerRenderer(Renderer renderer) {
		rendererInstances.put(renderer.getClass(), renderer);

//		if (!renderer.isReady()) resourceManager.addToUpdateList(renderer); // XXX
	}

	@SuppressWarnings("unchecked")
	public <T extends Renderer> T getRenderer(Class<T> rendererClass) {
		return (T) rendererInstances.get(rendererClass);
	}

	public Renderer getFragmentRenderer(BaseModel model, Fragment fragment) {
		Renderer renderer = fragmentRenderers.get(fragment);

		if (renderer == null) {
			renderer = fragment.createRenderer(this, model);
			fragmentRenderers.put(fragment, renderer);
		}

		return renderer;
	}

	public ToolRenderer getToolRenderer(Tool tool) {
		ToolRenderer renderer = toolRenderers.get(tool);

		if (renderer == null) {
			renderer = tool.createRenderer(this);

			if (renderer == null) return null;

			toolRenderers.put(tool, renderer);
		}

		return renderer;
	}

	// XXX: added synchronize to prevent crashes after computer wakes up. check if it works
	public synchronized void renderFramebuffer(FramebufferResource framebuffer) {
		if (this.currentFramebuffer != null) throw new IllegalStateException("Already rendering a framebuffer");

		this.currentFramebuffer = framebuffer;

		sharedDrawable.display();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();

		if (currentFramebuffer != null && !currentFramebuffer.isReady()) resourceManager.addToUpdateList(currentFramebuffer);

		resourceManager.processUpdateList(gl);

		for (Renderer r : rendererInstances.values()) if (!r.isReady()) r.prepare(gl);

		if (currentFramebuffer == null) return;

		try {
			currentFramebuffer.bind(gl);

			currentFramebuffer.getRenderer().render(gl, currentFramebuffer);

			currentFramebuffer.saveImage(gl);

			currentFramebuffer.unbind(gl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		currentFramebuffer = null;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		if (System.getProperty("modelcreator.debug") != null) drawable.setGL(new DebugGL3(drawable.getGL().getGL3()));

		GL3 gl = drawable.getGL().getGL3();

		if (contextInitializer != null) contextInitializer.init(gl, this);

//		resourceManager.init(gl); // TODO
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
//		GL3 gl = drawable.getGL().getGL3();

//		resourceManager.dispose(gl); // TODO
	}

	public PlaybackState getPlaybackState() {
		return playbackState;
	}

	@Override public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

	public static interface ContextInitializer {
		public void init(GL3 gl, RenderManager renderManager);
	}
}
