package Cargame;

import javax.swing.*;
import java.awt.*;

public abstract class Obstacle {
    protected int x, y;
    protected Image image;

    public Obstacle(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.image = new ImageIcon(imagePath).getImage();
    }

    public void draw(Graphics g, JComponent c) {
        g.drawImage(image, x - image.getWidth(c) / 2, y, c);
    }

    public Rectangle getBounds() {
        return new Rectangle(x - image.getWidth(null) / 2, y, image.getWidth(null), image.getHeight(null));
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}