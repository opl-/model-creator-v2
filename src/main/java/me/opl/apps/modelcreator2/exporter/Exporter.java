package me.opl.apps.modelcreator2.exporter;

import java.io.InputStream;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.model.ResourceLocation;

public interface Exporter {
	public byte[] open(ModelCreator modelCreator, ResourceLocation resourceLocation, InputStream stream);
}
