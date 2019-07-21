package me.opl.apps.modelcreator2.model;

import java.util.ArrayList;

public class ElementGroup {
	private String name;
	private ArrayList<Element> elements = new ArrayList<>();
	private ArrayList<ElementGroup> elementGroups = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public Element[] getElements() {
		Element[] elementArr = new Element[elements.size()];
		elements.toArray(elementArr);
		return elementArr;
	}

	public int getElementCount() {
		return elements.size();
	}

	public boolean addElement(Element element) {
		return elements.add(element);
	}

	public boolean removeElement(Element element) {
		return elements.remove(element);
	}

	public ElementGroup[] getElementGroups() {
		ElementGroup[] elementGroupsArr = new ElementGroup[elementGroups.size()];
		elements.toArray(elementGroupsArr);
		return elementGroupsArr;
	}

	public int getElementGroupCount() {
		return elementGroups.size();
	}

	public boolean addGroup(ElementGroup group) {
		return elementGroups.add(group);
	}

	public boolean removeElement(ElementGroup group) {
		return elementGroups.remove(group);
	}
}
