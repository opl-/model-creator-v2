package me.opl.apps.modelcreator2.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EventDispatcher {
	private EventDispatcher parentDispatcher;
	private Map<Class<? extends Event>, List<RegisteredListener>> eventListeners;
	private boolean corked;

	public EventDispatcher(EventDispatcher parentEventDispatcher) {
		parentDispatcher = parentEventDispatcher;
		eventListeners = new HashMap<>();
	}

	public void registerListeners(EventListener listener) {
		for (Method m : listener.getClass().getMethods()) {
			Class<?>[] params = m.getParameterTypes();
			if (m.isAnnotationPresent(EventHandler.class) && params.length == 1 && Event.class.isAssignableFrom(params[0])) registerListener(listener, m);
		}
	}

	public void registerListener(EventListener listener, Method method) {
		if (listener == null) throw new IllegalArgumentException("listener is null");
		if (method == null) throw new IllegalArgumentException("method is null");

		if (!method.isAnnotationPresent(EventHandler.class)) throw new IllegalArgumentException("Method is not an EventHandler.");

		RegisteredListener registeredListener = new RegisteredListener(listener, method);

		List<RegisteredListener> listeners = eventListeners.get(registeredListener.eventType);

		if (listeners == null) {
			listeners = new ArrayList<RegisteredListener>();
			eventListeners.put(registeredListener.eventType, listeners);
		} else {
			for (RegisteredListener rl : listeners) if (rl.listener.get() == listener && rl.method == method) return;
		}

		listeners.add(registeredListener);
	}

	public void unregisterListeners(EventListener listener) {
		for (Entry<Class<? extends Event>, List<RegisteredListener>> e : eventListeners.entrySet()) {
			List<RegisteredListener> listeners = e.getValue();

			ArrayList<RegisteredListener> toRemove = new ArrayList<>();
			for (int i = 0; i < listeners.size(); i++) if (listeners.get(i).listener.get() == listener || listeners.get(i).listener.get() == null) toRemove.add(listeners.get(i));

			if (listeners.size() - toRemove.size() > 0) for (RegisteredListener rl : toRemove) listeners.remove(rl);
			else eventListeners.remove(e.getKey());
		}
	}

	public void unregisterListener(EventListener listener, Method method) {
		Class<?>[] params = method.getParameterTypes();
		if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) throw new IllegalArgumentException("method doesn't accept one parameter of type Event");
		Class<? extends Event> eventType = params[0].asSubclass(Event.class);

		ArrayList<RegisteredListener> toRemove = new ArrayList<>();
		List<RegisteredListener> listeners = eventListeners.get(eventType);

		for (RegisteredListener rl : listeners) if (rl.listener.get() == listener && rl.method.equals(method) || rl.listener.get() == null) toRemove.add(rl);

		if (listeners.size() - toRemove.size() > 0) for (RegisteredListener rl : toRemove) listeners.remove(rl);
		else eventListeners.remove(eventType);
	}

	public void fire(Event event) {
		if (corked) return;

		Class<? extends Event> eventType = event.getClass();
		ArrayList<RegisteredListener> toRemove = new ArrayList<>();

		for (Entry<Class<? extends Event>, List<RegisteredListener>> e : eventListeners.entrySet()) {
			for (RegisteredListener rl : e.getValue()) {
				if (rl.listener.get() == null) {
					toRemove.add(rl);
					continue;
				}

				if (rl.eventType == eventType || (rl.method.getAnnotation(EventHandler.class).deep() && rl.eventType.isAssignableFrom(eventType))) {
					try {
						rl.method.invoke(rl.listener.get(), event);
					} catch (Exception ex) {
						throw new IllegalStateException(ex);
					}
				}
			}

			for (RegisteredListener rl : toRemove) e.getValue().remove(rl);
			toRemove.clear();
		}

		if (parentDispatcher != null) parentDispatcher.fire(event);
	}

	/**
	 * Event dispatchers can be corked to stop them from delivering events to
	 * registered event listeners. This allows reducing load by firing a single
	 * bulk event after an operation modifying many elements instead of firing
	 * them one by one during the operation.
	 *
	 * @return {@code true} if this event dispatcher is corked, {@code false}
	 * otherwise
	 */
	public boolean isCorked() {
		return corked;
	}

	/**
	 * Corks this event dispatcher.
	 *
	 * @see EventDispatcher#isCorked()
	 */
	public void cork() {
		this.corked = true;
	}

	/**
	 * Uncorks this event dispatcher.
	 *
	 * @see EventDispatcher#isCorked()
	 */
	public void uncork() {
		this.corked = false;
	}

	/**
	 * Changes this EventDispatcher's parent dispatcher.
	 *
	 * @param parentDispatcher The new {@link EventDispatcher}
	 * @see EventDispatcher#getParentDispatcher()
	 */
	public void setParentDipatcher(EventDispatcher parentDispatcher) {
		this.parentDispatcher = parentDispatcher;
	}

	/**
	 * Parent dispatcher is an EventDispatcher that all events from this
	 * EventDispatcher are forwarded to after triggering all listeners. If
	 * this dispatcher is corked, the parent dispatcher will not receive the
	 * event.
	 *
	 * @return This EventDispatcher's parent {@link EventDispatcher}
	 */
	public EventDispatcher getParentDispatcher() {
		return parentDispatcher;
	}

	private static class RegisteredListener {
		private WeakReference<EventListener> listener;
		private Method method;
		private Class<? extends Event> eventType;

		public RegisteredListener(EventListener listener, Method method) {
			Class<?>[] params = method.getParameterTypes();
			if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) throw new IllegalArgumentException("method doesn't accept one parameter of type Event");

			eventType = params[0].asSubclass(Event.class);

			this.listener = new WeakReference<EventListener>(listener);
			this.method = method;
		}
	}
}
