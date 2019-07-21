package me.opl.apps.modelcreator2.history;

// TODO: closing models (or even removing them) will keep references to them in history entries preventing the models from being GCed. fix this, somehow

public class HistoryManager {
	/**
	 * Stores performed actions. The length of this array is always 1 bigger
	 * than the max length of this history manager to allow for an empty entry
	 * indicating that we are at the beginning of history.
	 */
	private HistoryAction[] history;
	/** Points at last performed action. */
	private int cursor = 0;
	/** Points at last action in history or the beginning entry if history is empty. */
	private int maxIndex = 0;
	/** Points at the current empty beginning entry. */
	private int minIndex = 0;

	/**
	 * Initializes a new instance with a history length of 250 entries.
	 */
	public HistoryManager() {
		this(250);
	}

	public HistoryManager(int maxLength) {
		if (maxLength < 2) throw new IllegalArgumentException("maxLength must be at least 2");

		history = new HistoryAction[maxLength + 1];
	}

	/**
	 * Returns the maximum length of this history manager.
	 *
	 * @return Length of this history
	 */
	public int getMaximumLength() {
		return history.length;
	}

	/**
	 * Returns the amount of history entries currently in history.
	 *
	 * @return Current length of the history
	 */
	public int getLength() {
		return (maxIndex + (maxIndex < minIndex ? history.length : 0)) - minIndex;
	}

	/**
	 * Shrinks or expands the history size while preserving all of its entries.
	 *
	 * @param maxLength New maximum history length
	 */
	public void setMaximumLength(int maxLength) {
		// Add one for the entry indicating beginning of history
		maxLength++;

		if (history.length == maxLength) return;

		HistoryAction[] newHistory = new HistoryAction[maxLength];

		int oldLength = getLength();

		for (int i = 0; i < oldLength; i++) {
			newHistory[i] = history[(cursor + i) % history.length];
		}

		history = newHistory;
		cursor = oldLength;
		minIndex = 0;
		maxIndex = oldLength;
	}

	/**
	 * Undoes the last action in the queue and moves the cursor or does nothing.
	 *
	 * @return {@code false} if there is no action to undo, {@code true}
	 * otherwise
	 */
	public boolean undo() {
		if (cursor == minIndex || history[cursor] == null) return false;

		history[cursor].undo();

		cursor--;
		if (cursor < 0) cursor = history.length - 1;

		return true;
	}

	/**
	 * Does the next action in the queue and moves the cursor or does nothing.
	 *
	 * @return {@code false} if there is no action to redo, {@code true}
	 * otherwise
	 */
	public boolean redo() {
		if (cursor == maxIndex) return false;

		cursor++;
		if (cursor >= history.length) cursor = 0;

		history[cursor].redo();

		return true;
	}

	/**
	 * Inserts an action at the current cursor position and cuts the history to
	 * end on that action.
	 *
	 * @param entry Entry to insert
	 * @throws NullPointerException if the entry is {@code null}
	 */
	public void addHistoryEntry(HistoryAction entry) {
		if (entry == null) throw new NullPointerException("Tried to insert null history entry");

		int oldCursor = cursor;

		cursor++;
		if (cursor >= history.length) cursor = 0;

		// Dereference actions we can no longer redo
		if (oldCursor != maxIndex) {
			int i = cursor;

			while (i != maxIndex) {
				i = (i + 1) % history.length;
				history[i] = null;
			}
		}

		history[cursor] = entry;

		maxIndex = cursor;

		if (minIndex == maxIndex) {
			minIndex = (maxIndex + 1) % history.length;
			// Dereference the new beginning entry
			history[minIndex] = null;
		}
	}

	/**
	 * Clears the history by dereferencing all entries and resetting the
	 * cursors.
	 */
	public void clearHistory() {
		for (int i = 0; i < history.length; i++) history[i] = null;
		cursor = 0;
		minIndex = 0;
		maxIndex = 0;
	}
}
