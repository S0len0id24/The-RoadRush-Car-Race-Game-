package Cargame;

import javax.swing.ImageIcon;

public class Car {
    private double xpos;
    private double ypos;
    private ImageIcon carImage;

    public Car(double xpos, double ypos, String imagePath) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.carImage = new ImageIcon(imagePath);
    }

    // Getters and setters
    public double getXpos() { return  xpos; }
    public void setXpos(int xpos) { this.xpos = xpos; }
    public double getYpos() { return  ypos; }
    public void setYpos(int ypos) { this.ypos = ypos; }
    public ImageIcon getCarImage() { return carImage; }

}