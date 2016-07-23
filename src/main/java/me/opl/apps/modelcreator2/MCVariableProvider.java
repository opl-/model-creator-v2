package me.opl.apps.modelcreator2;

import me.opl.libs.tablib.BasicVariableProvider;

public class MCVariableProvider extends BasicVariableProvider {
	public static final String KEY_MODEL_WINDOW = "mcvp.MODEL_WINDOW";

	public MCVariableProvider(ModelWindow window) {
		setObject(KEY_MODEL_WINDOW, window);
	}

	public ModelWindow getModelWindow() {
		return (ModelWindow) getObject(KEY_MODEL_WINDOW);
	}
}
