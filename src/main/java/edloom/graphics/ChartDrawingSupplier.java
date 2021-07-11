package edloom.graphics;

import org.jfree.chart.plot.DefaultDrawingSupplier;

import java.awt.*;

public class ChartDrawingSupplier extends DefaultDrawingSupplier {

    public Paint[] paintSequence;
    public int paintIndex;
    public int fillPaintIndex;

    {
        paintSequence = new Paint[]{
                Color.GREEN,
                Color.RED,
                Color.GRAY,
                Color.BLUE
        };
    }

    @Override
    public Paint getNextPaint() {
        Paint result
                = paintSequence[paintIndex % paintSequence.length];
        paintIndex++;
        return result;
    }


    @Override
    public Paint getNextFillPaint() {
        Paint result
                = paintSequence[fillPaintIndex % paintSequence.length];
        fillPaintIndex++;
        return result;
    }
}