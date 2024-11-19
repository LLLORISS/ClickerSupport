package nm.cs.clickersupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
    private double interval;
    private int clickCount;

    @JsonCreator
    public Config(@JsonProperty("interval") double interval, @JsonProperty("clickCount") int clickCount) {
        this.interval = interval;
        this.clickCount = clickCount;
    }

    public int getClickCount() {
        return this.clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public double getInterval() {
        return this.interval;
    }

    public void setInterval(double interval) {
        this.interval = interval;
    }
}
