package nm.cs.clickersupport;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.awt.*;

public class ClickerCircle {
    private Circle circle;
    private Text text;
    private Color color;
    private int radius;

    public ClickerCircle(String textContent, Color color, int radius){
        this.color = color;
        this.radius = radius;

        this.circle = new Circle(radius);
        this.circle.setPickOnBounds(false);
        this.circle.setFill(color);

        this.text = new Text(textContent);
        this.text.setFill(Color.WHITE);
        this.text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        centerText();
        this.text.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> centerText());
    }

    public void centerText() {
        double textWidth = text.getBoundsInLocal().getWidth();
        double textHeight = text.getBoundsInLocal().getHeight();
        text.setX(circle.getCenterX() - textWidth / 2);
        text.setY(circle.getCenterY() + textHeight / 4);
    }

    public void setText(String newText) {
        text.setText(newText);
        centerText();
    }

    public Circle getCircle() {
        return circle;
    }

    public Text getText() {
        return text;
    }

    public Point getCoords() {
        Bounds bounds = this.circle.localToScreen(this.circle.getBoundsInLocal());
        int x = (int) (bounds.getMinX() + this.circle.getRadius());
        int y = (int) (bounds.getMinY() + this.circle.getRadius());

        return new Point(x, y);
    }


    public void addToPane(Pane pane) {
        pane.getChildren().add(circle);
        pane.getChildren().add(text);
    }


}
