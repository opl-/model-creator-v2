package me.opl.apps.modelcreator2;

import me.opl.libs.tablib.BasicVariableProvider;

public class MCVariableProvider extends BasicVariableProvider {
	public static final String KEY_MODEL_CREATOR = "mcvp.MODEL_CREATOR";
	public static final String KEY_MODEL_WINDOW = "mcvp.MODEL_WINDOW";

	public MCVariableProvider(ModelCreator modelCreator, ModelWindow window) {
		setObject(KEY_MODEL_CREATOR, modelCreator);
		setObject(KEY_MODEL_WINDOW, window);
	}

	public ModelCreator getModelCreator() {
		return (ModelCreator) getObject(KEY_MODEL_CREATOR);
	}

	public ModelWindow getModelWindow() {
		return (ModelWindow) getObject(KEY_MODEL_WINDOW);
	}
}
