package me.opl.apps.modelcreator2;

import me.opl.apps.modelcreator2.importer.MinecraftModelImporter;

public class ModelCreatorFactory {
	public static ModelCreator createModelCreator() {
		ModelCreator instance = new ModelCreator();

		// Register importers/exporters
		instance.getImporterManager().addImporter(new MinecraftModelImporter());

		return instance;
	}
}
