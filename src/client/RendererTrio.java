package client;

import line.ColoredDDAZBuff;
import line.LineRenderer;
import polygon.ColoredpolygonRenderer;
import polygon.PolygonRenderer;
import polygon.WireframeRenderer;
import polygon.WireframeRendererZBuff;

/**
 * Created by Leo on 10/11/2017.
 */
public class RendererTrio {
    private LineRenderer lineRenderer = ColoredDDAZBuff.make();
    private PolygonRenderer filledRenderer = ColoredpolygonRenderer.make();
    private PolygonRenderer wireFrameRenderer = WireframeRendererZBuff.make();

    public LineRenderer getLineRenderer(){
        return lineRenderer;
    }

    public PolygonRenderer getFilledRenderer(){
        return filledRenderer;
    }

    public PolygonRenderer getWireframeRenderer(){
        return wireFrameRenderer;
    }
}
