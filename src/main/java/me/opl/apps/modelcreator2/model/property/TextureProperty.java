package me.opl.apps.modelcreator2.model.property;

import me.opl.apps.modelcreator2.model.Texture;

public class TextureProperty extends Property {
	private Texture value;

	public TextureProperty(Texture initialValue) {
		this.value = initialValue;
	}

	@Override
	public Texture getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		// TODO
	}
}
