package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.BlockState;

public class BlockStateOpenedEvent extends Event {
	private BlockState blockState;

	public BlockStateOpenedEvent(BlockState blockState) {
		this.blockState = blockState;
	}

	public BlockState getBlockState() {
		return blockState;
	}
}
