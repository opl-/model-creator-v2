package me.opl.apps.modelcreator2.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FolderResourcePack implements ResourcePack {
	private File packDirectory;

	public FolderResourcePack(File packDirectory) {
		this.packDirectory = packDirectory;
	}

	@Override
	public int getVersion() {
		// TODO
		return 0;
	}

	@Override
	public String getName() {
		return packDirectory.getName();
	}

	@Override
	public InputStream getResource(ResourceLocation location) {
		try {
			// FIXME: this shouldnt be loading from textures
			return new FileInputStream(new File(packDirectory, "assets/" + location.getDomain() + "/textures/" + location.getPath() + ".png"));
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
