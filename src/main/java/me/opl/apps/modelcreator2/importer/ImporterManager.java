package me.opl.apps.modelcreator2.importer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.ResourceLocation;

public class ImporterManager {
	private ModelCreator modelCreator;

	private List<ModelImporter> importers = new ArrayList<>();

	public ImporterManager(ModelCreator modelCreator) {
		this.modelCreator = modelCreator;
	}

	public boolean addImporter(ModelImporter modelImporter) {
		return importers.add(modelImporter);
	}

	// FIXME: what about blockstates?
	public BaseModel[] openFile(ResourceLocation resourceLocation) {
		for (ModelImporter mi : importers) {
			try {
				InputStream inputStream = modelCreator.getRenderManager().getResourceManager().getResourceInputStream(resourceLocation);

				BaseModel[] models = mi.open(modelCreator, resourceLocation, inputStream);

				inputStream.close();

				if (models != null && models.length > 0) return models;
			} catch (Exception e) {
				throw new IllegalArgumentException("Exception thrown opening resource " + resourceLocation + " with " + mi.getClass().getSimpleName(), e);
			}
		}

		return null;
	}
}
