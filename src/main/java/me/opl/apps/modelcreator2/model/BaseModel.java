package me.opl.apps.modelcreator2.model;

import java.util.ArrayList;

public class BaseModel {
	protected BaseModel parentModel;

	protected ArrayList<Voxel> elements;

	protected ArrayList<Selectable> selectable;

	protected BaseModel(BaseModel parentModel) {
		this.parentModel = parentModel;
	}

	/**
	 * Get element at given index. If the index is bigger than the amount of
	 * elements in this model and the parent model exists, then the amount of
	 * elements in this model is subtracted from index and passed to the parent
	 * model.
	 *
	 * @param index Index of the desired element
	 * @throws ArrayIndexOutOfBoundsException If the index is bigger than the
	 * amount of elements in this model and it's parent models
	 * @return Desired element
	 */
	public Voxel getElementAtIndex(int index) throws ArrayIndexOutOfBoundsException {
		if (index >= elements.size()) {
			if (parentModel != null) throw new ArrayIndexOutOfBoundsException(index);
			else return parentModel.getElementAtIndex(index);
		}
		return elements.get(index);
	}

	/**
	 * Get element at given index in this model.
	 *
	 * @param index Index of desired element
	 * @throws ArrayIndexOutOfBoundsException If the index is bigger than the
	 * amount of elements in this model
	 * @return Desired element
	 */
	public Voxel getModelElementAtIndex(int index) throws ArrayIndexOutOfBoundsException {
		return elements.get(index);
	}

	/**
	 * Get elements in this model and all elements in it's parent models.
	 *
	 * @return Array of elements in this model and it's parents
	 */
	public Voxel[] getElements() {
		Voxel[] elementsArray = new Voxel[elements.size()];
		elements.toArray(elementsArray);
		return elementsArray;
	}

	/**
	 * Get elements in this model, without the elements in it's parent models.
	 *
	 * @return Array of elements in this model
	 */
	public Voxel[] getModelElements() {
		Voxel[] elementsArray = new Voxel[elements.size()];
		elements.toArray(elementsArray);
		return elementsArray;
	}

	/**
	 * Get number of elements in this model and all it's parent models.
	 *
	 * @return Number of all elements
	 */
	public int getElementCount() {
		int elementCount = elements.size();
		BaseModel model = parentModel;

		while (model != null) {
			elementCount += model.getModelElementCount();
			model = model.getParent();
		}

		return elementCount;
	}

	/**
	 * Get number of elements in this model, without the parent models.
	 *
	 * @return Number of elements in this model
	 */
	public int getModelElementCount() {
		return elements.size();
	}

	/**
	 * Get parent model.
	 *
	 * @return Parent model if it exists, null otherwise 
	 */
	public BaseModel getParent() {
		return parentModel;
	}
}
