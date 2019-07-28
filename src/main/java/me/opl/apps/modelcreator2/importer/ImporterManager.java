package me.opl.apps.modelcreator2.importer;

import java.util.ArrayList;
import java.util.List;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.event.ImporterRegisteringEvent;
import me.opl.apps.modelcreator2.model.ResourceLocation;

public class ImporterManager {
	private ModelCreator modelCreator;

	private List<Importer> importers = new ArrayList<>();

	public ImporterManager(ModelCreator modelCreator) {
		this.modelCreator = modelCreator;
	}

	public boolean addImporter(Importer importer) {
		ImporterRegisteringEvent event = new ImporterRegisteringEvent(importer);
		modelCreator.getGlobalEventDispatcher().fire(event);

		if (event.isCancelled()) return false;

		return importers.add(importer);
	}

	// FIXME: what about blockstates?
	// TODO: should opening stand-alone files be possible too?
	// FIXME: this just doesn't work as is. figure out a proper way of opening files
	public void openFiles(ResourceLocation resourceLocation) {
		/*for (Importer importer : importers) {
			try {
				InputStream inputStream = modelCreator.getRenderManager().getResourceManager().getResourceInputStream(resourceLocation);

				importer.open(modelCreator, resourceLocation, inputStream);

				inputStream.close();
			} catch (Exception e) {
				throw new IllegalArgumentException("Exception thrown opening resource " + resourceLocation + " with " + importer.getClass().getSimpleName(), e);
			}
		}*/
	}
}
