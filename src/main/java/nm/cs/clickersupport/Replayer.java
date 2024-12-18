package nm.cs.clickersupport;

import java.util.ArrayList;
import java.util.List;

public class Replayer {
    private Recorder recorder;

    public Replayer(Recorder recorder){
        this.recorder = recorder;
    }

    public void startReplaying(){
        List<RecordedClick> recordedClickList = new ArrayList<>(recorder.getRecordedClicks());


    }

    public void stopReplaying(){

    }
}
