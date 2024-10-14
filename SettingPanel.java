package Cargame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SettingPanel extends JPanel implements ActionListener, KeyListener {
    private CarGame carGame;
    private IconButton musicButton;
    private IconButton soundButton;
    private boolean musicOn = true;
    private boolean soundOn = true;
    private final Image backgroundImage;

    // Sound files
    private File backgroundMusicFile;
    private File carCrashSoundFile;
    private File carEngineSoundFile;

    // Clips for sound playback
    private Clip backgroundMusicClip;
    private Clip carCrashClip;
    private Clip carEngineClip;

    public SettingPanel(CarGame carGame) {
        this.carGame = carGame;
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon("Game/src/assets/Background.png").getImage();

        musicButton = new IconButton(new ImageIcon("Game/src/assets/musicOn.png"));
        musicButton.setBounds(200, 325, 160, 150);
        musicButton.addActionListener(this);
        add(musicButton);

        soundButton = new IconButton(new ImageIcon("Game/src/assets/soundOn.png"));
        soundButton.setBounds(450, 325, 160, 150);
        soundButton.addActionListener(this);
        add(soundButton);

        // Initialize sound files
        backgroundMusicFile = new File("Game/src/assets/background_music.wav");
        carCrashSoundFile = new File("Game/src/assets/car_crash.wav");
        carEngineSoundFile = new File("Game/src/assets/car_engine.wav");

        // Load sound clips
        loadSoundClips();

        // Start playing background music immediately
        playBackgroundMusic();
    }

    // Loads sound clips for background music, car crash, and car engine.
    private void loadSoundClips() {
        try {
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(AudioSystem.getAudioInputStream(backgroundMusicFile));

            carCrashClip = AudioSystem.getClip();
            carCrashClip.open(AudioSystem.getAudioInputStream(carCrashSoundFile));

            carEngineClip = AudioSystem.getClip();
            carEngineClip.open(AudioSystem.getAudioInputStream(carEngineSoundFile));
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void playBackgroundMusic() {
        if (musicOn && backgroundMusicClip != null) {
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.stop();
        }
    }

    public void playCarCrashSound() {
        if (soundOn && carCrashClip != null) {
            carCrashClip.stop();
            carCrashClip.setFramePosition(0);
            carCrashClip.start();
        }
    }

    public void playCarEngineSound() {
        if (soundOn && carEngineClip != null) {
            carEngineClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopCarEngineSound() {
        if (carEngineClip != null) {
            carEngineClip.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(100, 200, 600, 400, 25, 25);
    }

    //Handles button click events.
    //Toggles music on/off when Music button is clicked
    //Toggles sound effects on/off when Sound button is clicked

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == musicButton) {
            musicOn = !musicOn;
            musicButton.setIcon(new ImageIcon(musicOn ? "Game/src/assets/musicOn.png" : "Game/src/assets/musicOff.png"));
            requestFocusInWindow();

            if (musicOn) {
                playBackgroundMusic();
            } else {
                stopBackgroundMusic();
            }

        } else if (e.getSource() == soundButton) {
            soundOn = !soundOn;
            soundButton.setIcon(new ImageIcon(soundOn ? "Game/src/assets/soundOn.png" : "Game/src/assets/soundOff.png"));
            requestFocusInWindow();

            if (!soundOn) {
                stopCarEngineSound();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            carGame.getCardLayout().show(carGame.getCardPanel(), "menu");
            carGame.requestFocusInWindow();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            requestFocusInWindow();
        }
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public boolean isSoundOn() {
        return soundOn;
    }
}