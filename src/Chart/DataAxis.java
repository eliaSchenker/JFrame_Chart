package Chart;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse einer Achse auf einem Diagramm
 */
public class DataAxis {
    private List<DataEntry> dataEntries; //Daten der Achse
    private Color lineColor; //Farbe der Achse
    private String axisTitle;
    private boolean axisVisible;

    /**
     * Standard-Konstruktor der Klasse Chart.DataAxis
     * @param lineColor Farbe der Achse
     */
    public DataAxis(Color lineColor, String axisTitle) {
        this.dataEntries = new ArrayList<>();
        this.lineColor = lineColor;
        this.axisTitle = axisTitle;
        this.axisVisible = true;
    }

    /**
     * Gibt die Liste mit Entries zurück
     * @return Liste mit Entries
     */
    public List<DataEntry> getEntries() {
        return dataEntries;
    }

    /**
     * Gibt ein Entry von einem Index zurück
     * @param index Index des Entries
     * @return Der Entry
     */
    public DataEntry getEntry(int index) {
        return dataEntries.get(index);
    }

    /**
     * Fügt einen Entry hinzu
     * @param entry Der hinzuzufügende Entry
     */
    public void addEntry(DataEntry entry) {
        dataEntries.add(entry);
    }

    /**
     * Löscht einen Entry von einem Index
     * @param index Index des Entries
     */
    public void removeEntry(int index) {
        dataEntries.remove(index);
    }

    /**
     * Gibt die Farbe der Achse zurück
     * @return Farbe der Achse
     */
    public Color getLineColor() {
        return lineColor;
    }

    /**
     * Setzt die Farbe der Achse
     * @param lineColor Die zu setztende Farbe
     */
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public String getAxisTitle() {
        return axisTitle;
    }

    public void setAxisTitle(String axisTitle) {
        this.axisTitle = axisTitle;
    }

    public boolean isAxisVisible() {
        return axisVisible;
    }

    public void setAxisVisible(boolean axisVisible) {
        this.axisVisible = axisVisible;
    }

    public List<DataEntry> getDataEntries() {
        return dataEntries;
    }

    public void setDataEntries(List<DataEntry> dataEntries) {
        this.dataEntries = dataEntries;
    }
}