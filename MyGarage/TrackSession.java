package MyGarage;

import java.io.Serializable;
import java.time.LocalDate;

public class TrackSession implements Serializable {
    
    private String trackName;
    private String lapTime;     // ex: 2:15.450
    private LocalDate date;
    private String conditions;  // ex: Dry, Wet
    private String tires;       // ex: Cup 2

    public TrackSession(String trackName, String lapTime, LocalDate date, String conditions, String tires) {
        this.trackName = trackName;
        this.lapTime = lapTime;
        this.date = date;
        this.conditions = conditions;
        this.tires = tires;
    }

    // Getter Methods
    public String getTrackName() { return trackName; }
    public String getLapTime() { return lapTime; }
    public LocalDate getDate() { return date; }
    public String getConditions() { return conditions; }
    public String getTires() { return tires; }
}
