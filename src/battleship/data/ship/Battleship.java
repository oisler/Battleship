package battleship.data.ship;

public class Battleship extends Ship {

    private static final int BATTLESHIP_CELLS = 4;
    private static final String BATTLESHIP_NAME = "Battleship";

    public Battleship() {
        super(BATTLESHIP_CELLS, BATTLESHIP_NAME);
    }
}
