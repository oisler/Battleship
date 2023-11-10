package battleship.data.game;

import battleship.data.player.Player;
import battleship.data.ship.Ship;

import java.util.Arrays;
import java.util.Scanner;

import static battleship.common.Constants.*;

public final class BattleShipGame {

    private final Scanner scanner;
    private final Player one;
    private final Player two;

    private static int roundCounter = 0;

    public BattleShipGame() {
        this.scanner = new Scanner(System.in);
        this.one = new Player("Player 1");
        this.two = new Player("Player 2");
    }

    public void start() {
        placeShips(one);
        pressEnterLoop();

        placeShips(two);

        boolean gameIsFinished = false;
        while (!gameIsFinished) {
            pressEnterLoop();

            var activePlayer = getActivePlayer();
            var passivePlayer = getPassivePlayer();

            printPvP(activePlayer, passivePlayer);

            var coordinate = takeShot(activePlayer);
            validateShot(coordinate, passivePlayer);
            gameIsFinished = isGameFinished(passivePlayer);

            roundCounter++;
        }
    }

    private void printPvP(Player activePlayer, Player passivePlayer) {
        printGrid(passivePlayer, true);
        System.out.println(BORDER);
        printGrid(activePlayer, false);
    }

    private void placeShips(Player player) {
        var fleet = player.getFleet();
        System.out.printf("%s, place your ships on the game field\n", player.getName());
        printGrid(player, true);
        for (Ship ship : fleet) {
            System.out.printf("\nEnter the coordinates of the %s (%d cells):\n", ship.getName(), ship.getCells());
            boolean isShipPLaced;
            do {
                isShipPLaced = placeShip(player, ship);
            } while (!isShipPLaced);

            printGrid(player, false);
        }
    }

    private void printGrid(Player player, boolean hide) {
        System.out.println();
        var grid = player.getGrid();
        System.out.println(COLUMN_NUMBERS);
        for (int i = 0; i < grid.length; i++) {
            System.out.print(ROW_LETTERS[i] + SPACE);
            for (int j = 0; j < grid.length; j++) {
                System.out.print((hide && grid[i][j] == SHIP ? FOG_OF_WAR : grid[i][j]) + SPACE);
            }
            System.out.println();
        }
    }

    private boolean placeShip(Player player, Ship ship) {
        var grid = player.getGrid();
        Coordinate[] coordinates;
        try {
            coordinates = createCoordinatesFromUserInput();
        } catch (RuntimeException e) {
            return false;
        }

        if (!isHorizontal(coordinates) && !isVertical(coordinates) || isHorizontal(coordinates) && isVertical(coordinates)) {
            System.out.print("Error! Wrong ship location! Try again:\n");
            return false;
        } else if (isHorizontal(coordinates)) {
            int firstNumber = Integer.parseInt(coordinates[0].digit);
            int secondNumber = Integer.parseInt(coordinates[1].digit);
            if (Math.abs(firstNumber - secondNumber) + 1 != ship.getCells()) {
                System.out.printf("Error! Wrong length of the %s! Try again:\n", ship.getName());
                return false;
            }
        } else if (isVertical(coordinates)) {
            int firstNumber = getIndexOfLetter(coordinates[0].letter);
            int secondNumber = getIndexOfLetter(coordinates[1].letter);
            if (Math.abs(firstNumber - secondNumber) + 1 != ship.getCells()) {
                System.out.printf("Error! Wrong length of the %s! Try again:\n", ship.getName());
                return false;
            }
        }

        Coordinate firstCoordinate = coordinates[0];
        Coordinate secondCoordinate = coordinates[1];
        if (isHorizontal(coordinates)) {
            int[] num = {Integer.parseInt(firstCoordinate.digit) - 1, Integer.parseInt(secondCoordinate.digit) - 1};
            Arrays.sort(num);
            int startingPoint = num[0];
            int endingPoint = num[1];
            int rowIndex = getIndexOfLetter(firstCoordinate.letter());
            if (isToCloseToAnotherHorizontalShip(player, startingPoint, endingPoint, rowIndex)) {
                System.out.print("Error! You placed it too close to another one. Try again:\n");
                return false;
            }
            for (int columnIndex = startingPoint; columnIndex <= endingPoint; columnIndex++) {
                grid[rowIndex][columnIndex] = SHIP;
                ship.addCoordinate(new Coordinate(firstCoordinate.letter, String.valueOf(columnIndex + 1)));
            }
        } else if (isVertical(coordinates)) {
            int[] num = {getIndexOfLetter(firstCoordinate.letter), getIndexOfLetter(secondCoordinate.letter)};
            Arrays.sort(num);
            int startingPoint = num[0];
            int endingPoint = num[1];
            int columnIndex = Integer.parseInt(firstCoordinate.digit) - 1;
            if (isToCloseToAnotherVerticalShip(player, startingPoint, endingPoint, columnIndex)) {
                System.out.print("Error! You placed it too close to another one. Try again:\n");
                return false;
            }
            for (int rowIndex = startingPoint; rowIndex <= endingPoint; rowIndex++) {
                grid[rowIndex][columnIndex] = SHIP;
                ship.addCoordinate(new Coordinate(ROW_LETTERS[rowIndex], String.valueOf(Integer.parseInt(firstCoordinate.digit))));
            }
        }

        ship.setShipIsPlaced();
        return ship.isShipIsPlaced();
    }

    private void pressEnterLoop() {
        System.out.println("Press Enter and pass the move to another player");
        boolean isEnter = false;
        while (!isEnter) {
            String input = scanner.nextLine();
            isEnter = input.isEmpty();
        }
    }

    private Player getPassivePlayer() {
        return roundCounter % 2 == 0 ? two : one;
    }

    private Player getActivePlayer() {
        return roundCounter % 2 == 0 ? one : two;
    }

    private void validateShot(Coordinate coordinate, Player passivePlayer) {
        var grid = passivePlayer.getGrid();
        int row = getIndexOfLetter(coordinate.letter);
        int column = Integer.parseInt(coordinate.digit) - 1;
        char c = grid[row][column];
        boolean isNewHit = c == SHIP;
        boolean isAlreadyHit = c == HIT;
        if (isNewHit || isAlreadyHit) {
            for (Ship ship : passivePlayer.getFleet()) {
                if (ship.getCoordinates().contains(coordinate)) {
                    if (isNewHit) {
                        grid[row][column] = HIT;
                        ship.addHit();
                    }
                    if (isGameFinished(passivePlayer)) {
                        System.out.println("\nYou sank the last ship. You won. Congratulations!");
                    } else if (ship.isSink()) {
                        System.out.println("\nYou sank a ship!");
                    } else {
                        System.out.println("\nYou hit a ship!");
                    }
                }
            }
        } else {
            grid[row][column] = MISS;
            System.out.println("\nYou missed!");
        }
    }

    private boolean isGameFinished(Player passivePlayer) {
        var grid = passivePlayer.getGrid();
        boolean isFinished = true;

        for (char[] chars : grid) {
            for (int j = 0; j < grid.length; j++) {
                if (chars[j] == SHIP) {
                    isFinished = false;
                    break;
                }
            }
        }

        return isFinished;
    }

    private Coordinate takeShot(Player activePlayer) {
        System.out.printf("%s, it's your turn:\n", activePlayer.getName());
        String userInput = scanner.nextLine();
        boolean validInput = isValidInputForShot(userInput);
        while (!validInput) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            userInput = scanner.nextLine();
            validInput = isValidInputForShot(userInput);
        }

        return new Coordinate(userInput.substring(0, 1), userInput.substring(1));
    }

    private static boolean isValidInputForShot(String userInput) {
        return userInput.matches("^([a-jA-J])([1-9]|10)$");
    }

    private boolean isToCloseToAnotherVerticalShip(Player player, int rowStartIndex, int rowEndIndex, int columnIndex) {
        var grid = player.getGrid();
        if (rowStartIndex != 0 && grid[rowStartIndex - 1][columnIndex] == SHIP) {
            return true;
        }
        if (rowEndIndex != 9 && grid[rowEndIndex + 1][columnIndex] == SHIP) {
            return true;
        }
        if (columnIndex - 1 >= 0) {
            for (int rowIndex = rowStartIndex; rowIndex <= rowEndIndex; rowIndex++) {
                if (grid[rowIndex][columnIndex] == SHIP) {
                    return true;
                }
            }
        }
        if (columnIndex + 1 <= 9) {
            for (int rowIndex = rowStartIndex; rowIndex <= rowEndIndex; rowIndex++) {
                if (grid[rowIndex][columnIndex] == SHIP) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isToCloseToAnotherHorizontalShip(Player player, int columnStartIndex, int columnEndIndex, int rowIndex) {
        var grid = player.getGrid();
        if (columnStartIndex != 0 && grid[rowIndex][columnStartIndex - 1] == SHIP) {
            return true;
        }
        if (columnEndIndex != 9 && grid[rowIndex][columnEndIndex + 1] == SHIP) {
            return true;
        }
        if (rowIndex - 1 >= 0) {
            for (int columnIndex = columnStartIndex; columnIndex <= columnEndIndex; columnIndex++) {
                if (grid[rowIndex][columnIndex] == SHIP) {
                    return true;
                }
            }
        }
        if (rowIndex + 1 <= 9) {
            for (int columnIndex = columnStartIndex; columnIndex <= columnEndIndex; columnIndex++) {
                if (grid[rowIndex][columnIndex] == SHIP) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getIndexOfLetter(String letter) {
        int index = -1;
        for (int i = 0; i < ROW_LETTERS.length; i++) {
            if (ROW_LETTERS[i].equals(letter)) {
                index = i;
            }
        }
        return index;
    }

    private static boolean isHorizontal(Coordinate[] coordinates) {
        return coordinates[0].letter.equals(coordinates[1].letter);
    }

    private boolean isVertical(Coordinate[] coordinates) {
        return coordinates[0].digit.equals(coordinates[1].digit);
    }

    private Coordinate[] createCoordinatesFromUserInput() {
        String userInput = scanner.nextLine();
        if (!userInput.matches("^([a-jA-J])([1-9]|10)( )([a-jA-J])([1-9]|10)")) {
            throw new RuntimeException("Invalid input");
        }

        String[] splittedUserInput = userInput.split("\\s");
        var c1 = new Coordinate(splittedUserInput[0].substring(0, 1), splittedUserInput[0].substring(1));
        var c2 = new Coordinate(splittedUserInput[1].substring(0, 1), splittedUserInput[1].substring(1));

        return new Coordinate[]{c1, c2};
    }

    public record Coordinate(String letter, String digit) {
        public Coordinate {
            if (!isValidLetter(letter) || isValidDigit(digit)) {
                throw new IllegalArgumentException("Invalid coordinate");
            }
        }

        private boolean isValidDigit(String digit) {
            return digit != null && !digit.matches("^[0-9]{1,2}$");
        }

        private boolean isValidLetter(String letter) {
            return letter.length() == 1 && Arrays.asList(ROW_LETTERS).contains(letter);
        }
    }
}
