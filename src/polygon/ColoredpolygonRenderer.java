package polygon;

import client.interpreter.SimpInterpreter;
import geometry.Vertex3D;
import line.Interpolate;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class ColoredpolygonRenderer implements PolygonRenderer {

    private enum SIDE {LEFT, RIGHT, BOTH, NEITHER}
    public static PolygonRenderer make(){return new ColoredpolygonRenderer();}

    @Override
    public void drawPolygon(Polygon polygon, Drawable panel, Shader vertexShader) {
        Chain leftChain = polygon.leftChain();
        Chain rightChain = polygon.rightChain();

        int leftChainLastIndex = leftChain.length() - 1;
        int rightChainLastIndex = rightChain.length() - 1;

        int p1LeftIndex = 0;
        int p1RightIndex = 0;
        int p2LeftIndex = 1;
        int p2RightIndex = 1;
        SIDE side = SIDE.NEITHER;

        while (p2LeftIndex < leftChainLastIndex || p2RightIndex < rightChainLastIndex){
            if ((side == SIDE.BOTH || side == SIDE.LEFT) && p2LeftIndex < leftChainLastIndex){
                p1LeftIndex++;
                p2LeftIndex++;
            }
            if ((side == SIDE.BOTH || side == SIDE.RIGHT) && p2RightIndex < rightChainLastIndex){
                p1RightIndex++;
                p2RightIndex++;
            }

            side = drawTriangle(panel,
                    leftChain.get(p1LeftIndex), leftChain.get(p2LeftIndex),
                    rightChain.get(p1RightIndex), rightChain.get(p2RightIndex));
        }

    }

    private SIDE drawTriangle(Drawable panel, Vertex3D p1L, Vertex3D p2L, Vertex3D p1R, Vertex3D p2R){
//        System.out.println(p1L);
//        System.out.println(p2L);
//        System.out.println(p1R);
//        System.out.println(p2R);
        double startY = Math.min(p1L.getY(), p1R.getY());
        double endY = Math.max(p2L.getY(), p2R.getY());

        Interpolate xLeft = new Interpolate(p1L.getY(), p2L.getY(), startY, p1L.getX(), p2L.getX(), true);
        Interpolate xRight = new Interpolate(p1R.getY(), p2R.getY(), startY, p1R.getX(), p2R.getX(), true);

        Interpolate zLeft = new Interpolate(p1L.getY(), p2L.getY(), startY, 1 / p1L.getZ(), 1 / p2L.getZ(), true);
        Interpolate zRight = new Interpolate(p1R.getY(), p2R.getY(), startY, 1 / p1R.getZ(), 1 / p2R.getZ(), true);

        Interpolate redLeft = new Interpolate(p1L.getY(), p2L.getY(), startY, p1L.getColor().getR() / p1L.getZ(), p2L.getColor().getR() / p2L.getZ(), true);
        Interpolate redRight = new Interpolate(p1R.getY(), p2R.getY(), startY, p1R.getColor().getR() / p1R.getZ(), p2R.getColor().getR() / p2R.getZ(), true);

        Interpolate greenLeft = new Interpolate(p1L.getY(), p2L.getY(), startY, p1L.getColor().getG() / p1L.getZ(), p2L.getColor().getG() / p2L.getZ(), true);
        Interpolate greenRight = new Interpolate(p1R.getY(), p2R.getY(), startY, p1R.getColor().getG() / p1R.getZ(), p2R.getColor().getG() / p2R.getZ(), true);

        Interpolate blueLeft = new Interpolate(p1L.getY(), p2L.getY(), startY, p1L.getColor().getB() / p1L.getZ(), p2L.getColor().getB() / p2L.getZ(), true);
        Interpolate blueRight = new Interpolate(p1R.getY(), p2R.getY(), startY, p1R.getColor().getB() / p1R.getZ(), p2R.getColor().getB() / p2R.getZ(), true);

        for (double y = startY; y >= endY; y--){

            Interpolate innerZ = new Interpolate(xLeft.getValue(), xRight.getValue(), xLeft.getValue(), zLeft.getValue(), zRight.getValue());
            Interpolate innerRed = new Interpolate(xLeft.getValue(), xRight.getValue(), xLeft.getValue(), redLeft.getValue(), redRight.getValue());
            Interpolate innerGreen = new Interpolate(xLeft.getValue(), xRight.getValue(), xLeft.getValue(), greenLeft.getValue(), greenRight.getValue());
            Interpolate innerBlue = new Interpolate(xLeft.getValue(), xRight.getValue(), xLeft.getValue(), blueLeft.getValue(), blueRight.getValue());

            for (double x = xLeft.getValue(); x < xRight.getValue(); x++){
                double realZ = 1 / innerZ.getValue();
                double realRed = realZ * innerRed.getValue();
                double realGreen = realZ * innerGreen.getValue();
                double realBlue = realZ * innerBlue.getValue();

                Color color = new Color(realRed, realGreen, realBlue);
                if(y >= 0 && y < 650 && x < 650 && x >= 0 && realZ >= SimpInterpreter.getYon() && realZ <= SimpInterpreter.getHeight()) {
                    if (SimpInterpreter.z_buffer.getValue((int) x, (int) y) < realZ) {
                        panel.setPixel((int) x, (int) y, realZ, color.asARGB());
                        SimpInterpreter.z_buffer.changeValue((int) x, (int) y, realZ);
                    }
                }
                innerZ.add();
                innerRed.add();
                innerGreen.add();
                innerBlue.add();
            }

            xLeft.add(); xRight.add();
            zLeft.add(); zRight.add();
            redLeft.add(); redRight.add();
            greenLeft.add(); greenRight.add();
            blueLeft.add(); blueRight.add();
        }

        if (p2L.getY() == endY){
            if (p2R.getY() == endY){return SIDE.BOTH;}
            else {return SIDE.LEFT;}
        } else {
            return SIDE.RIGHT;
        }
    }
}