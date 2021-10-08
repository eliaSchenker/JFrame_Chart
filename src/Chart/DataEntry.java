package Chart;

/**
 * Objekt, welches ein Eintrag der Daten enthält
 */
public class DataEntry {
    private long timestamp; //Zeitpunkt als Timestamp (Millisekunden seit 1970)
    private float value; //Wert an diesem Zeitpunkt

    /**
     * Standard-Konstruktor der Chart.DataEntry Klasse
     * @param timestamp Zeitpunkt
     * @param value Wert
     */
    public DataEntry(long timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    /**
     * Gibt den Zeitpunkt zurück
     * @return Der Zeitpunkt
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Setzt den Zeitpunkt
     * @param timestamp Der zu setztende Zeitpunkt
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gibt den Wert zurück
     * @return Wert als float
     */
    public float getValue() {
        return value;
    }

    /**
     * Setzt den Wert
     * @param value Wert
     */
    public void setValue(float value) {
        this.value = value;
    }
}