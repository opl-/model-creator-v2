package me.opl.apps.modelcreator2.importer;

import java.io.InputStream;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.model.ResourceLocation;

public interface Importer {
	public boolean open(ModelCreator modelCreator, ResourceLocation resourceLocation, InputStream stream);
}
