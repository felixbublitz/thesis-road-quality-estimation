package de.felixbublitz.simra_rq;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class RectPainter implements Painter<JXMapViewer> {

    private Color color = Color.GRAY;


    public RectPainter(Color color)
    {
        this.color = color;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h)
    {
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
       // g.translate(-rect.x, -rect.y);


        // do the drawing again
        g.setColor(color);
        g.fillRect(0,0,w,h);


        g.dispose();
    }



}
