package me.opl.apps.modelcreator2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.UIManager;

import me.opl.apps.modelcreator2.event.BlockStateOpenedEvent;
import me.opl.apps.modelcreator2.event.EventDispatcher;
import me.opl.apps.modelcreator2.event.EventHandler;
import me.opl.apps.modelcreator2.event.EventListener;
import me.opl.apps.modelcreator2.event.ModelOpenedEvent;
import me.opl.apps.modelcreator2.event.WindowClosedEvent;
import me.opl.apps.modelcreator2.importer.ImporterManager;
import me.opl.apps.modelcreator2.importer.MinecraftBlockModelmporter;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.BlockModel;
import me.opl.apps.modelcreator2.model.BlockState;
import me.opl.apps.modelcreator2.model.Cuboid;
import me.opl.apps.modelcreator2.model.CuboidElement;
import me.opl.apps.modelcreator2.model.Face;
import me.opl.apps.modelcreator2.model.ResourceLocation;
import me.opl.apps.modelcreator2.model.Texture;
import me.opl.apps.modelcreator2.tool.ToolManager;
import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.ViewportContextInitializer;

// FIXME: textures become black when resizing the panels
public class ModelCreator implements EventListener {
	public static final String VERSION = "1.0.0";

	private static ModelCreator instance;

	private ImporterManager importerManager;

	/**
	 * The RenderManager instance for rendering model viewports.
	 */
	private RenderManager renderManager = new RenderManager(new ViewportContextInitializer());
	private Map<BaseModel, ToolManager> toolManagers = new WeakHashMap<>();

	private List<ModelWindow> windows = new ArrayList<>();

	private List<BaseModel> models = new ArrayList<>();
	private List<BlockState> blockStates = new ArrayList<>();

	private EventDispatcher eventDispatcher = new EventDispatcher();

	public ModelCreator() {
		getEventDispatcher().registerListeners(this);

		importerManager = new ImporterManager(this);
		importerManager.addImporter(new MinecraftBlockModelmporter());

		init();
	}

	public static void main(String[] args) throws Exception {
		instance = new ModelCreator();

		// XXX: test model
		// cube.json
		BlockModel cubeModel = new BlockModel(instance, null, "cube");
		cubeModel.setHasElements(true);
		CuboidElement element = new CuboidElement();
		//element.getFragment().setFrom(new Position(0, 2, 3));
		//element.getFragment().setTo(new Position(15, 8, 12));
		for (Face f : Face.values()) {
			((Cuboid) element.getFragments()[0]).getFaceData(f).setTexture(new Texture(null, f.name().toLowerCase()));
		}
		cubeModel.addElement(element);

		// cube_all.json
		BlockModel cubeAllModel = new BlockModel(instance, cubeModel, "cube_all");
		cubeAllModel.addTexture(new Texture("particle", "all"));
		for (Face f : Face.values()) {
			cubeAllModel.addTexture(new Texture(f.name().toLowerCase(), "all"));
		}

		// coal_block.json
		BlockModel model = new BlockModel(instance, cubeAllModel, "coal_block");

		ResourceLocation resourceLocation = new ResourceLocation("minecraft:blocks/comparator_on");
		// TODO: do this load call elsewhere
		instance.getRenderManager().getResourceManager().loadTexture(resourceLocation);
		Texture tex1 = new Texture("all", resourceLocation);
		model.addTexture(tex1);
		Texture tex2 = new Texture("west", new ResourceLocation("minecraft:blocks/missingtex"));
		model.addTexture(tex2);

		instance.addModel(model);
	}

	private void init() {
		// TODO: load settings
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {e.printStackTrace();}

		createWindow();
	}

	public RenderManager getRenderManager() {
		return renderManager;
	}

	/**
	 * Returns a tool manager for the provided model.
	 *
	 * @param model Model with elements
	 * @return The {@link ToolManager} instance for the given model
	 * @throws IllegalArgumentException if the model doesn't have elements
	 */
	public ToolManager getToolManager(BaseModel model) {
		if (!model.hasElements()) throw new IllegalArgumentException("Tried to get a tool manager for a model with no elements");

		ToolManager toolManager = toolManagers.get(model);

		if (toolManager == null) {
			toolManager = new ToolManager();
			toolManagers.put(model, toolManager);
		}

		return toolManager;
	}

	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public BaseModel[] getModels() {
		return models.toArray(new BaseModel[models.size()]);
	}

	public int getModelIndex(BaseModel model) {
		return models.indexOf(model);
	}

	public void addModel(BaseModel model) {
		models.add(model);

		getEventDispatcher().fire(new ModelOpenedEvent(model));
	}

	public BaseModel getModelByReference(String reference) {
		String[] ref = reference.split("\0");
		if (ref.length != 2) return null;

		int index = Integer.parseInt(ref[1]);
		if (index < 0 || index >= models.size()) return null;

		BaseModel model = models.get(index);
		if (!model.getName().equals(ref[0])) return null;

		return model;
	}

	public String getModelReference(BaseModel model) {
		int index = models.indexOf(model);
		if (index < 0) return null;

		return model.getName() + '\0' + index;
	}

	public int getModelCount() {
		return models.size();
	}

	public BlockState[] getBlockStates() {
		return blockStates.toArray(new BlockState[blockStates.size()]);
	}

	public int getBlockStateIndex(BlockState blockState) {
		return blockStates.indexOf(blockState);
	}

	public void addBlockState(BlockState blockState) {
		blockStates.add(blockState);

		getEventDispatcher().fire(new BlockStateOpenedEvent(blockState));
	}

	public BlockState getBlockStateByReference(String reference) {
		String[] ref = reference.split("\0");
		if (ref.length != 2) return null;

		int index = Integer.parseInt(ref[1]);
		if (index < 0 || index >= blockStates.size()) return null;

		BlockState blockState = blockStates.get(index);
		if (!blockState.getName().equals(ref[0])) return null;

		return blockState;
	}

	public String getBlockStateReference(BlockState blockState) {
		int index = blockStates.indexOf(blockState);
		if (index < 0) return null;

		return blockState.getName() + '\0' + index;
	}

	public int getBlockStateCount() {
		return blockStates.size();
	}

	@EventHandler
	public void onWindowClosed(WindowClosedEvent event) {
		windows.remove(event.getModelWindow());

		if (windows.size() == 0) System.exit(0);
	}

	public ModelWindow createWindow() {
		ModelWindow win = new ModelWindow(this);

		windows.add(win);

		return win;
	}
}
