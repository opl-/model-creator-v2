package me.opl.apps.modelcreator2.event.blockstate;

import me.opl.apps.modelcreator2.event.EventCancellable;
import me.opl.apps.modelcreator2.model.BlockState;

public class BlockStateNameChangingEvent extends BlockStateEvent implements EventCancellable {
	private String oldName;
	private String newName;

	private boolean cancelled = false;

	public BlockStateNameChangingEvent(BlockState blockState, String newName) {
		super(blockState);
		this.oldName = blockState.getName();
		this.newName = newName;
	}

	public String getOldName() {
		return oldName;
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
