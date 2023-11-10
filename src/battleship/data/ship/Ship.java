package battleship.data.ship;

import battleship.data.game.BattleShipGame;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Ship {

    private final int cells;
    private final String name;

    private boolean shipIsPlaced;
    private final Set<BattleShipGame.Coordinate> coordinates;

    private int hits;

    public Ship(int cells, String name) {
        this.cells = cells;
        this.name = name;
        this.hits = 0;
        this.shipIsPlaced = false;
        this.coordinates = new LinkedHashSet<>();
    }

    public int getCells() {
        return cells;
    }

    public String getName() {
        return name;
    }

    public int getHits() {
        return hits;
    }

    public void addHit() {
        if (getHits() < getCells()) {
            hits++;
        }
    }

    public boolean isSink() {
        return cells == hits;
    }

    public void setShipIsPlaced() {
        this.shipIsPlaced = true;
    }

    public boolean isShipIsPlaced() {
        return shipIsPlaced;
    }

    public void addCoordinate(BattleShipGame.Coordinate coordinate) {
        coordinates.add(coordinate);
    }

    public Set<BattleShipGame.Coordinate> getCoordinates() {
        return Collections.unmodifiableSet(coordinates);
    }
}
