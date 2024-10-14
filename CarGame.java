package Cargame;

import javax.swing.*;
import java.awt.*;

//Main Frame (Just extends JFrame)

public class CarGame extends JFrame {
    private GamePanel gamePanel;
    private MainMenuPanel menuPanel;
    private SettingPanel settingPanel;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    //Constructor for CarGame.
    //Sets up the main game window, Initializes game panels (menu, settings, game)
    //Starts background music

    public CarGame(String title) {
        super(title);
        setBounds(300, 10, 800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cardPanel.getLayout();

        menuPanel = new MainMenuPanel(this);
        settingPanel = new SettingPanel(this);
        gamePanel = new GamePanel(this, 800,800);

        cardPanel.add(menuPanel, "menu");
        cardPanel.add(gamePanel, "game");
        cardPanel.add(settingPanel, "setting");

        add(cardPanel);

        setVisible(true);

        // Start playing background music
        settingPanel.playBackgroundMusic();
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public SettingPanel getSettingPanel() {
        return settingPanel;
    }

    public MainMenuPanel getMainMenuPanel() {
        return menuPanel;
    }
}
