package Cargame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
import java.awt.Rectangle;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private int conditionalScore = 50;
    private final CarGame carGame;
    private Car playerCar;
    private boolean playerIsHit = false;
    private ArrayList<OpponentCar> opponentCars;
    private ArrayList<Scenery> leftScenery;
    private ArrayList<Scenery> rightScenery;
    private Random random;
    private Timer timer;
    private int score;
    private int speed;
    private int delay;
    private boolean gameover;
    private int roadMove;
    private OpponentCar ambulanceCar;
    private boolean ambulancePresent;
    private ArrayList<Obstacle> obstacles;
    private static final int OBSTACLE_SPAWN_CHANCE =  1; // chance per appear
    private int OPPONENT_CAR_SPEED = 15; // Reduced from 25
    private static final int CONSTRUCTION_SIGN_RARITY = 1; // 1 in 20 chance when an obstacle spawns
    private static int OBSTACLE_SPEED = 20; // Reduced from 20

    private int panelWidth;
    private int panelHeight;
    private static final int ROAD_WIDTH = 600;
    private static final int ROAD_START = 100;
    private static final int NUM_LANES = 4;
    private static final int LANE_WIDTH = ROAD_WIDTH / NUM_LANES;
    private static final int[] LANE_POSITIONS = new int[NUM_LANES];
    static  {
        for (int i = 0; i < NUM_LANES; i++) {
            LANE_POSITIONS[i] = ROAD_START + (i * LANE_WIDTH) + (LANE_WIDTH / 2) -2; // Subtract 2 pixels cuz I shift each lane position slightly to the left
        }
    }
    private static final int[] OPPONENT_START_POSITIONS = {-240, -480, -720, -960};
    private boolean visibleInGame;

    public GamePanel(CarGame carGame, int width, int height) {
        this.carGame = carGame;
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        this.panelWidth = width;
        this.panelHeight = height;

        random = new Random();
        timer = new Timer(100, this);
        timer.start();

        obstacles = new ArrayList<>();
        initializeGame();
    }

    public void initializeGame() {
        opponentCars = new ArrayList<>();
        playerCar = new Car(LANE_POSITIONS[0], 600, "Game/src/assets/mycar.png");
        opponentCars = new ArrayList<>();
        leftScenery = new ArrayList<>();
        rightScenery = new ArrayList<>();
        OPPONENT_CAR_SPEED = 15;
        score = 0;
        speed = 40;
        delay = 100;
        gameover = false;
        playerIsHit = false;
        roadMove = 0;
        conditionalScore = 50;

        obstacles.clear();
        initializeOpponentCars();
        initializeScenery();
    }

    //initialize opponent cars by clearing existing cars with random and adds ambulance chance
    private void initializeOpponentCars() {
        opponentCars.clear();
        for (int i = 0; i < 3; i++) {
            int lane = random.nextInt(NUM_LANES);
            int startPos = random.nextInt(4);
            opponentCars.add(new OpponentCar(LANE_POSITIONS[lane], OPPONENT_START_POSITIONS[startPos],
                    "Game/src/assets/gamecar" + (i + 1) + ".png", lane));
        }

        ambulancePresent = random.nextInt(100) < 70;
        if (ambulancePresent) {
            int ambulanceLane = random.nextInt(NUM_LANES);
            ambulanceCar = new OpponentCar(LANE_POSITIONS[ambulanceLane], -500,
                    "Game/src/assets/ambulance.png", ambulanceLane);
        } else {
            ambulanceCar = null;
        }
    }

    //initialize scenery on both sides
    private void initializeScenery() {
        initializeSideScenery(leftScenery, -10);
        initializeSideScenery(rightScenery, 710);
    }

    private void initializeSideScenery(ArrayList<Scenery> sideScenery, int xPos) {
        sideScenery.clear();
        String[] mainSceneryTypes = {"house", "building"};
        String[] houseImages = {"house1", "house2", "house3", "house4", "house5"};
        String[] buildingImages = {"building1", "building2", "building3", "building4"};

        int yPos = -700;
        while (yPos < 700) {
            String type = mainSceneryTypes[random.nextInt(mainSceneryTypes.length)];
            String imagePath = "";

            switch (type) {
                case "house":
                    imagePath = "Game/src/assets/"
                            + houseImages[random.nextInt(houseImages.length)] + ".png";
                    break;
                case "building":
                    imagePath = "Game/src/assets/"
                            + buildingImages[random.nextInt(buildingImages.length)] + ".png";
                    break;
            }

            sideScenery.add(new Scenery(xPos, yPos, imagePath, type));
            yPos += 150 + random.nextInt(20);  // Increase space between main structures

            // Add trees between houses and buildings
            int treesToAdd = random.nextInt(3) + 1; // Add 1 to 3 trees
            for (int i = 0; i < treesToAdd; i++) {
                imagePath = "Game/src/assets/tree"
                        + (random.nextInt(3) + 1) + ".png";
                sideScenery.add(new Scenery(xPos, yPos, imagePath, "tree"));
                yPos += 80 + random.nextInt(40);  // Adjust spacing between trees

                // 10% chance to add an elephant near a tree
                if (random.nextInt(100) < 80) {
                    imagePath = "Game/src/assets/elephant.png";
                    sideScenery.add(new Scenery(xPos, yPos - 40, imagePath, "elephant"));
                }
            }

            imagePath = "src/assets/tree"
                    + (random.nextInt(3) + 1) + ".png";
            sideScenery.add(new Scenery(xPos, yPos, imagePath, "tree"));
            yPos += 80 + random.nextInt(40);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        drawRoad(g);
        drawScenery(g);
        drawPlayerCar(g);
        drawOpponentCars(g);
        drawScore(g);

        drawObstacles(g);

        if (gameover) {
            drawGameOver(g);
        } else {
            if (playerIsHit) {
                updateGameState();
            }
        }
    }

    private void drawObstacles(Graphics g) {
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g, this);
        }
    }

    private void drawBackground(Graphics g) {
        g.setColor(new Color(133, 174, 90)); // Light green for grass
        g.fillRect(0, 0, panelWidth, panelHeight);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(ROAD_START, 0, ROAD_WIDTH, panelHeight);
    }

    private void drawRoad(Graphics g) {
        g.setColor(Color.WHITE);

        // Draw lane markers
        for (int i = 1; i < NUM_LANES; i++) {
            int x = ROAD_START + i * LANE_WIDTH;
            if (i == NUM_LANES / 2) {
                // Central double line
                g.fillRect(x - 5, 0, 4, panelHeight);
                g.fillRect(x + 5, 0, 4, panelHeight);
            } else {
                // Other lane markers
                for (int y = -100; y < panelHeight; y += 100) {
                    g.fillRect(x - 2, (y + roadMove * 10) % panelHeight, 4, 60);
                }
            }
        }
    }


    private void drawScenery(Graphics g) {
        drawSideScenery(g, leftScenery);
        drawSideScenery(g, rightScenery);
    }

    private void drawSideScenery(Graphics g, ArrayList<Scenery> sideScenery) {
        for (Scenery item : sideScenery) {
            item.getSceneryImage().paintIcon(this, g, item.getXpos(), item.getYpos());
            item.setYpos(item.getYpos() + 50);
        }

        // Remove off-screen items and add new ones at the top
        sideScenery.removeIf(item -> item.getYpos() > 700);

        if (!sideScenery.isEmpty() && sideScenery.get(0).getYpos() > -150) {
            int xPos = sideScenery.get(0).getXpos();
            String type = getRandomSceneryType();
            String imagePath = getImagePathForType(type);
            sideScenery.add(0, new Scenery(xPos, sideScenery.get(0).getYpos() - 250, imagePath, type));

            // Add a tree if the new item is a house or building
            if (type.equals("house") || type.equals("building")) {
                imagePath = "Game/src/assets/tree"
                        + (random.nextInt(3) + 1) + ".png";
                sideScenery.add(1, new Scenery(xPos, sideScenery.get(0).getYpos() + 150, imagePath, "tree"));
            }
        }
    }

    private String getRandomSceneryType() {
        String[] types = {"house", "building", "elephant", "tree"};
        return types[random.nextInt(types.length)];
    }

    private String getImagePathForType(String type) {
        String basePath = "Game/src/assets/";
        switch (type) {
            case "house":
                return basePath + "house" + (random.nextInt(5) + 1) + ".png";
            case "building":
                return basePath + "building" + (random.nextInt(4) + 1) + ".png";
            case "tree":
                return basePath + "tree" + (random.nextInt(6) + 1) + ".png";
            default:
                return "";
        }
    }

    private void drawPlayerCar(Graphics g) {
        int carWidth = playerCar.getCarImage().getIconWidth();
        double xPos = playerCar.getXpos() - carWidth / 2; // Center the car in its lane
        playerCar.getCarImage().paintIcon(this, g, (int)xPos, (int)playerCar.getYpos());
    }

    //check for collision between two cars
    private boolean isColliding(OpponentCar car1, OpponentCar car2) {
        Rectangle bounds1 = new Rectangle((int)car1.getXpos(), (int)car1.getYpos(), car1.getCarImage().getIconWidth(), car1.getCarImage().getIconHeight());
        Rectangle bounds2 = new Rectangle((int)car2.getXpos(), (int)car2.getYpos(), car2.getCarImage().getIconWidth(), car2.getCarImage().getIconHeight());
        return bounds1.intersects(bounds2);
    }


    private void resetOpponentCar(OpponentCar car) {
        int attempts = 0;
        do {
            car.setLane(random.nextInt(NUM_LANES));
            car.setXpos(LANE_POSITIONS[car.getLane()]);
            car.setYpos(OPPONENT_START_POSITIONS[random.nextInt(4)] - random.nextInt(200));  // Add random offset
            attempts++;
        } while ((isCollidingWithAny(car) || isCollidingWithObstacle(car)) && attempts < 10);

        if (attempts >= 10) {
            // If a suitable position isn't found after 10 attempts, it forces a position change to keep the game moving.
            car.setYpos((int)car.getYpos() - 300 - random.nextInt(100));  // Increase spacing
        }
    }

     //Checks if an opponent car is colliding with any obstacle.
    private boolean isCollidingWithObstacle(OpponentCar car) {
        Rectangle carBounds = new Rectangle(
                (int)car.getXpos() - car.getCarImage().getIconWidth() / 2,
                (int)car.getYpos(),
                car.getCarImage().getIconWidth(),
                car.getCarImage().getIconHeight()
        );

        for (Obstacle obstacle : obstacles) {
            if (obstacle instanceof ConstructionSign) {
                // Allow cars to stack behind construction signs
                if (car.getLane() == getCurrentLane(obstacle.getX()) &&
                        carBounds.intersects(obstacle.getBounds())) {
                    return true;
                }
            }
        }
        return false;
    }

    //Checks if an opponent car is colliding with any car.
    private boolean isCollidingWithAny(OpponentCar car) {
        for (OpponentCar otherCar : opponentCars) {
            if (car != otherCar && isColliding(car, otherCar)) {
                return true;
            }
        }
        return ambulancePresent && ambulanceCar != null && isColliding(car, ambulanceCar);
    }

    private void drawOpponentCars(Graphics g) {
        updateOpponentCars();
        for (OpponentCar car : opponentCars) {
            int carWidth = car.getCarImage().getIconWidth();
            double xPos = car.getXpos() - carWidth / 2;
            car.getCarImage().paintIcon(this, g, (int)xPos, (int)car.getYpos());
        }
        if (ambulancePresent && ambulanceCar != null) {
            int ambulanceWidth = ambulanceCar.getCarImage().getIconWidth();
            double xPos = ambulanceCar.getXpos() - ambulanceWidth / 2;
            ambulanceCar.getCarImage().paintIcon(this, g, (int)xPos, (int)ambulanceCar.getYpos());
        }
    }


    private void drawScore(Graphics g) {
        // Score display (left side)
        g.setColor(Color.gray);
        g.fillRect(20, 20, 220, 50);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(25, 25, 210, 40);

        // Speed display (right side) - moved further right
        g.setColor(Color.gray);
        g.fillRect(panelWidth - 200, 20, 180, 50);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(panelWidth - 195, 25, 170, 40);

        g.setColor(Color.yellow);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Score : " + score, 30, 55);
        g.drawString(speed + " Km/h", panelWidth - 185, 55);
    }

    private void drawGameOver(Graphics g) {
        int gameOverWidth = 400;
        int gameOverHeight = 150;
        int gameOverX = (panelWidth - gameOverWidth) / 2;
        int gameOverY = (panelHeight - gameOverHeight) / 2;

        g.setColor(Color.gray);
        g.fillRect(gameOverX, gameOverY, gameOverWidth, gameOverHeight);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(gameOverX + 10, gameOverY + 10, gameOverWidth - 20, gameOverHeight - 20);

        g.setFont(new Font("Serif", Font.BOLD, 50));
        g.setColor(Color.yellow);
        g.drawString("Game Over !", gameOverX + 70, gameOverY + 70);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Press Enter to Restart", gameOverX + 50, gameOverY + 120);
    }

    private void updateGameState() {
        if (speed < 180) {
            speed++;
        }


        score++;
        if (score % 100 == 0) {  // Changed from 50 to 100
//            speed += 5;  // Reduced speed increase from 1 to 5 every 100 points

            delay -= 5;  // Reduced delay decrease from 10 to 5
            if (delay < 100) {  // Increased min delay from 60 to 100
                delay = 100;
            }
            timer.setDelay(delay);
        }
        roadMove = (roadMove + 1) % 2;
        checkCollision();
        updateOpponentCars();
        updateObstacles();
        spawnObstacle();
    }

    //Updates positions of opponent cars.
    //Moves cars forward if not blocked by obstacles
    private void updateOpponentCars() {
        for (OpponentCar car : opponentCars) {
            boolean canMove = true;
            for (Obstacle obstacle : obstacles) {
                if (obstacle instanceof ConstructionSign) {
                    // Check if the car is in the same lane as the construction sign
                    if (car.getLane() == getCurrentLane(obstacle.getX())) {
                        // If the car is behind the construction sign, stop it with some spacing
                        if (car.getYpos() < obstacle.getY() + obstacle.getBounds().height + 50) {  // Add 50 pixels of spacing
                            canMove = false;
                            break;
                        }
                    }
                }
            }
            if (canMove && !playerIsHit) {
                if (score >= conditionalScore && OPPONENT_CAR_SPEED < 45){
                    OPPONENT_CAR_SPEED += 1;
                    conditionalScore += 50;
                }
                car.setYpos((int)car.getYpos() + OPPONENT_CAR_SPEED);
            }
            if (car.getYpos() > 700) {
                resetOpponentCar(car);
            }
        }

        if (ambulancePresent && ambulanceCar != null && !playerIsHit) {
            ambulanceCar.setYpos((int)ambulanceCar.getYpos() + OPPONENT_CAR_SPEED);
            if (ambulanceCar.getYpos() > 700) {
                ambulancePresent = false;
            }
        }
    }

    private void updateObstacles() {
        for (Obstacle obstacle : obstacles) {
            obstacle.setY(obstacle.getY() +OBSTACLE_SPEED); //just use new speed constant
        }
        obstacles.removeIf(obstacle -> obstacle.getY() > 700);
    }


    private void spawnObstacle() {
        if (random.nextInt(100) < OBSTACLE_SPAWN_CHANCE) {
            int lane = random.nextInt(NUM_LANES);
            int y = -100 - random.nextInt(100);  // Add random offset to y-position

            // Rare chance to spawn a construction sign
            if (random.nextInt(CONSTRUCTION_SIGN_RARITY) == 0) {
                // Check if there's already a construction sign in this lane
                boolean constructionSignExists = obstacles.stream()
                        .filter(o -> o instanceof ConstructionSign)
                        .anyMatch(o -> getCurrentLane(o.getX()) == lane);

                if (!constructionSignExists) {
                    // Check for nearby obstacles and adjust y-position if necessary
                    while (isObstacleNearby(lane, y)) {
                        y -= 100;  // Move the obstacle up if there's another nearby
                    }
                    obstacles.add(new ConstructionSign(LANE_POSITIONS[lane], y));
                }
            }
        }
    }

    //Checks if there's an obstacle nearby in the same lane.
    //Y coordinate to check

    private boolean isObstacleNearby(int lane, int y) {
        for (Obstacle obstacle : obstacles) {
            if (getCurrentLane(obstacle.getX()) == lane && Math.abs(obstacle.getY() - y) < 200) {
                return true;
            }
        }
        return false;
    }

    //check collision my car with other opponent cars and obstacles
    private void checkCollision() {
        Rectangle playerBounds = new Rectangle(
                (int)playerCar.getXpos() - playerCar.getCarImage().getIconWidth() / 2,
                (int)playerCar.getYpos(),
                playerCar.getCarImage().getIconWidth(),
                playerCar.getCarImage().getIconHeight()
        );

        for (OpponentCar car : opponentCars) {
            Rectangle opponentBounds = new Rectangle(
                    (int)car.getXpos() - car.getCarImage().getIconWidth() / 2,
                    (int)car.getYpos(),
                    car.getCarImage().getIconWidth(),
                    car.getCarImage().getIconHeight()
            );
            if (playerBounds.intersects(opponentBounds)) {
                gameover = true;
                playerIsHit = true;
                break;
            }
        }

        if (ambulancePresent && ambulanceCar != null) {
            Rectangle ambulanceBounds = new Rectangle(
                    (int)ambulanceCar.getXpos() - ambulanceCar.getCarImage().getIconWidth() / 2,
                    (int)ambulanceCar.getYpos(),
                    ambulanceCar.getCarImage().getIconWidth(),
                    ambulanceCar.getCarImage().getIconHeight()
            );
            if (playerBounds.intersects(ambulanceBounds)) {
                gameover = true;
                playerIsHit = true;
            }
        }

        for (Obstacle obstacle : obstacles) {
            if (playerBounds.intersects(obstacle.getBounds())) {
                gameover = true;
                playerIsHit = true;
                break;
            }
        }

        if (gameover) {
            HighScore.updateHighScores(score);
            // Update the main menu's high scores
            carGame.getMainMenuPanel().updateHighScores();
            carGame.getSettingPanel().stopCarEngineSound();
            if (carGame.getSettingPanel().isSoundOn()  && visibleInGame) {
                carGame.getSettingPanel().playCarCrashSound();
            }
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameover) {
            int currentLane = getCurrentLane((int)playerCar.getXpos());
            if (e.getKeyCode() == KeyEvent.VK_LEFT && currentLane > 0) {
                playerCar.setXpos(LANE_POSITIONS[currentLane - 1]);
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && currentLane < NUM_LANES - 1) {
                playerCar.setXpos(LANE_POSITIONS[currentLane + 1]);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            initializeGame();
            carGame.getSettingPanel().playCarEngineSound();
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            carGame.getSettingPanel().stopCarEngineSound();
            setVisibleInGame(false);
            carGame.getCardLayout().show(carGame.getCardPanel(), "menu");
            setFocusable(false);
            carGame.setFocusable(true);
            carGame.requestFocusInWindow();
        }
    }

    private int getCurrentLane(int xpos) {
        for (int i = 0; i < LANE_POSITIONS.length; i++) {
            if (xpos == LANE_POSITIONS[i]) {
                return i;
            }
        }
        return -1; // Should never happen
    }


    public boolean isVisibleInGame() {
        return visibleInGame;
    }


    public void setVisibleInGame(boolean visible) {
        this.visibleInGame = visible;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameover) {
            updateGameState();
        }
        repaint();
    }
}