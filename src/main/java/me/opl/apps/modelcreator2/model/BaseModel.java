package me.opl.apps.modelcreator2.model;

import java.util.ArrayList;
import java.util.List;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.event.ElementsResizedEvent;
import me.opl.apps.modelcreator2.event.ElementsRotatedEvent;
import me.opl.apps.modelcreator2.event.EventDispatcher;
import me.opl.apps.modelcreator2.event.EventHandler;
import me.opl.apps.modelcreator2.event.EventListener;
import me.opl.apps.modelcreator2.event.ModelNameChange;
import me.opl.apps.modelcreator2.event.SelectionChangedEvent;
import me.opl.apps.modelcreator2.event.SelectionChangedEvent.ChangeType;

public class BaseModel implements EventListener {
	private ModelCreator modelCreator;
	private EventDispatcher eventDispatcher;
	protected BaseModel parentModel;

	private String name;

	private ElementGroup rootGroup = new ElementGroup();
	private ArrayList<Element> elements;
	private ArrayList<Texture> textures = new ArrayList<>();

	private List<FaceData> selectedFaces = new ArrayList<>();
	private List<Element> selectedElements = new ArrayList<>();

	private Position selectionAABBFrom = new Position();
	private Position selectionAABBTo = new Position();
	/**
	 * Stores the center of the selection AABB. {@code null} if no elements are
	 * selected.
	 */
	private Position selectionCenter = null;

	protected BaseModel(ModelCreator modelCreator, BaseModel parentModel, String name) {
		this.modelCreator = modelCreator;
		this.parentModel = parentModel;
		this.name = name;

		eventDispatcher = modelCreator.getEventDispatcher(this);
		eventDispatcher.registerListeners(this);
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
	 * @return This model's event dispatcher
	 */
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
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

		modelCreator.getGlobalEventDispatcher().fire(event);

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
		// TODO: add a method to add elements to a class (added later: what?)
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
	 * @return {@code true} if this model has any selected faces, {@code false}
	 * otherwise
	 */
	public boolean hasSelection() {
		return selectedFaces.size() > 0;
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

		Element element = face.getFragment().getElement();

		if (!selectedElements.contains(element)) {
			selectedElements.add(element);
			addToAABB(element);
			eventDispatcher.fire(new SelectionChangedEvent(this, ChangeType.ADDED, new FaceData[] {face}, new Element[] {element}));
		} else {
			eventDispatcher.fire(new SelectionChangedEvent(this, ChangeType.ADDED, new FaceData[] {face}, new Element[] {}));
		}

		face.getFragment().triggerUpdate();

		return selectedFaces.add(face);
	}

	/**
	 * Adds all faces to the selected faces list.
	 *
	 * @return Array of faces that were selected
	 */
	public FaceData[] selectAllFaces() {
		ArrayList<FaceData> newlySelectedFaces = new ArrayList<>();
		ArrayList<Element> newlySelectedElements = new ArrayList<>();

		for (Element e : elements) {
			for (FaceData f : e.getFaces()) if (selectedFaces.contains(f)) {
				newlySelectedFaces.add(f);
				selectedFaces.add(f);
				f.getFragment().triggerUpdate();
			}

			if (!selectedElements.contains(e)) {
				newlySelectedElements.add(e);
				selectedElements.add(e);
				addToAABB(e);
			}
		}

		FaceData[] selectedFaces = newlySelectedFaces.toArray(new FaceData[newlySelectedFaces.size()]);

		eventDispatcher.fire(new SelectionChangedEvent(this, ChangeType.ADDED, selectedFaces, newlySelectedElements.toArray(new Element[newlySelectedElements.size()])));

		return selectedFaces;
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

		boolean removed = selectedFaces.remove(face);

		if (removed) {
			face.getFragment().triggerUpdate();

			if (!foundElement) {
				selectedElements.remove(element);
				removeFromAABB(element);
				eventDispatcher.fire(new SelectionChangedEvent(this, ChangeType.REMOVED, new FaceData[] {face}, new Element[] {element}));
			} else {
				eventDispatcher.fire(new SelectionChangedEvent(this, ChangeType.REMOVED, new FaceData[] {face}, new Element[] {}));
			}
		}

		return removed;
	}

	/**
	 * Removes all faces from the selected faces list.
	 *
	 * @return Array of faces that were deselected
	 */
	public FaceData[] deselectAllFaces() {
		FaceData[] faces = getSelectedFaces();
		Element[] elements = getSelectedElements();

		selectedFaces.clear();
		selectedElements.clear();

		for (FaceData f : faces) f.getFragment().triggerUpdate();

		eventDispatcher.fire(new SelectionChangedEvent(this, ChangeType.REMOVED, faces, elements));

		removeFromAABB(null);

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

	/**
	 * Returns the axis-aligned bounding box of the selected elements in this
	 * model. The returned array has lowest coordinates at index 0 and highest
	 * coordinates at index 1.
	 *
	 * @return Array containing the AABB of the selected elements or
	 * {@code null} there's no selection
	 */
	public Position[] getSelectionAABB() {
		if (!hasSelection()) return null;
		return new Position[] {selectionAABBFrom.clone(), selectionAABBTo.clone()};
	}

	/**
	 * Returns the center of the axis-aligned bounding box of the selected
	 * elements in this model.
	 *
	 * @return The center enter point of the AABB of the selected elements or
	 * {@code null} if no elements are selected
	 */
	public Position getSelectionAABBCenter() {
		if (!hasSelection()) return null;
		return selectionCenter.clone();
	}

	/**
	 * Updates the AABB to include the given element.
	 *
	 * @param element Element that should be added to the AABB
	 */
	private void addToAABB(Element element) {
		if (selectionCenter == null) {
			selectionAABBFrom.set(element.getFrom());
			selectionAABBTo.set(element.getTo());
		} else {
			Position elementFrom = element.getFrom();
			Position elementTo = element.getTo();

			selectionAABBFrom.set(Math.min(elementFrom.getX(), selectionAABBFrom.getX()), Math.min(elementFrom.getY(), selectionAABBFrom.getY()), Math.min(elementFrom.getZ(), selectionAABBFrom.getZ()));
			selectionAABBTo.set(Math.max(elementTo.getX(), selectionAABBTo.getX()), Math.max(elementTo.getY(), selectionAABBTo.getY()), Math.max(elementTo.getZ(), selectionAABBTo.getZ()));
		}

		updateAABBCenter();
	}

	// TODO: this doesnt consider rotation
	/**
	 * Updates the AABB to no longer include the given element. If the element
	 * is {@code null} the AABB is updated regardless of the elements position.
	 *
	 * @param element
	 */
	private void removeFromAABB(Element element) {
		if (selectedElements.size() == 0) {
			selectionCenter = null;
			return;
		}

		if (element != null) {
			Position elementFrom = element.getFrom();
			Position elementTo = element.getTo();

			// No need to update if the element was in the AABB instead of on its border
			if (
				elementFrom.getX() > selectionAABBFrom.getX() && elementFrom.getY() > selectionAABBFrom.getY() && elementFrom.getZ() > selectionAABBFrom.getZ()
				&& elementTo.getX() < selectionAABBTo.getX() && elementTo.getY() < selectionAABBTo.getY() && elementTo.getZ() < selectionAABBTo.getZ()
			) return;
		}

		selectionAABBFrom.set(selectedElements.get(0).getFrom());
		selectionAABBTo.set(selectedElements.get(0).getTo());

		for (Element e : selectedElements) {
			Position elementFrom = e.getFrom();
			Position elementTo = e.getTo();
			selectionAABBFrom.set(Math.min(elementFrom.getX(), selectionAABBFrom.getX()), Math.min(elementFrom.getY(), selectionAABBFrom.getY()), Math.min(elementFrom.getZ(), selectionAABBFrom.getZ()));
			selectionAABBTo.set(Math.max(elementTo.getX(), selectionAABBTo.getX()), Math.max(elementTo.getY(), selectionAABBTo.getY()), Math.max(elementTo.getZ(), selectionAABBTo.getZ()));
		}

		updateAABBCenter();
	}

	private void updateAABBCenter() {
		selectionCenter = selectionAABBTo.clone().subtract(selectionAABBFrom).multiply(0.5f).add(selectionAABBFrom);
	}

	@EventHandler
	public void onElementsResized(ElementsResizedEvent event) {
		removeFromAABB(null);
	}

	@EventHandler
	public void onElementsRotated(ElementsRotatedEvent event) {
		removeFromAABB(null);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[name=" + name + "]";
	}
}
