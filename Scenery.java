package Cargame;

import javax.swing.ImageIcon;

public class Scenery {
    private int xpos;
    private int ypos;
    private ImageIcon sceneryImage;
    private String type;

    public Scenery(int xpos, int ypos, String imagePath, String type) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.sceneryImage = new ImageIcon(imagePath);
        this.type = type;
    }

    // Getters and setters
    public int getXpos() { return xpos; }
    public void setXpos(int xpos) { this.xpos = xpos; }
    public int getYpos() { return ypos; }
    public void setYpos(int ypos) { this.ypos = ypos; }
    public ImageIcon getSceneryImage() { return sceneryImage; }
    public String getType() { return type; }
}