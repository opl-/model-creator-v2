package me.opl.apps.modelcreator2.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.Ray;
import me.opl.apps.modelcreator2.model.RayIntersection;
import me.opl.apps.modelcreator2.util.RayHelper;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer;

// TODO: Add method to get the current state of a button
// TODO: Fire the events
public class PointerToolEvent extends ToolEvent {
	public static final int BUTTON_LEFT = 1;
	public static final int BUTTON_MIDDLE = 2;
	public static final int BUTTON_RIGHT = 3;

	private ViewportFramebufferRenderer framebufferRenderer;
	private PointerEventType type;
	private int button;
	private int buttonStates;
	private int pressCount;
	private float x;
	private float y;
	private float lastX;
	private float lastY;
	private float scroll;
	private boolean controlDown;
	private boolean shiftDown;
	private Ray ray;
	private Ray lastRay;
	private RayIntersection[] rayIntersections;

	public PointerToolEvent(ViewportFramebufferRenderer framebufferRenderer, PointerEventType type, int button, int buttonStates, int pressCount, float x, float y, float lastX, float lastY, boolean controlDown, boolean shiftDown) {
		this(framebufferRenderer, type, button, buttonStates, pressCount, x, y, lastX, lastY, 0, controlDown, shiftDown);
	}

	public PointerToolEvent(ViewportFramebufferRenderer framebufferRenderer, PointerEventType type, int button, int buttonStates, int pressCount, float x, float y, float lastX, float lastY, float scroll, boolean controlDown, boolean shiftDown) {
		this.framebufferRenderer = framebufferRenderer;
		this.type = type;
		this.button = button;
		this.buttonStates = buttonStates;
		this.pressCount = pressCount;
		this.x = x;
		this.y = y;
		this.lastX = lastX;
		this.lastY = lastY;
		this.scroll = scroll;
		this.controlDown = controlDown;
		this.shiftDown = shiftDown;

		ray = RayHelper.rayFromClick(framebufferRenderer, x, y);
		lastRay = RayHelper.rayFromClick(framebufferRenderer, x, y);
	}

	/**
	 * @return {@link ViewportFramebufferRenderer} this event was triggered by
	 */
	public ViewportFramebufferRenderer getFramebufferRenderer() {
		return framebufferRenderer;
	}

	/**
	 * @return The current displayed model of the
	 * {@link ViewportFramebufferRenderer} this event was triggered by
	 */
	public BaseModel getDisplayedModel() {
		return framebufferRenderer.getDisplayedModel();
	}

	/**
	 * @return The current edited model of the
	 * {@link ViewportFramebufferRenderer} this event was triggered by
	 */
	public BaseModel getEditedModel() {
		return framebufferRenderer.getEditedModel();
	}

	/**
	 * @return Type of this event
	 */
	public PointerEventType getType() {
		return type;
	}

	/**
	 * @return Number of the button that caused this even to trigger
	 */
	public int getButton() {
		return button;
	}

	/**
	 * Returns which buttons are held down. Each button's state is represented
	 * by it's corresponding bit. For example, if buttons 1 and 3 are held down,
	 * the returned value will be {@code 0b0101}.
	 *
	 * @return Current state of buttons
	 */
	public int getButtonStates() {
		return buttonStates;
	}

	public boolean isButtonDown(int buttonNumber) {
		return (buttonStates & getButtonMask(buttonNumber)) != 0;
	}

	public boolean isLeftButtonDown() {
		return (buttonStates & 0b1) != 0;
	}

	public boolean isMiddleButtonDown() {
		return (buttonStates & 0b10) != 0;
	}

	public boolean isRightButtonDown() {
		return (buttonStates & 0b100) != 0;
	}

	/**
	 * Returns the amount of times the button this event relates to has been
	 * pressed. Can be used to detect double clicking.
	 *
	 * @return Times the button has been pressed
	 */
	public int getPressCount() {
		return pressCount;
	}

	public float getX() {
		return x;
	}

	public int getXi() {
		return (int) x;
	}

	public float getY() {
		return y;
	}

	public int getYi() {
		return (int) y;
	}

	public float getLastX() {
		return lastX;
	}

	public int getLastXi() {
		return (int) lastX;
	}

	public float getLastY() {
		return lastY;
	}

	public int getLastYi() {
		return (int) lastY;
	}

	public float getChangeX() {
		return x - lastX;
	}

	public int getChangeXi() {
		return (int) (x - lastX);
	}

	public float getChangeY() {
		return y - lastY;
	}

	public int getChangeYi() {
		return (int) (y - lastY);
	}

	/**
	 * @return The scroll amount. Negative if scrolled up or away from user,
	 * positive if scrolled down or towards the user
	 */
	public float getScroll() {
		return scroll;
	}

	public boolean isShiftDown() {
		return shiftDown;
	}

	public boolean isControlDown() {
		return controlDown;
	}

	public Ray getRay() {
		return ray;
	}

	public Ray getLastRay() {
		return lastRay;
	}

	/**
	 * Intersects this event's ray with faces in the model returned by
	 * {@link PointerToolEvent#getEditedModel()} and caches the result.
	 *
	 * @return Array of ray intersections sorted from closest to farthest
	 */
	public RayIntersection[] getRayIntersections() {
		if (rayIntersections == null) {
			BaseModel editedModel = getEditedModel();

			if (editedModel == null) {
				rayIntersections = new RayIntersection[0];
			} else {
				ArrayList<RayIntersection> intersections = new ArrayList<>();

				for (Element e : editedModel.getElements()) {
					RayIntersection[] fragmentIntersections = e.intersect(ray);

					for (RayIntersection i : fragmentIntersections) intersections.add(i);
				}

				rayIntersections = new RayIntersection[intersections.size()];
				intersections.toArray(rayIntersections);

				Arrays.sort(rayIntersections, new Comparator<RayIntersection>() {
					@Override
					public int compare(RayIntersection a, RayIntersection b) {
						return (int) (a.distance() - b.distance());
					}
				});
			}
		}

		RayIntersection[] intersectionsCopy = new RayIntersection[rayIntersections.length];
		System.arraycopy(rayIntersections, 0, intersectionsCopy, 0, intersectionsCopy.length);
		return intersectionsCopy;
	}

	/**
	 * Returns a mask for use with {@code buttonStates}.
	 *
	 * @param buttonNumber Number of the button
	 * @return Button's mask
	 */
	public static int getButtonMask(int buttonNumber) {
		if (buttonNumber < 1 || buttonNumber > 16) throw new IllegalArgumentException("Button number out of range (" + buttonNumber + ")");
		return 1 << (buttonNumber - 1);
	}

	public static enum PointerEventType {
		DOWN,
		UP,
		CLICK,
		DRAG_START,
		DRAG,
		DRAG_END,
		HOVER,
		SCROLL;
	}
}
