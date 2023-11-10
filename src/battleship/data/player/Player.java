package battleship.data.player;

import battleship.data.ship.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static battleship.common.Constants.FOG_OF_WAR;

public class Player {

    public Player(String name) {
        this.name = name;
        this.grid = initGrid();
        this.fleet = initFleet();
    }

    private final String name;
    private final char[][] grid;
    private final Set<Ship> fleet;

    private char[][] initGrid() {
        var grid = new char[10][10];

        for (int i = 0; i < grid[0].length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = FOG_OF_WAR;
            }
        }

        return grid;
    }

    private Set<Ship> initFleet() {
        var fleet = new LinkedHashSet<Ship>(5);

        fleet.add(new AircraftCarrier());
        fleet.add(new Battleship());
        fleet.add(new Submarine());
        fleet.add(new Cruiser());
        fleet.add(new Destroyer());

        return fleet;
    }

    public Set<Ship> getFleet() {
        return Collections.unmodifiableSet(this.fleet);
    }

    public String getName() {
        return this.name;
    }

    public char[][] getGrid() {
        return this.grid;
    }
}
