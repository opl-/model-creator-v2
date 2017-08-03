package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BlockState;

public class BlockStateNameChange extends EventCancellable {
	private BlockState blockState;
	private String newName;

	public BlockStateNameChange(BlockState blockState, String newName) {
		this.blockState = blockState;
		this.newName = newName;
	}

	public String getOldName() {
		return blockState.getName();
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
}
