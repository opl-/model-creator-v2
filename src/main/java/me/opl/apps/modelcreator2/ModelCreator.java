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
import me.opl.apps.modelcreator2.exporter.ExporterManager;
import me.opl.apps.modelcreator2.importer.ImporterManager;
import me.opl.apps.modelcreator2.menu.ContextMenuManager;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.BlockModel;
import me.opl.apps.modelcreator2.model.BlockState;
import me.opl.apps.modelcreator2.model.Cuboid;
import me.opl.apps.modelcreator2.model.CuboidElement;
import me.opl.apps.modelcreator2.model.Face;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.ResourceLocation;
import me.opl.apps.modelcreator2.model.Rotation;
import me.opl.apps.modelcreator2.model.Texture;
import me.opl.apps.modelcreator2.tool.ToolManager;
import me.opl.apps.modelcreator2.viewport.RenderManager;
import me.opl.apps.modelcreator2.viewport.ViewportContextInitializer;

// TODO: replace FloatUtil.isEqual with something that actually uses the epsilon
// TODO: display matrix previews (1.13 apparently differs from the previous versions)
// TODO: vertex snapping
// TODO: look into styling (web L&F looks neat: https://github.com/mgarin/weblaf/tree/styling)
// TODO: add obj loading support (jogamp utils can read .objs)
// TODO: switch to jogl helpers? (GLBuffers.bytesPerPixel for type byte sizes, Matrix4, Quaternion)
public class ModelCreator implements EventListener {
	public static final String VERSION = "1.0.0-SNAPSHOT";

	private static ModelCreator instance;

	private ImporterManager importerManager;
	private ExporterManager exporterManager;

	/**
	 * The RenderManager instance for rendering model viewports.
	 */
	private RenderManager renderManager = new RenderManager(new ViewportContextInitializer());
	private Map<BaseModel, ToolManager> toolManagers = new WeakHashMap<>();

	private ContextMenuManager windowMenuManager = ContextMenuManager.createDefaultWindowMenuManager(this);

	private List<ModelWindow> windows = new ArrayList<>();

	private List<BaseModel> models = new ArrayList<>();
	private List<BlockState> blockStates = new ArrayList<>();

	private EventDispatcher globalEventDispatcher = new EventDispatcher(null);
	private WeakHashMap<BaseModel, EventDispatcher> eventDispatchers = new WeakHashMap<>();

	public ModelCreator() {
		getGlobalEventDispatcher().registerListeners(this);

		renderManager.getPlaybackState().getEventDispatcher().setParentDipatcher(globalEventDispatcher);

		importerManager = new ImporterManager(this);
		exporterManager = new ExporterManager(this);

		init();
	}

	public static void main(String[] args) throws Exception {
		instance = ModelCreatorFactory.createModelCreator();

		// XXX: test model
		// cube.json
		BlockModel cubeModel = new BlockModel(instance, null, "cube");
		cubeModel.setHasElements(true);
		/*for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) {
			CuboidElement element = new CuboidElement(new Position(x, y, z), new Position(x + 1, y + 1, z + 1));

			for (Face f : Face.values()) ((Cuboid) element.getFragments()[0]).getFaceData(f).setTexture(new Texture(null, f.name().toLowerCase()));

			cubeModel.addElement(element);
		}*/
		/*CuboidElement element = new CuboidElement(cubeModel);
		element.setRotation(new Rotation().setd(22.5f, 90, 0));
		element.setCorners(new Position(0, 2, 3), new Position(15, 8, 12));
		for (Face f : Face.values()) {
			((Cuboid) element.getFragments()[0]).getFaceData(f).setTexture(new Texture(null, f.name().toLowerCase()));
		}
		cubeModel.addElement(element);*/

		CuboidElement element = new CuboidElement(cubeModel);
		element.setCorners(new Position(0, 0, 0), new Position(5, 5, 5));
		element.setRotation(new Rotation().setd(0, 45, 0));
		for (Face f : Face.values()) {
			((Cuboid) element.getFragments()[0]).getFaceData(f).setTexture(new Texture(null, f.name().toLowerCase()));
		}
		cubeModel.addElement(element);
		element = new CuboidElement(cubeModel);
		element.setCorners(new Position(10, 10, 7), new Position(15, 15, 12));
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

		ResourceLocation resourceLocation = new ResourceLocation("minecraft:block/comparator_on");
		// TODO: do this load call elsewhere
		instance.getRenderManager().getResourceManager().loadTexture(resourceLocation);
		Texture tex1 = new Texture("all", resourceLocation);
		model.addTexture(tex1);
		/*Texture tex2 = new Texture("west", new ResourceLocation("minecraft:blocks/missingtex"));
		model.addTexture(tex2);*/

		instance.addModel(model);

		/*BaseModel hammerModel = new MinecraftBlockModelImporter().open(instance, new ResourceLocation("blocks/hammer"), new FileInputStream(new File("C:\\Users\\opl\\Desktop\\mymodels\\reinhardt\\hammer.json")))[0];
		instance.addModel(hammerModel);
		instance.getRenderManager().getResourceManager().loadTexture(new ResourceLocation("Downloads/hammer"));*/
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
	 * Returns tool managers for every model.
	 *
	 * @return All tool managers
	 */
	public ToolManager[] getToolManagers() {
		return toolManagers.values().toArray(new ToolManager[toolManagers.size()]);
	}

	/**
	 * Returns a tool manager for the provided model.
	 *
	 * @param model Model with elements
	 * @return The {@link ToolManager} instance for the given model
	 * @throws NullPointerException if the passed model is {@code null}
	 * @throws IllegalArgumentException if the model doesn't have elements
	 */
	public ToolManager getToolManager(BaseModel model) {
		if (model == null) return null;

		if (!model.hasElements()) throw new IllegalArgumentException("Tried to get a tool manager for a model with no elements");

		ToolManager toolManager = toolManagers.get(model);

		if (toolManager == null) {
			toolManager = new ToolManager(model);
			toolManagers.put(model, toolManager);
		}

		return toolManager;
	}

	public EventDispatcher getGlobalEventDispatcher() {
		return globalEventDispatcher;
	}

	public EventDispatcher getEventDispatcher(BaseModel model) {
		EventDispatcher eventDispatcher = eventDispatchers.get(model);

		if (eventDispatcher == null) {
			eventDispatcher = new EventDispatcher(globalEventDispatcher);
			eventDispatchers.put(model, eventDispatcher);
		}

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

		getGlobalEventDispatcher().fire(new ModelOpenedEvent(model));
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

		getGlobalEventDispatcher().fire(new BlockStateOpenedEvent(blockState));
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

	public ContextMenuManager getWindowMenuManager() {
		return windowMenuManager;
	}

	@EventHandler
	public void onWindowClosed(WindowClosedEvent event) {
		windows.remove(event.getModelWindow());

		if (windows.size() == 0) System.exit(0);
	}

	public ModelWindow[] getWindows() {
		return windows.toArray(new ModelWindow[windows.size()]);
	}

	public ModelWindow createWindow() {
		ModelWindow win = new ModelWindow(this);

		windows.add(win);

		return win;
	}

	public ImporterManager getImporterManager() {
		return importerManager;
	}

	public ExporterManager getExporterManager() {
		return exporterManager;
	}
}
