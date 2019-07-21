package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.event.BlockStateNameChange;

// TODO
public class BlockState {
	private ModelCreator modelCreator;

	private String name;

	public BlockState(ModelCreator modelCreator, String name) {
		this.modelCreator = modelCreator;
		this.name = name;
	}

	/**
	 * Returns the name of this block state.
	 *
	 * @return Name of this block state
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set a new name for this block state. Fires {@link BlockStateNameChange} event.
	 *
	 * @param newName New name for this block state
	 */
	public void setName(String newName) {
		if (name == null) throw new IllegalArgumentException("name cannot be null");

		BlockStateNameChange event = new BlockStateNameChange(this, newName);

		modelCreator.getGlobalEventDispatcher().fire(event);

		if (event.isCancelled()) return;

		this.name = event.getNewName();
	}
}
