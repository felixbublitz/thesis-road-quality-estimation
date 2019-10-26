package de.felixbublitz.simra_rq.map.painter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;

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
        Rectangle rect = map.getViewportBounds();
        g.setColor(color);
        g.fillRect(0,0,w,h);
        g.dispose();
    }



}
