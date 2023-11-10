package battleship.data.ship;

public class AircraftCarrier extends Ship {

    private static final int AIRCRAFT_CARRIER_CELLS = 5;
    private static final String AIRCRAFT_CARRIER_NAME = "Aircraft Carrier";

    public AircraftCarrier() {
        super(AIRCRAFT_CARRIER_CELLS, AIRCRAFT_CARRIER_NAME);
    }
}
