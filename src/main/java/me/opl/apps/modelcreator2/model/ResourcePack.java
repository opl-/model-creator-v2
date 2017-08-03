package me.opl.apps.modelcreator2.model;

import java.io.InputStream;

public interface ResourcePack {
	public int getVersion();

	public String getName();

	public InputStream getResource(ResourceLocation location);
}
