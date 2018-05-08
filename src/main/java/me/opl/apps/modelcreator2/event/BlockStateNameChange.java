package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BlockState;

public class BlockStateNameChange extends Event implements EventCancellable {
	private BlockState blockState;
	private String newName;

	private boolean cancelled = false;

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

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
}
