package me.opl.apps.modelcreator2.model;

public class Grid {
	private float gridSize;
	private float halfGrid;

	public Grid(float gridSize) {
		setGridSize(gridSize);
	}

	public float getGridSize() {
		return gridSize;
	}

	public void setGridSize(float gridSize) {
		this.gridSize = gridSize;
		this.halfGrid = gridSize / 2f;
	}

	public Position snapToGrid(Position position) {
		return position.clone().subtract(((position.getX() + halfGrid) % gridSize) - halfGrid, ((position.getY() + halfGrid) % gridSize) - halfGrid, ((position.getZ() + halfGrid) % gridSize) - halfGrid);
	}

	public Position snapToGrid(Position position, Position originalPosition) {
		Position v = position.clone().subtract(originalPosition);

		v.subtract(((v.getX() + halfGrid) % gridSize) - halfGrid, ((v.getY() + halfGrid) % gridSize) - halfGrid, ((v.getZ() + halfGrid) % gridSize) - halfGrid);

		return v.add(originalPosition);
	}
}
