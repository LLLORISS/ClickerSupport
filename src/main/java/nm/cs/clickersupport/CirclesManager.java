package nm.cs.clickersupport;

import java.io.IOException;
import java.util.ArrayList;

public class CirclesManager {
    private ArrayList<ClickerCircle> circles = new ArrayList();

    public CirclesManager(){

    }

    public boolean addCircle(ClickerCircle circle){
        return circles.add(circle);
    }

    public void removeCircle(){
        circles.removeLast();
    }

    public void updateCirclesNumbers(){
        for(int i = 0; i < circles.size(); i++){
            circles.get(i).setText(Integer.toString(i + 1));
        }
    }

    public int getCount(){
        return circles.size();
    }
}
