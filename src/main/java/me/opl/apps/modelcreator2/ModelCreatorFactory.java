package me.opl.apps.modelcreator2;

import me.opl.apps.modelcreator2.importer.MinecraftBlockModelImporter;

public class ModelCreatorFactory {
	public static ModelCreator createModelCreator() {
		ModelCreator instance = new ModelCreator();

		// Register importers/exporters
		instance.getImporterManager().addImporter(new MinecraftBlockModelImporter());

		return instance;
	}
}
