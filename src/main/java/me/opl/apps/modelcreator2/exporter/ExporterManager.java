package me.opl.apps.modelcreator2.exporter;

import java.util.ArrayList;
import java.util.List;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.event.ExporterRegisteringEvent;

public class ExporterManager {
	private ModelCreator modelCreator;

	private List<Exporter> exporters = new ArrayList<>();

	public ExporterManager(ModelCreator modelCreator) {
		this.modelCreator = modelCreator;
	}

	public boolean addExporter(Exporter exporter) {
		ExporterRegisteringEvent event = new ExporterRegisteringEvent(exporter);
		modelCreator.getGlobalEventDispatcher().fire(event);

		if (event.isCancelled()) return false;

		return exporters.add(exporter);
	}

	/* public BaseModel[] openFile(ResourceLocation resourceLocation) {
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
