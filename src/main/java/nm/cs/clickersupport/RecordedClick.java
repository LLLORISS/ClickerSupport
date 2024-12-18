package nm.cs.clickersupport;

import java.awt.*;

public class RecordedClick {
    public final Point point;
    public final long delay;

    public RecordedClick(Point point, long delay){
        this.point = point;
        this.delay = delay;
    }

}
