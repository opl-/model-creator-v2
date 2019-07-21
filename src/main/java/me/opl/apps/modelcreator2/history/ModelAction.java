package me.opl.apps.modelcreator2.history;

import me.opl.apps.modelcreator2.model.BaseModel;

/**
 * Represents a history entry that in any way modifies a model.
 */
public interface ModelAction extends HistoryAction {
	/**
	 * @return The model this history entry is for.
	 */
	public BaseModel getModel();
}
