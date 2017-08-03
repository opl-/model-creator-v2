package me.opl.apps.modelcreator2.model;

import java.util.ArrayList;
import java.util.List;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.event.ModelNameChange;

public class BaseModel {
	private ModelCreator modelCreator;
	protected BaseModel parentModel;

	private String name;

	private ArrayList<Element> elements;
	private ArrayList<Texture> textures = new ArrayList<>();

	private List<FaceData> selectedFaces = new ArrayList<>();
	private List<Element> selectedElements = new ArrayList<>();

	protected BaseModel(ModelCreator modelCreator, BaseModel parentModel, String name) {
		this.modelCreator = modelCreator;
		this.parentModel = parentModel;
		this.name = name;
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
	 * Returns either the model which holds the elements. The returned value can
	 * be this model, one of the parent models or {@code null}.
	 *
	 * This method loops over all parents to check if they have elements.
	 * Because of this, the returned value should be cached if it needs to be
	 * called many times in a single method.
	 *
	 * @return Model instance which holds the elements or {@code null} if none
	 * exist
	 */
	public BaseModel getModelWithElements() {
		BaseModel model = this;
		while (model != null && !model.hasElements()) model = model.getParent();
		return model;
	}

	/**
	 * Set a new name for this model. Fires {@link ModelNameChange} event.
	 *
	 * @param newName New name for this model
	 */
	public void setName(String newName) {
		if (name == null) throw new IllegalArgumentException("name cannot be null");

		ModelNameChange event = new ModelNameChange(this, newName);

		modelCreator.getEventDispatcher().fire(event);

		if (event.isCancelled()) return;

		this.name = event.getNewName();
	}

	/**
	 * Get the name of this model.
	 *
	 * @return The name of this model
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return {@code true} if this model has its own elements, {@code false}
	 * otherwise
	 */
	public boolean hasElements() {
		return elements != null;
	}

	public void setHasElements(boolean hasElements) {
		 // TODO: handle removing elements from selection, etc
		elements = hasElements ? new ArrayList<Element>() : null;
	}

	/**
	 * Returns elements in this model or if this model doesn't have elements, an
	 * array of elements of the first parent that does. The returned array is a
	 * copy of the element list.
	 *
	 * @return Array of elements in this model or first of its parents that does
	 */
	public Element[] getElements() {
		return getModelElements() == null ? (getParent() == null ? null : getParent().getElements()) : getModelElements();
	}

	/**
	 * Get elements in this model or {@code null} if this model has no elements.
	 *
	 * @return Array of elements in this model or {@code null} if this model
	 * has no elements of its own
	 */
	public Element[] getModelElements() {
		if (elements == null) return null;

		Element[] elementsArray = new Element[elements.size()];
		elements.toArray(elementsArray);
		return elementsArray;
	}

	/**
	 * Add an element to this model.
	 *
	 * @param element Element to add
	 * @return {@code true} if the element wasn't already a part of the model
	 * and was added, {@code false} otherwise
	 * @throws IllegalStateException if this model has no elements
	 * @throws IllegalArgumentException if the element is {@code null}
	 */
	public boolean addElement(Element element) {
		// TODO: add a method to add elements to a class
		if (elements == null) throw new IllegalStateException("Tried to add element to model with no elements");
		if (element == null) throw new IllegalArgumentException("Element is null");
		if (elements.contains(element)) return false;

		return elements.add(element);
	}

	/**
	 * Get the number of elements in this model or -1 if this model has no
	 * elements.
	 *
	 * @return Number of elements in this model or -1
	 */
	public int getElementCount() {
		return elements == null ? -1 : elements.size();
	}

	/**
	 * Get texture by name in this or parent models.
	 *
	 * @param textureName The name of the desired texture
	 * @return Texture with the given name
	 */
	public Texture getTextureByName(String textureName) {
		Texture texture = getModelTextureByName(textureName);

		if (texture == null && getParent() != null) texture = getParent().getTextureByName(textureName);

		return texture;
	}

	/**
	 * Get texture by name in this model.
	 *
	 * @param textureName The name of the desired texture
	 * @return Texture with the given name
	 */
	public Texture getModelTextureByName(String textureName) {
		for (Texture t : textures) if (t.getName().equals(textureName)) return t;

		return null;
	}

	/**
	 * Get a Texture with resource location from any Texture passed. 
	 *
	 * @param texture Any Texture object or {@code null}
	 * @return Texture with resource location or {@code null} if not found or passed
	 * texture is {@code null}
	 */
	public Texture resolveTexture(Texture texture) {
		if (texture == null) return null;

		if (!texture.isReference()) return texture;

		return resolveTexture(getTextureByName(texture.getTextureReference()));
	}

	/**
	 * Get textures in this model. The returned array is a copy of the texture
	 * list.
	 *
	 * @return Copy of the list of textures in this model
	 */
	public Texture[] getModelTextures() {
		Texture[] texturesArray = new Texture[textures.size()];
		textures.toArray(texturesArray);
		return texturesArray;
	}

	/**
	 * @param texture {@link Texture} instance to add to this model's texture
	 * list
	 * @return {@code true} if the texture doesn't already exist and was added,
	 * {@code false} otherwise
	 * @throws IllegalArgumentException if {@code texture} is null or doesn't
	 * have a name
	 */
	public boolean addTexture(Texture texture) {
		if (texture == null) throw new IllegalArgumentException("Texture is null");
		if (texture.getName() == null) throw new IllegalArgumentException("Texture has no name");
		if (textures.contains(texture)) return false;

		return textures.add(texture);
	}

	/**
	 * @param texture {@link Texture} instance to remove from this model's
	 * texture list
	 * @return {@code true} if the texture existed and was removed,
	 * {@code false} otherwise
	 */
	public boolean removeTexture(Texture texture) {
		return textures.remove(texture);
	}

	/**
	 * Add the face to the selected faces.
	 *
	 * @param face Face to select
	 * @return {@code true} if selection changed, {@code false} otherwise
	 * @throws IllegalStateException if this model has no elements of its own
	 * @throws IllegalArgumentException if the face is null
	 */
	public boolean selectFace(FaceData face) {
		// TODO: check if the face belongs to this model?
		if (elements == null) throw new IllegalStateException("Tried to select a face in a model with no elements");
		if (face == null) throw new IllegalArgumentException("Face is null");
		if (selectedFaces.contains(face)) return false;

		if (!selectedElements.contains(face.getFragment().getElement())) {
			selectedElements.add(face.getFragment().getElement());
			face.getFragment().triggerUpdate();
		}

		return selectedFaces.add(face);
	}

	/**
	 * Adds all faces to the selected faces list.
	 *
	 * @return Array of faces that were selected
	 */
	public FaceData[] selectAllFaces() {
		ArrayList<FaceData> newlySelectedFaces = new ArrayList<>();

		for (Element e : elements) {
			for (FaceData f : e.getFaces()) if (selectedFaces.contains(f)) {
				newlySelectedFaces.add(f);
				selectedFaces.add(f);
				f.getFragment().triggerUpdate();
			}

			if (!selectedElements.contains(e)) selectedElements.add(e);
		}

		FaceData[] newlySelectedFacesArray = new FaceData[newlySelectedFaces.size()];
		newlySelectedFaces.toArray(newlySelectedFacesArray);
		return newlySelectedFacesArray;
	}

	/**
	 * Removes a face from the selected faces list.
	 *
	 * @param face Face to deselect
	 * @return {@code true} if this face was selected and was deselected,
	 * {@code false} otherwise
	 */
	public boolean deselectFace(FaceData face) {
		// XXX: alternative - get faces of this element, check if theyre selected. might be faster
		Element element = face.getFragment().getElement();

		boolean foundElement = false;
		for (FaceData f : selectedFaces) if (f != face && f.getFragment().getElement() == element) {
			foundElement = true;
			break;
		}

		if (!foundElement) selectedElements.remove(element);

		boolean removed = selectedFaces.remove(face);

		if (removed) face.getFragment().triggerUpdate();

		return removed;
	}

	/**
	 * Removes all faces from the selected faces list.
	 *
	 * @return Array of faces that were deselected
	 */
	public FaceData[] deselectAllFaces() {
		FaceData[] faces = getSelectedFaces();

		for (FaceData f : faces) f.getFragment().triggerUpdate();

		selectedFaces.clear();
		selectedElements.clear();

		return faces;
	}

	/**
	 * Toggle face selection. Calls {@link BaseModel#selectFace(FaceData)} or
	 * {@link BaseModel#deselectFace(FaceData)}.
	 *
	 * @return {@code true} if the face is now selected, {@code false} otherwise
	 */
	public boolean toggleFaceSelection(FaceData face) {
		if (elements == null) throw new IllegalStateException("Tried to select a face in a model with no elements");
		if (face == null) throw new IllegalArgumentException("Face is null");

		return selectedFaces.contains(face) ? !deselectFace(face) : selectFace(face);
	}

	/**
	 * Returns an array containing all selected faces in this model. The array
	 * is a copy of the selected faces list.
	 *
	 * @return A copy of the selected faces list
	 */
	public FaceData[] getSelectedFaces() {
		FaceData[] faces = new FaceData[selectedFaces.size()];
		selectedFaces.toArray(faces);
		return faces;
	}

	/**
	 * Returns an array containing all selected elements in this model. The
	 * array is a copy of the selected elements list.
	 *
	 * @return A copy of the selected elements list
	 */
	public Element[] getSelectedElements() {
		Element[] elements = new Element[selectedElements.size()];
		selectedElements.toArray(elements);
		return elements;
	}

	/**
	 * @param face Face to test
	 * @return {@code true} if the face is currently selected, {@code false}
	 * otherwise
	 * @throws IllegalStateException if the model has no elements
	 */
	public boolean isFaceSelected(FaceData face) {
		if (elements == null) throw new IllegalStateException("Tried to check if face is selected in a model with no elements");

		return selectedFaces.contains(face);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[name=" + name + "]";
	}
}
