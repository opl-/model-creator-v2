package me.opl.apps.modelcreator2.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EventDispatcher {
	private Map<Class<? extends Event>, List<RegisteredListener>> eventListeners;

	public EventDispatcher() {
		eventListeners = new HashMap<Class<? extends Event>, List<RegisteredListener>>();
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

			ArrayList<Integer> toRemove = new ArrayList<Integer>();
			for (int i = 0; i < listeners.size(); i++) if (listeners.get(i).listener.get() == listener || listeners.get(i).listener.get() == null) toRemove.add(i);

			if (listeners.size() - toRemove.size() > 0) for (Integer i : toRemove) listeners.remove(i);
			else eventListeners.remove(e.getKey());
		}
	}

	public void unregisterListener(EventListener listener, Method method) {
		Class<?>[] params = method.getParameterTypes();
		if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) throw new IllegalArgumentException("method doesn't accept one parameter of type Event");
		Class<? extends Event> eventType = params[0].asSubclass(Event.class);

		ArrayList<RegisteredListener> toRemove = new ArrayList<RegisteredListener>();
		List<RegisteredListener> listeners = eventListeners.get(eventType);

		for (RegisteredListener rl : listeners) if (rl.listener.get() == listener && rl.method.equals(method) || rl.listener.get() == null) toRemove.add(rl);

		if (listeners.size() - toRemove.size() > 0) for (RegisteredListener rl : toRemove) listeners.remove(rl);
		else eventListeners.remove(eventType);
	}

	public void fire(Event event) {
		Class<? extends Event> eventType = event.getClass();
		ArrayList<RegisteredListener> toRemove = new ArrayList<RegisteredListener>();

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
