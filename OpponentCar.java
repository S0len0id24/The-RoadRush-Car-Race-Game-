package Cargame;

public class OpponentCar extends Car {
    private int lane;

    public OpponentCar(int xpos, int ypos, String imagePath, int lane) {
        super(xpos, ypos, imagePath);
        this.lane = lane;
    }

    public int getLane() { return lane; }
    public void setLane(int lane) { this.lane = lane; }
}