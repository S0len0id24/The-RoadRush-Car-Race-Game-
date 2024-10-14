package Cargame;

import javax.swing.*;

class IconButton extends JButton {
    public IconButton(Icon icon) {
        super(icon);

        setBorderPainted(false);  // Remove the border
        setFocusPainted(false);  // Remove the focus outline
        setContentAreaFilled(false);  // Remove background fill
    }
}
