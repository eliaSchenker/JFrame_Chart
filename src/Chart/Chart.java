package Chart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Simple Klasse, um ein Liniendiagramm in einem JPanel anzuzeigen
 */
public class Chart extends JLabel implements MouseListener, MouseMotionListener, MouseWheelListener{

    private List<DataAxis> dataAxes; //Liste mit Datenachsen

    //Allgemeine Variablen für die Darstellung (in Pixel)
    public int xRand;
    public int yRand;

    public float yAxisDisplayStep = 10; //Abstand zwischen den angezeigten Y-Achsenwerten

    public Color backgroundColor = Color.WHITE;

    public long minX; //Minimales X (Zeitpunkt), welches dargestellt werden soll
    public long maxX; //Maximales X (Zeitpunkt), welches dargestellt werden soll
    public float minY; //Minimales Y (Wert), welches dargestellt werden soll
    public float maxY; //Minimales Y (Wert), welches dargestellt werden soll

    //Zoom (wie viel wird von minX und maxX addiert bzw. abgezogen)
    public long minXZoom = 0;
    public long maxXZoom = 0;

    //Letztes Bild, welches gezeichnet wurde
    private BufferedImage lastImage;

    //Variablen für die Legende
    public boolean showLegend = true; //Soll die Legende angezeigt werden?

    /**
     * Standard Konstruktor des Chart.Chart Objektes
     */
    public Chart() {
        dataAxes = new ArrayList<>();
        //Registrieren des MouseListeners und des MouseMotionListeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                renderFrame();
            }
        });
    }


    /**
     * Zeichnet ein Frame auf das Icon des Labels
     */
    public void renderFrame() {

        //Generieren der Grafik für das Label
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        Color mainTextColor = ColorParser.getContrastColor(backgroundColor);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Y-Achsenanzeigewerte berechnen
        float yNumber = minY;
        List<Float> toDisplayValues = new ArrayList<>();
        while(yNumber < maxY) {
            toDisplayValues.add(yNumber);
            yNumber+=yAxisDisplayStep;
        }

        //Zeichnen des Grids
        g.setColor(new Color(202, 202, 202, 204));
        for(Float value : toDisplayValues) {
            Point p = point2DToPoint(pointToPixel(pointToPoint2D(new Point(0, Math.round(value)))));
            g.drawLine(xRand, p.y, getWidth() - xRand, p.y);
        }

        //Hashmap for drawing visible values to the
        TreeMap<Long, Float> visibleValues = new TreeMap<>();

        int axisCounter = 0;
        //Iterieren durch alle DataAxes und DataEntries, um das Diagram zu zeichnen
        for(DataAxis axis : dataAxes) {
            if(axis.isAxisVisible()) {
                List<DataEntry> dataEntries = axis.getEntries();
                g.setColor(axis.getLineColor());
                for (int i = 0; i < dataEntries.size() - 1; i++) {
                    DataEntry d1 = dataEntries.get(i);
                    DataEntry d2 = dataEntries.get(i + 1);
                    Point p1 = point2DToPoint(pointToPixel(new Point2D(d1.getTimestamp(), (long) d1.getValue())));
                    Point p2 = point2DToPoint(pointToPixel(new Point2D(d2.getTimestamp(), (long) d2.getValue())));
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);

                    if (pixelPointInBounds(p1) && axisCounter == 0) {
                        visibleValues.put(d1.getTimestamp(), d1.getValue());
                    }
                }
            }
            axisCounter++;
        }

        //Alles um das Diagramm mit Weiss füllen
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), yRand);
        g.fillRect(0, 0, xRand, getHeight());
        g.fillRect(getWidth() - xRand, yRand, yRand, getHeight());
        g.fillRect(0, getHeight() - yRand, getWidth(), yRand);

        //Zeichnen der Werte um das Diagramm
        //Zeichnen der Y-Werte
        g.setColor(mainTextColor);
        int counter = 0;
        if(toDisplayValues.size() != 0) {
            float step = (getHeight() - 2 * yRand) / (float)toDisplayValues.size();

            for (Float f : toDisplayValues) {
                g.drawString(f.toString(), xRand - g.getFontMetrics().stringWidth(f.toString()) - 2, getHeight() - yRand - (counter * step));
                counter += 1;
            }
        }

        //X-Werte (Timestamps)
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.S");
        g.setFont(new Font("Arial", Font.PLAIN, 10));

        int nextMinimumPosition = -Integer.MAX_VALUE;
        int dateLabelAdditionalSpacer = 50;
        for(long timestamp : visibleValues.keySet()) {

            Timestamp t = new Timestamp(timestamp);
            Point p = point2DToPoint(pointToPixel(new Point2D(timestamp, Math.round(visibleValues.get(timestamp)))));

            String date = dateFormatter.format(t);
            String time = timeFormatter.format(t);
            if(p.x > nextMinimumPosition){
                int textLength = g.getFontMetrics().stringWidth(date);
                g.setColor(Color.black);

                drawString(g,date + "\n" + time, p.x - textLength / 2, getHeight() - yRand);
                nextMinimumPosition = p.x + textLength + dateLabelAdditionalSpacer;
            }
        }

        //Zeichnen der Legende
        if(showLegend) {
            int totalTextLength = 0;
            int additionalLegendTextSpacing = 0;
            int colorIndicatorWidth = 35;
            int colorIndicatorHeight = 15;
            for (DataAxis axis : dataAxes) {
                totalTextLength += g.getFontMetrics().stringWidth(axis.getAxisTitle()) + additionalLegendTextSpacing;
            }

            Point startPoint = new Point(getWidth() / 2 - totalTextLength / 2, 10);
            for (DataAxis axis : dataAxes) {
                g.setColor(mainTextColor);
                g.drawString(axis.getAxisTitle(), startPoint.x, startPoint.y);

                int currentTextWidth = g.getFontMetrics().stringWidth(axis.getAxisTitle());
                Point drawingStartPosition = new Point(startPoint.x + currentTextWidth / 2 -
                        colorIndicatorWidth / 2,
                        startPoint.y + 5);


                g.setColor(axis.getLineColor());
                g.fillRect(drawingStartPosition.x,
                        drawingStartPosition.y,
                        colorIndicatorWidth,
                        colorIndicatorHeight);

                startPoint.x += currentTextWidth + 10;
            }
        }

        //Zeichnen des Randes um das Diagramm
        g.setColor(Color.black);
        if(minY - minX != 0 && maxY - maxX != 0) {
            Point topLeft = point2DToPoint(pointToPixel(new Point2D(minX + minXZoom, (long) minY)));
            Point bottomRight = point2DToPoint(pointToPixel(new Point2D(maxX - maxXZoom, (long) maxY)));

            g.drawLine(topLeft.x, topLeft.y, topLeft.x, bottomRight.y);
            g.drawLine(bottomRight.x, topLeft.y, bottomRight.x, bottomRight.y);
            g.drawLine(topLeft.x, bottomRight.y, bottomRight.x, bottomRight.y);
            g.drawLine(topLeft.x, topLeft.y, bottomRight.x, topLeft.y);
        }


        //Setzen der Grafik auf den Hintergrund des Labels
        lastImage = image;
        setIcon(new ImageIcon(image));
        g.dispose();
    }

    public BufferedImage getRenderedImage() {
        return lastImage;
    }

    /**
     * Methode um Strings mit Zeilenumbrüchen schreiben zu können
     * @param g Das Graphics Objekt
     * @param text Der zu schreibende Text
     * @param x X-Koordinate des Textes
     * @param y Y-Koordinate des Textes
     */
    void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }

    /**
     * Überprüft, ob ein Punkt in dem gewählten Rand ist, oder nicht
     * @param p Punkt (in Pixel)
     * @return Ist der Punkt in dem Round
     */
    private boolean pixelPointInBounds(Point p) {
        return p.x >= xRand &&
                p.x <= getWidth() - xRand &&
                p.y >= yRand &&
                p.y <= getHeight() - yRand;
    }

    /**
     * Berechnet und setzt die minimalen und maximalen Werte
     */
    public void calculateMinMax() {
        minX = Long.MAX_VALUE;
        minY = Float.MAX_VALUE;
        maxX = -Long.MAX_VALUE;
        maxY = -Float.MAX_VALUE;
        for (DataAxis axis : dataAxes) {
            for(DataEntry entry : axis.getEntries()) {
                if(entry.getTimestamp() < minX) {
                    minX = entry.getTimestamp();
                }

                if(entry.getValue() < minY) {
                    minY = entry.getValue();
                }

                if(entry.getTimestamp() > maxX) {
                    maxX = entry.getTimestamp();
                }

                if(entry.getValue() > maxY) {
                    maxY = entry.getValue();
                }
            }
        }
    }

    /**
     * Transformiert ein Punkt auf dem Bild zu einem Punkt in dem Graph
     * @param pixel Der zu transformierende Punkt (in Pixeln)
     * @return Transformierter Punkt auf dem Graph
     */
    public Point2D pixelToPoint(Point2D pixel) {
        //NewValue = (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin
        //Zoom berechnen
        long newMinX = (minX + minXZoom);
        long newMaxX = (maxX - maxXZoom);
        long x = (((pixel.x - xRand) * (newMaxX - newMinX)) / (getWidth() - xRand - xRand)) + newMinX;
        long y = (long) ((((pixel.y - yRand) * (maxY - minY)) / (getWidth() - yRand - yRand)) + minY);

        return new Point2D(x, y);
    }

    /**
     * Transformiert ein Punkt in dem Graph zu einem Punkt auf dem Bild
     * @param point
     * @return
     */
    public Point2D pointToPixel(Point2D point) {

        //Converts one range of number to another, maintaining ratio. Source:
        // https://stackoverflow.com/questions/929103/convert-a-number-range-to-another-range-maintaining-ratio
        //NewValue = (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin
        //Zoom berechnen
        long newMinX = minX + minXZoom;
        long newMaxX = maxX - maxXZoom;
        long x = (((point.x - newMinX) * (getWidth() - xRand - xRand)) / (newMaxX - newMinX)) + xRand;
        long y = (long) ((((point.y - maxY) * (getHeight() - yRand - yRand)) / (minY - maxY)) + yRand);

        return new Point2D(x, y);
    }

    /**
     * Gibt eine Achse mit einem Index zurück
     * @param index Der Index der Achse
     * @return Das Chart.DataAxis Objekt
     */
    public DataAxis getAxis(int index) {
        return dataAxes.get(index);
    }

    /**
     * Fügt eine Achse hinzu
     * @param axis Die Achse
     */
    public void addAxis(DataAxis axis) {
        dataAxes.add(axis);
        calculateMinMax();
        renderFrame();
    }

    /**
     * Löscht eine Achse
     * @param index Der Index der zu löschenden Achse
     */
    public void removeAxis(int index) {
        dataAxes.remove(index);
        calculateMinMax();
        renderFrame();
    }

    //Variablen für die Interaktion mit dem Chart.Chart
    public boolean interactable = true; //Darf mit dem Chart.Chart interagiert wereden
    private boolean isDragging; //Ist der Benutzer am draggen
    private Point startPoint;

    /**
     * Setzt den Zoom zurück
     */
    public void resetZoom() {
        minXZoom = 0;
        maxXZoom = 0;
        renderFrame();
    }

    /**
     * Mouse Click Event
     * @param e Event-Args
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Mouse Pressed Event
     * @param e Event-Args
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if(interactable && !isDragging) {
            isDragging = true;
            startPoint = e.getPoint();
        }
    }

    /**
     * Mouse released event
     * @param e Event-Args
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if(isDragging) {
            isDragging = false;
            setIcon(new ImageIcon(lastImage));
            Point2D leftTopPoint = pixelToPoint(pointToPoint2D(startPoint));
            Point2D bottomRightPoint = pixelToPoint(pointToPoint2D(e.getPoint()));

            if(bottomRightPoint.x - leftTopPoint.x > 1) {
                //Transformieren der selektierten Punkte zu den Anfangswerten
                minXZoom = leftTopPoint.x - minX;
                maxXZoom = maxX - bottomRightPoint.x;
            }
            renderFrame();
        }
    }

    /**
     * Mouse Entered Event
     * @param e Event-Args
     */
    @Override
    public void mouseEntered(MouseEvent e) { }

    /**
     * Mouse Exit Event
     * @param e Event-Args
     */
    @Override
    public void mouseExited(MouseEvent e) { }

    /**
     * Mouse Drag Event
     * @param e Event-Args
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if(isDragging) {
            //Kopieren des letzten Bildes in die dragTempImage Variable
            BufferedImage dragTempImage = new BufferedImage(lastImage.getWidth(), lastImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) dragTempImage.getGraphics();
            g.drawImage(lastImage, 0, 0, null);
            g.setColor(new Color(255, 0, 0, 120));
            g.fillRect(startPoint.x, startPoint.y, e.getPoint().x - startPoint.x, e.getPoint().y - startPoint.y);
            g.dispose();

            setIcon(new ImageIcon(dragTempImage));
        }
    }

    /**
     * Mouse Move Event
     * @param e Event-Args
     */
    @Override
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * Mousewheel moved Event
     * @param e
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        long changeValue = (maxX - minX) / 100;
        if(e.getWheelRotation() > 0) {
            if(minXZoom - changeValue >= 0 && maxXZoom - changeValue >= 0) {
                minXZoom -= changeValue;
                maxXZoom -= changeValue;
            }
        }else {
            if((minX + minXZoom + changeValue < maxX - maxXZoom - changeValue) || minXZoom == 0) {
                minXZoom += changeValue;
                maxXZoom += changeValue;
            }
        }
        renderFrame();
    }

    /**
     * Konvertiert einen Chart.Point2D (interne Rechnung mit Longs
     * anstatt Ints) zu einem Point (Werte mit Ints
     * @param p Der umzuwandelnde Punkt (Point)
     * @return Der umgewandelte Punkt (Chart.Point2D)
     */
    public Point2D pointToPoint2D(Point p) {
        return new Point2D(p.x, p.y);
    }

    /**
     * Konvertiert einen Chart.Point2D (interne Rechnung mit Longs
     * anstatt Ints) zu einem Point (Werte mit Ints
     * @param p Der umzuwandelnde Punkt (Chart.Point2D)
     * @return Der umgewandelte Punkt (Point)
     */
    public Point point2DToPoint(Point2D p) {
        return new Point((int)p.x, (int)p.y);
    }

}





