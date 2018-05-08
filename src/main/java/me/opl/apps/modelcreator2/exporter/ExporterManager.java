package me.opl.apps.modelcreator2.exporter;

import java.util.ArrayList;
import java.util.List;

import me.opl.apps.modelcreator2.ModelCreator;

public class ExporterManager {
	private ModelCreator modelCreator;

	private List<ModelExporter> exporters = new ArrayList<>();

	public ExporterManager(ModelCreator modelCreator) {
		this.modelCreator = modelCreator;
	}

	/* public boolean addImporter(ModelExporter modelExporter) {
		return exporters.add(Exporter);
	}

	public BaseModel[] openFile(ResourceLocation resourceLocation) {
		for (ModelExporter me : exporters) {
			try {
				InputStream inputStream = modelCreator.getRenderManager().getResourceManager().getResourceInputStream(resourceLocation);

				BaseModel[] models = me.open(modelCreator, resourceLocation, inputStream);

				inputStream.close();

				if (models != null && models.length > 0) return models;
			} catch (Exception e) {
				throw new IllegalArgumentException("Exception thrown opening resource " + resourceLocation + " with " + mi.getClass().getSimpleName(), e);
			}
		}

		return null;
	} */
}
