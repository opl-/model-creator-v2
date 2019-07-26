package me.opl.apps.modelcreator2.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import me.opl.apps.modelcreator2.model.Element;
import me.opl.apps.modelcreator2.model.MinecraftModel;
import me.opl.apps.modelcreator2.model.Ray;
import me.opl.apps.modelcreator2.model.RayFaceIntersection;
import me.opl.apps.modelcreator2.util.RayHelper;
import me.opl.apps.modelcreator2.viewport.ViewportFramebufferRenderer;

public class PointerToolEvent extends ToolEvent {
	public static final int BUTTON_LEFT = 1;
	public static final int BUTTON_MIDDLE = 2;
	public static final int BUTTON_RIGHT = 3;

	public static final int MODIFIER_CONTROL = 0x1;
	public static final int MODIFIER_SHIFT = 0x2;
	public static final int MODIFIER_ALT = 0x4;

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
	private boolean altDown;
	private Ray ray;
	private Ray lastRay;
	private RayFaceIntersection[] rayIntersections;

	public PointerToolEvent(ViewportFramebufferRenderer framebufferRenderer, PointerEventType type, int button, int buttonStates, int pressCount, float x, float y, float lastX, float lastY, boolean controlDown, boolean shiftDown, boolean altDown) {
		this(framebufferRenderer, type, button, buttonStates, pressCount, x, y, lastX, lastY, 0, controlDown, shiftDown, altDown);
	}

	public PointerToolEvent(ViewportFramebufferRenderer framebufferRenderer, PointerEventType type, int button, int buttonStates, int pressCount, float x, float y, float lastX, float lastY, float scroll, boolean controlDown, boolean shiftDown, boolean altDown) {
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
		this.altDown = altDown;

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
	public MinecraftModel getDisplayedModel() {
		return framebufferRenderer.getDisplayedModel();
	}

	/**
	 * @return The current edited model of the
	 * {@link ViewportFramebufferRenderer} this event was triggered by
	 */
	public MinecraftModel getEditedModel() {
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

	// TODO: change name, docs
	public boolean testDown(int button, int modifiers) {
		return testDown(button, modifiers, 0);
	}

	// TODO: change name, docs
	public boolean testDown(int button, int modifiers, int optionalModifiers) {
		if ((modifiers & optionalModifiers) != 0) throw new IllegalArgumentException("One of required modifiers is on the optional modifiers list");
		if (this.button != button) return false;
		if (modifiers != (((controlDown ? MODIFIER_CONTROL : 0) | (shiftDown ? MODIFIER_SHIFT : 0) | (altDown ? MODIFIER_ALT : 0)) & ~optionalModifiers)) return false;

		return true;
	}

	// TODO: change name, docs
	public boolean testHeld(int button, int modifiers) {
		return testHeld(button, modifiers, 0);
	}

	// TODO: change name, docs
	public boolean testHeld(int button, int modifiers, int optionalModifiers) {
		if ((modifiers & optionalModifiers) != 0) throw new IllegalArgumentException("One of required modifiers is on the optional modifiers list");
		if (button > 0 && (getButtonMask(button) & buttonStates) == 0) return false;
		if (modifiers != (((controlDown ? MODIFIER_CONTROL : 0) | (shiftDown ? MODIFIER_SHIFT : 0) | (altDown ? MODIFIER_ALT : 0)) & ~optionalModifiers)) return false;

		return true;
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
	 * Intersects this event's ray intersections with faces in the model
	 * returned by {@link PointerToolEvent#getEditedModel()} and caches the
	 * result.
	 *
	 * @return Array of ray intersections sorted from closest to farthest
	 */
	public RayFaceIntersection[] getRayIntersections() {
		if (rayIntersections == null) rayIntersections = intersectRay(ray);

		RayFaceIntersection[] intersectionsCopy = new RayFaceIntersection[rayIntersections.length];
		System.arraycopy(rayIntersections, 0, intersectionsCopy, 0, intersectionsCopy.length);
		return intersectionsCopy;
	}

	private RayFaceIntersection[] intersectRay(Ray ray) {
		MinecraftModel editedModel = getEditedModel();

		if (editedModel == null) return new RayFaceIntersection[0];

		ArrayList<RayFaceIntersection> intersections = new ArrayList<>();

		for (Element e : editedModel.getElements()) {
			RayFaceIntersection[] fragmentIntersections = e.intersect(ray);

			for (RayFaceIntersection i : fragmentIntersections) intersections.add(i);
		}

		RayFaceIntersection[] rayIntersections = intersections.toArray(new RayFaceIntersection[intersections.size()]);

		Arrays.sort(rayIntersections, new Comparator<RayFaceIntersection>() {
			@Override
			public int compare(RayFaceIntersection a, RayFaceIntersection b) {
				float ad = a.distance();
				float bd = b.distance();
				return ad == bd ? 0 : ad > bd ? 1 : -1;
			}
		});

		return rayIntersections;
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
