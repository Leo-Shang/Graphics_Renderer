package windowing.drawable;

import client.interpreter.SimpInterpreter;

public class PixelClippingDrawable extends DrawableDecorator {

    private double low_X, high_X, low_Y, high_Y, hither, yon;

    public PixelClippingDrawable(Drawable delegate, double low_X, double high_X, double low_Y, double high_Y, double hither, double yon) {
        super(delegate);

        this.low_X = low_X;
        this.high_X = high_X;
        this.low_Y = low_Y;
        this.high_Y = high_Y;
        this.hither = hither;
        this.yon = yon;
    }

    private Boolean proceed(double x, double y, double z){
        low_X = (delegate.getWidth() - SimpInterpreter.getWidth()) / 2;
        high_X = delegate.getWidth() - low_X;
        low_Y = (delegate.getHeight() - SimpInterpreter.getHeight()) / 2;
        high_Y = delegate.getHeight() - low_Y;
        return (x >= this.low_X && x < this.high_X) &&
                (y >= this.low_Y && y < this.high_Y);
    }

    @Override
    public void setPixel(int x, int y, double z, int argbColor) {
        if (proceed(x, y, z)) {delegate.setPixel(x, y, z, argbColor);}
    }

    @Override
    public void setPixelWithCoverage(int x, int y, double z, int argbColor, double coverage) {
        if (proceed(x, y, z)) {delegate.setPixelWithCoverage(x,  y, z, argbColor, coverage);}
    }
}
