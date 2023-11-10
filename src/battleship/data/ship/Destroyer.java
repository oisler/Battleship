package battleship.data.ship;

public class Destroyer extends Ship {

    private static final int DESTROYER_CELLS = 2;
    private static final String DESTROYER_NAME = "Destroyer";

    public Destroyer() {
        super(DESTROYER_CELLS, DESTROYER_NAME);
    }
}
