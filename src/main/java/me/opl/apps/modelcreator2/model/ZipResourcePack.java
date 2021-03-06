package me.opl.apps.modelcreator2.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipResourcePack implements ResourcePack {
	private File packFile;
	private ZipFile zipFile;

	public ZipResourcePack(File packFile) {
		this.packFile = packFile;

		try {
			zipFile = new ZipFile(packFile);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public int getVersion() {
		// TODO
		return 0;
	}

	@Override
	public String getName() {
		return packFile.getName();
	}

	@Override
	public InputStream getResourceStream(String resourcePath) {
		try {
			ZipEntry entry = zipFile.getEntry(resourcePath);

			if (entry == null) return null;

			return zipFile.getInputStream(entry);
		} catch (ZipException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}
