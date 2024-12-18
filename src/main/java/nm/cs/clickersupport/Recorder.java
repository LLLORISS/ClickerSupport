package nm.cs.clickersupport;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Recorder implements NativeMouseListener {
    private List<RecordedClick> recordedClicks = new ArrayList<>();
    private long lastClickTime = 0;
    private boolean isRecording = false;
    private boolean isMousePressed = false;

    private static final Logger logger = Logger.getLogger(Recorder.class.getName());

    public void startRecording() {
        if (isRecording) {
            logger.warning("[ClickerSupport] Recording is already started.");
            return;
        }
        recordedClicks.clear();
        lastClickTime = System.currentTimeMillis();
        isRecording = true;
        System.out.println("[ClickerSupport] Recording started...");
    }

    public void stopRecording() {
        if (!isRecording) {
            System.out.println("[ClickerSupport] No recording in progress.");
            return;
        }
        isRecording = false;
        System.out.println("[ClickerSupport] Recording stopped. Total clicks recorded: " + recordedClicks.size());
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        if (!isRecording) {
            return;
        }
        isMousePressed = true;
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        if (!isRecording || !isMousePressed) {
            return;
        }

        Point point = MouseInfo.getPointerInfo().getLocation();
        long currentTime = System.currentTimeMillis();
        long delay = currentTime - lastClickTime;

        recordedClicks.add(new RecordedClick(point, delay));
        lastClickTime = currentTime;

        isMousePressed = false;

        System.out.println("[ClickerSupport] Recorded click at: " + point + " with delay: " + delay + " ms");
    }

    public List<RecordedClick> getRecordedClicks() {
        return recordedClicks;
    }
}
