package me.opl.apps.modelcreator2.model;

import me.opl.apps.modelcreator2.event.EventDispatcher;
import me.opl.apps.modelcreator2.event.PlaybackStateChangeEvent;
import me.opl.apps.modelcreator2.event.PlaybackStateChangeEvent.ChangeType;

public class PlaybackState {
	private static float FRAME_DURATION = 1000 / 20;

	private EventDispatcher eventDispatcher = new EventDispatcher(null);

	private float playbackSpeed = 1f;
	private long timeOffset = 0;
	private long startTime = System.currentTimeMillis();

	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public int getCurrentFrame() {
		return (int) ((timeOffset + (System.currentTimeMillis() - startTime) * playbackSpeed) / FRAME_DURATION);
	}

	public void setCurrentFrame(int frame) {
		int oldFrame = getCurrentFrame();
		long oldTimeOffset = timeOffset;

		timeOffset += (frame - getCurrentFrame()) * FRAME_DURATION;

		PlaybackStateChangeEvent event = new PlaybackStateChangeEvent(this, ChangeType.FRAME, oldFrame, getCurrentFrame(), playbackSpeed, playbackSpeed);
		eventDispatcher.fire(event);

		if (event.isCancelled()) {
			timeOffset = oldTimeOffset;
		}
	}

	public boolean isPlaying() {
		return playbackSpeed != 0f;
	}

	public float getSpeed() {
		return playbackSpeed;
	}

	/**
	 * Sets playback speed. If set to {@code 0}, the playback will pause.
	 *
	 * Fires {@link PlaybackStateChangeEvent}
	 *
	 * @param playbackSpeed New playback speed
	 */
	public void setSpeed(float playbackSpeed) {
		int currentFrame = getCurrentFrame();

		PlaybackStateChangeEvent event = new PlaybackStateChangeEvent(this, ChangeType.FRAME, currentFrame, currentFrame, this.playbackSpeed, playbackSpeed);
		eventDispatcher.fire(event);

		if (event.isCancelled()) return;

		long currentTime = System.currentTimeMillis();

		if (this.playbackSpeed != 0) {
			timeOffset += (currentTime - startTime) / this.playbackSpeed;
		}

		startTime = currentTime;

		this.playbackSpeed = playbackSpeed;
	}
}
