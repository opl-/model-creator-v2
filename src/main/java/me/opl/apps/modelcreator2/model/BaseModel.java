package me.opl.apps.modelcreator2.model;

import java.util.ArrayList;

public class BaseModel {
	protected BaseModel parentModel;

	private ArrayList<Element> elements;
	private ArrayList<Texture> textures;

	protected BaseModel(BaseModel parentModel) {
		this.parentModel = parentModel;

		elements = new ArrayList<Element>();
	}

	/**
	 * Get parent model.
	 *
	 * @return Parent model if it exists, null otherwise 
	 */
	public BaseModel getParent() {
		return parentModel;
	}

	/**
	 * Get element at given index.
	 *
	 * @param index Index of the desired element
	 * @throws ArrayIndexOutOfBoundsException If the index is bigger than the
	 * amount of elements in this model
	 * @return Element at given index
	 */
	public Element getElementAtIndex(int index) throws ArrayIndexOutOfBoundsException {
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
	public Element getModelElementAtIndex(int index) throws ArrayIndexOutOfBoundsException {
		return elements.get(index);
	}

	/**
	 * Get elements in this model or one of the parent elements if this model
	 * doesn't have any elements.
	 *
	 * @return Array of elements in this or one of the parent models
	 */
	public Element[] getElements() {
		if (elements == null) {
			if (getParent() == null) return null;
			else return getParent().getElements(); 
		}

		return getModelElements();
	}

	/**
	 * Get elements in this model.
	 *
	 * @return Array of elements in this model
	 */
	public Element[] getModelElements() {
		Element[] elementsArray = new Element[elements.size()];
		elements.toArray(elementsArray);
		return elementsArray;
	}

	/**
	 * Add an element to this model.
	 *
	 * @param element Element to add
	 */
	public void addElement(Element element) {
		elements.add(element);
	}

	/**
	 * Get the number of elements in this model.
	 *
	 * @return Number of elements in this model
	 */
	public int getElementCount() {
		return elements.size();
	}

	/**
	 * Get texture by name.
	 *
	 * @param textureName The name of the desired texture.
	 * @return Texture with the given name
	 */
	public Texture getModelTextureByName(String textureName) {
		for (Texture t : textures) if (t.getName().equals(textureName)) return t;

		return null;
	}

	/**
	 * Get textures in this model.
	 *
	 * @return Array of textures in this model
	 */
	public Texture[] getTextures() {
		Texture[] texturesArray = new Texture[textures.size()];
		textures.toArray(texturesArray);
		return texturesArray;
	}

	/**
	 * Get number of textures in this and the parent model.
	 *
	 * @return Number of textures in this and the parent model
	 */
	public int getTextureCount() {
		return textures.size() + parentModel.getTextureCount();
	}

	/**
	 * Get number of textures in this model.
	 *
	 * @return Number of textures in this model
	 */
	public int getModelTextureCount() {
		return textures.size();
	}
}
