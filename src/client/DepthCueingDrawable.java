package client;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.drawable.DrawableDecorator;
import windowing.graphics.Color;

public class DepthCueingDrawable implements Drawable{
    private double color;
    private int z_max;
    private int z_min;
    private Drawable panel;

    public DepthCueingDrawable(Drawable panel, int z_max, int z_min, Color color){
        this.z_min = z_min;
        this.z_max = z_max;
        this.color = color.asARGB();
        this.panel = panel;
    }

    @Override
    public void fill(int color, double z){
        for(int i = 0; i < panel.getWidth(); i++){
            for(int j = 0; j < panel.getHeight(); j++){
                panel.setPixel(i, j, z, color);
            }
        }
    }

    @Override
    public void setPixel(int x, int y, double z, int argbColor) {
        panel.setPixel(x, y, z, argbColor);
    }

    @Override
    public int getPixel(int x, int y) {
        return (int)Math.round(color);
    }

    @Override
    public double getZValue(int x, int y) {
        return panel.getZValue(x, y);
    }

    @Override
    public int getWidth() {
        return panel.getWidth();
    }

    @Override
    public int getHeight() {
        return panel.getHeight();
    }
}
