package me.gorgeousone.tangledmaze.generation.paving;

import me.gorgeousone.tangledmaze.generation.GridCell;
import me.gorgeousone.tangledmaze.generation.GridMap;
import me.gorgeousone.tangledmaze.util.Direction;
import me.gorgeousone.tangledmaze.util.Vec2;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class Room {

	private final Vec2 cellStart;
	private final Vec2 cellSize;

	public Room(Vec2 cellStart, Vec2 cellSize) {
		this.cellStart = cellStart;
		this.cellSize = cellSize;
	}

	public Vec2 getCellStart() {
		return cellStart;
	}

	public Vec2 getCellSize() {
		return cellSize;
	}

	public boolean contains(Vec2 gridPos) {
		return gridPos.getX() >= cellStart.getX() &&
		       gridPos.getZ() >= cellStart.getZ() &&
		       gridPos.getX() < cellStart.getX() + cellSize.getX() &&
		       gridPos.getZ() < cellStart.getZ() + cellSize.getZ();
	}

	public void floodFillRoom(GridCell startCell, GridMap gridMap) {
		PathTree pathTree = startCell.getTree();
		List<Vec2> openEnds = new ArrayList<>();
		openEnds.add(startCell.getGridPos());

		while (!openEnds.isEmpty()) {
			Vec2 gridPos = openEnds.remove(0);
			GridCell cell = gridMap.getCell(gridPos);

			for (Direction facing : Direction.CARDINALS) {
				Vec2 facingVec = facing.getVec2();
				Vec2 newPos1 = gridPos.add(facingVec);
				Vec2 newPos2 = newPos1.clone().add(facingVec);
				PathType pathType2 = gridMap.getPathType(newPos2);

				if (pathType2 == PathType.ROOM) {
					GridCell roomCell = gridMap.getCell(newPos1);
					pathTree.addSegment(roomCell, cell);
					pathTree.addSegment(gridMap.getCell(newPos2), roomCell);
					openEnds.add(newPos2);
				}
			}
		}
		markRoom(gridMap, PathType.PAVED);
	}

	/**
	 * Mark the cells of a room as ROOM type on the grid map.
	 */
	public void markRoom(GridMap gridMap, PathType pathType) {
		Bukkit.broadcastMessage("mark " + cellStart);
		for (int x = cellStart.getX(); x < cellStart.getX() + cellSize.getX(); ++x) {
			for (int z = cellStart.getZ(); z < cellStart.getZ() + cellSize.getZ(); ++z) {
				gridMap.setPathType(x, z, pathType);
			}
		}
	}

}
