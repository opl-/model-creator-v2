package me.opl.apps.modelcreator2.model;

import java.util.Objects;

public class ResourceLocation {
	private String domain;
	private String path;

	public ResourceLocation(String location) {
		int collon = location.indexOf(58);

		this.domain = collon <= 0 ? "minecraft" : location.substring(0, collon).toLowerCase();
		this.path = collon == -1 ? location : location.substring(collon + 1);
	}

	public ResourceLocation(String domain, String path) {
		if (domain == null) throw new IllegalArgumentException("domain is null");
		if (path == null) throw new IllegalArgumentException("path is null");

		this.domain = domain;
		this.path = path;
	}

	public String getDomain() {
		return domain;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return domain + ":" + path;
	}

	@Override
	public int hashCode() {
		return Objects.hash(domain, path);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		else if (!(obj instanceof ResourceLocation)) return false;
		else return ((ResourceLocation) obj).toString().equals(toString());
	}
}
