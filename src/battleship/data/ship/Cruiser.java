package battleship.data.ship;

public class Cruiser extends Ship {

    private static final int CRUISER_CELLS = 3;
    private static final String CRUISER_NAME = "Cruiser";

    public Cruiser() {
        super(CRUISER_CELLS, CRUISER_NAME);
    }
}
