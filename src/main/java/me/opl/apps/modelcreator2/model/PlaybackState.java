package me.opl.apps.modelcreator2.model;

public class PlaybackState {
	private static int FRAME_DURATION = 1000 / 20;

	private float playbackSpeed = 1f;
	private long timeOffset = 0;
	private long startTime = System.currentTimeMillis();

	public int getCurrentFrame() {
		return (int) (timeOffset + (System.currentTimeMillis() - startTime) * playbackSpeed) / FRAME_DURATION;
	}

	public void setCurrentFrame(int frame) {
		timeOffset += (frame - getCurrentFrame()) * FRAME_DURATION;
	}

	public boolean isPlaying() {
		return playbackSpeed != 0f;
	}

	public float getSpeed() {
		return playbackSpeed;
	}

	public void setSpeed(float playbackSpeed) {
		long currentTime = System.currentTimeMillis();

		timeOffset += (currentTime - startTime) / this.playbackSpeed;
		startTime = currentTime;

		this.playbackSpeed = playbackSpeed;
	}
}
