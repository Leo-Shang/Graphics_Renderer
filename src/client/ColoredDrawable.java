package client;

import windowing.drawable.Drawable;
import windowing.drawable.DrawableDecorator;

/**
 * Created by Leo on 2017-09-24.
 */
public class ColoredDrawable extends DrawableDecorator {
    //private final Drawable panel;
    private static int color;

    public ColoredDrawable(Drawable panel, int color){
        super(panel);
        //this.panel = panel;
    }

    @Override
    public void fill(int newColor, double z){
        for(int y = 0; y < getHeight(); y++)
            for(int x = 0; x < getWidth(); x++){
                setPixel(x, y, z, color);
            }
    }
}
