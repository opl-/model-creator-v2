package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.ModelCreator;

public class BlockModel extends BaseModel {
	private boolean ambientOcclusion = true;

	public BlockModel(ModelCreator modelCreator, BaseModel parent, String name) {
		super(modelCreator, parent, name);
	}

	/**
	 * @param ambientOcclusion New ambient occlusion value
	 */
	public void setAmbientOcclusion(boolean ambientOcclusion) {
		this.ambientOcclusion = ambientOcclusion;
	}

	/**
	 * @return Current ambient occlusion value
	 */
	public boolean getAmbientOcclusion() {
		return ambientOcclusion;
	}
}
