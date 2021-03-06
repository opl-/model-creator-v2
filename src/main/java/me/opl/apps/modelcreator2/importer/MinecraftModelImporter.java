package me.opl.apps.modelcreator2.importer;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.model.Axis;
import me.opl.apps.modelcreator2.model.CuboidElement;
import me.opl.apps.modelcreator2.model.Face;
import me.opl.apps.modelcreator2.model.FaceData;
import me.opl.apps.modelcreator2.model.MinecraftModel;
import me.opl.apps.modelcreator2.model.Position;
import me.opl.apps.modelcreator2.model.ResourceLocation;
import me.opl.apps.modelcreator2.model.Rotation;
import me.opl.apps.modelcreator2.model.UV;

// TODO: differentiate between block and item models. somehow
// TODO: load parent models
// TODO: create an exception or result object to give users feedback on why the loading failed or if any problems were found with the model. could also use it to tell what else needs to be loaded
public class MinecraftModelImporter implements Importer {
	@Override
	public boolean open(ModelCreator modelCreator, ResourceLocation resourceLocation, InputStream stream) {
		JSONObject json;
		try {
			json = new JSONObject(new JSONTokener(stream));
		} catch (JSONException e) {
			return false;
		}

		MinecraftModel model = new MinecraftModel(modelCreator, null, resourceLocation.getPath());

		model.setAmbientOcclusion(json.optBoolean("ambientocclusion", true));

		try {
			createTextures(json, model);

			createElements(json, model);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

		modelCreator.addModel(model);
		return true;
	}

	private void createTextures(JSONObject json, MinecraftModel model) {
		JSONObject texturesObject = json.optJSONObject("textures");

		if (texturesObject == null || texturesObject.length() == 0) return;

		for (String name : JSONObject.getNames(texturesObject)) {
			String location = texturesObject.getString(name);

			if (location.charAt(0) == '#') {
				model.addTexture(name, new ResourceLocation(location));
			} else {
				ResourceLocation resLoc = new ResourceLocation(location);
				model.addTexture(name, new ResourceLocation(resLoc.getDomain(), resLoc.getPath()));
			}
		}
	}

	private void createElements(JSONObject json, MinecraftModel model) {
		JSONArray elementsArray = json.optJSONArray("elements");

		if (elementsArray == null) return;

		model.setHasElements(true);

		if (elementsArray.length() == 0) return;

		for (int i = 0; i < elementsArray.length(); i++) {
			JSONObject element = elementsArray.getJSONObject(i);

			CuboidElement cuboidElement = new CuboidElement(model, arrayToPosition(element.getJSONArray("from")), arrayToPosition(element.getJSONArray("to")));

			if (element.has("rotation")) {
				JSONObject rotation = element.getJSONObject("rotation");

				Position axisVector = Axis.valueOf(rotation.getString("axis").toUpperCase()).getVector();
				float angle = (float) rotation.optDouble("angle", 0);
				cuboidElement.setRotation(new Rotation().setd(axisVector.getX() * angle, axisVector.getY() * angle, axisVector.getZ() * angle));

				JSONArray rotationOrigin = rotation.optJSONArray("origin");
				cuboidElement.setRotationOrigin(rotationOrigin == null ? new Position(8, 8, 8) : arrayToPosition(rotationOrigin));
				cuboidElement.setRotationRescale(rotation.optBoolean("rescale", false));
			}

			JSONObject faces = element.getJSONObject("faces");

			for (Face f : Face.values()) {
				FaceData faceData = cuboidElement.getFragment().getFaceData(f);

				if (faces.has(f.name().toLowerCase())) {
					JSONObject face = faces.getJSONObject(f.name().toLowerCase());

					// Texture
					String textureReference = face.getString("texture");

					if (textureReference.charAt(0) != '#') continue;

					faceData.setTexture(new ResourceLocation(textureReference));

					// UV
					JSONArray uvArray = face.optJSONArray("uv");

					if (uvArray != null) {
						faceData.setUV(arrayToUV(uvArray));
					}
				} else {
					faceData.setVisible(false);
				}
			}

			model.addElement(cuboidElement);
		}
	}

	private Position arrayToPosition(JSONArray array) {
		if (array.length() != 3) throw new IllegalArgumentException("Passed JSON array isn't of length 3 (" + array.length() + ")");

		float[] pos = new float[3];
		for (int i = 0; i < 3; i++) pos[i] = (float) array.getDouble(i);

		return new Position(pos);
	}

	private UV arrayToUV(JSONArray array) {
		if (array.length() != 4) throw new IllegalArgumentException("Passed JSON array isn't of length 4 (" + array.length() + ")");

		float[] uv = new float[4];
		for (int i = 0; i < 4; i++) uv[i] = (float) array.getDouble(i);

		return new UV(uv[0] / 16, uv[1] / 16, uv[2] / 16, uv[3] / 16);
	}
}
