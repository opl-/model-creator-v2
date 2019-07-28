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
	public InputStream getResourceStream(String resourcePath) {
		try {
			return new FileInputStream(new File(packDirectory, resourcePath));
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
