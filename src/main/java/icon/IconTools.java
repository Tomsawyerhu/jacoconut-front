package icon;


import javax.swing.*;
import java.awt.*;

public class IconTools {
    public static Icon getIconWithFixedSize(int width,int height,String path){
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return new ImageIcon( scaledImage );
    }
}
