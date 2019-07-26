package me.opl.apps.modelcreator2.history;

/**
 * Represents an action performed by a user and provides a way to undo and redo
 * that action.
 * <p>
 * The behavior of the classes implementing this interface when the
 * {@link #undo()} or {@link #redo()} method is called must folow these
 * requirements:
 * <ul>
 * <li>The state of the application is assumed to be at the same point it was
 * right after or before the action originally took place.
 * </li>
 * <li>The behavior must not change based on any state changes that might have
 * happened after the action originally happened and the method was invoked such
 * as application settings being changed.
 * </li>
 * <li>The behavior must not change if the method is called multiple times.
 * </li>
 * </ul>
 */
public interface HistoryAction {
	/**
	 * Called to perform the undoing of the action this entry represents.
	 *
	 * @see HistoryAction
	 */
	public void undo();

	/**
	 * Called to perform the redoing of the action this entry represents.
	 *
	 * @see HistoryAction
	 */
	public void redo();
}
