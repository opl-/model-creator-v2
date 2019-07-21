package me.opl.apps.modelcreator2.event;

import me.opl.apps.modelcreator2.model.PlaybackState;

public class PlaybackStateChangeEvent extends Event implements EventCancellable {
	private boolean cancelled = false;

	private ChangeType changeType;
	private int oldFrame;
	private int newFrame;
	private float oldSpeed;
	private float newSpeed;

	public PlaybackStateChangeEvent(PlaybackState playbackState, ChangeType changeType, int oldFrame, int newFrame, float oldSpeed, float newSpeed) {
		this.oldFrame = oldFrame;
		this.newFrame = newFrame;
		this.newSpeed = newSpeed;
		this.oldSpeed = oldSpeed;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public int getOldFrame() {
		return oldFrame;
	}

	public int getNewFrame() {
		return newFrame;
	}

	public float getOldSpeed() {
		return oldSpeed;
	}

	public float getNewSpeed() {
		return newSpeed;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	public static enum ChangeType {
		FRAME,
		SPEED,
		PLAYBACK_STATE;
	}
}
