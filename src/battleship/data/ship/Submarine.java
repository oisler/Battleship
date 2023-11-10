package battleship.data.ship;

import battleship.data.ship.Ship;

public class Submarine extends Ship {

    private static final int SUBMARINE_CELLS = 3;
    private static final String SUBMARINE_NAME = "Submarine";

    public Submarine() {
        super(SUBMARINE_CELLS, SUBMARINE_NAME);
    }
}
