package Cargame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainMenuPanel extends JPanel implements ActionListener {
    private CarGame carGame;
    private boolean focusState = true;
    private final Image backgroundImage;
    private final Image gameNameImage;
    private IconButton startButton;
    private IconButton settingButton;
    private IconButton exitButton;
    private List<Integer> highScores;

    public MainMenuPanel(CarGame carGame) {
        this.carGame = carGame;
        setLayout(null);
        setFocusable(isFocusState());

        startButton = new IconButton(new ImageIcon("Game/src/assets/playButton.png"));
        startButton.setBounds(125, 250, 250, 70);
        startButton.addActionListener(this);
        add(startButton);

        settingButton = new IconButton(new ImageIcon("Game/src/assets/settingButton.png"));
        settingButton.setBounds(125, 350, 250, 70);
        settingButton.addActionListener(this);
        add(settingButton);

        exitButton = new IconButton(new ImageIcon("Game/src/assets/exitButton.png"));
        exitButton.setBounds(125, 450, 250, 70);
        exitButton.addActionListener(this);
        add(exitButton);

        backgroundImage = new ImageIcon("Game/src/assets/Background.png").getImage();
        gameNameImage = new ImageIcon("Game/src/assets/gameName.png").getImage();

        highScores = HighScore.getTopScores();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(100, 150, 300, 500, 25, 25);

        // Draw the game name image
        int gameNameWidth = gameNameImage.getWidth(this);
        int gameNameHeight = gameNameImage.getHeight(this);
        int gameNameX = startButton.getX() + (startButton.getWidth() - gameNameWidth) / 2;
        int gameNameY = startButton.getY() - gameNameHeight + 15;
        g.drawImage(gameNameImage, gameNameX, gameNameY, gameNameWidth, gameNameHeight, this);

        drawHighScores(g);
    }

    private void drawHighScores(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.setFont(new Font("Math Sans", Font.BOLD, 20));
        int y = getHeight() - 100;
        g.drawString("Highest Scores are: ", getWidth() - 220, y);
        y += 25;
        for (int i = 0; i < highScores.size(); i++) {
            String scoreText = (i + 1) + ". " + highScores.get(i) + " points";
            g.drawString(scoreText, getWidth() - 180, y);
            y += 25;
        }
    }

    public boolean isFocusState() {
        return focusState;
    }

    public void setFocusState(boolean focusState) {
        this.focusState = focusState;
    }

    //click buttons to deliver start game, setting and exit
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            carGame.getCardLayout().show(carGame.getCardPanel(), "game");
            setFocusable(false);
            carGame.getGamePanel().setFocusable(true);
            carGame.getGamePanel().initializeGame();
            if (carGame.getSettingPanel().isSoundOn()) {
                carGame.getSettingPanel().playCarEngineSound();
                carGame.getGamePanel().setVisibleInGame(true);
            }
            carGame.getGamePanel().requestFocusInWindow();
        } else if (e.getSource() == settingButton) {
            carGame.getCardLayout().show(carGame.getCardPanel(), "setting");
            setFocusable(false);
            carGame.getSettingPanel().setFocusable(true);
            carGame.getSettingPanel().requestFocusInWindow();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    //Display high score and fetch latest high scores
    public void updateHighScores() {
        highScores = HighScore.getTopScores();
        repaint();
    }
}
