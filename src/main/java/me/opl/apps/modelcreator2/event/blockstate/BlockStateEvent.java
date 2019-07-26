package me.opl.apps.modelcreator2.event.blockstate;

import me.opl.apps.modelcreator2.event.Event;
import me.opl.apps.modelcreator2.model.BlockState;

public class BlockStateEvent extends Event {
	private BlockState blockState;

	public BlockStateEvent(BlockState blockState) {
		this.blockState = blockState;
	}

	public BlockState getBlockState() {
		return blockState;
	}
}
