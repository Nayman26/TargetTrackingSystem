import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class AnimatedPanel extends JPanel {

    private int xPos = 50;  // Initial x-coordinate for animation
    private int yPos = 300; // Initial y-coordinate for animation
    private final int radarX = 100; // Initial x-coordinate for radar
    private final int radarY = 200; // Initial y-coordinate for radar
    private final int cameraX = 250; // Initial x-coordinate for camera
    private final int cameraY = 200; // Initial y-coordinate for camera
    private double cameraAngle = Math.PI;
    private KafkaConsumerHandler kafkaConsumerHandler;
    public static Timer timer;

    public AnimatedPanel(KafkaConsumerHandler kafkaConsumerHandler) {
        this.kafkaConsumerHandler = kafkaConsumerHandler;

        HashMap<String, String> towerPositions = new HashMap<>();
        towerPositions.put("camera", cameraX+","+cameraY);
        towerPositions.put("radar", radarX+","+radarY);
        Producer producer = new Producer();
        producer.produceTower(towerPositions);

        // Set the timer interval to 1000 milliseconds (1 second)
        timer = new Timer(1000, e -> {
            kafkaConsumerHandler.consumeKafkaValues(AnimatedPanel.this);

            xPos += 5;  // Adjust the movement speed as needed
            producer.produceTarget(xPos+","+yPos);
            //yPos += 2;  // Adjust the Y movement speed as needed

            // Ensure that the x-coordinate stays within the panel bounds
            if (xPos > getWidth()) {
                xPos = 0;
            }

            // Ensure that the y-coordinate stays within the panel bounds
            if (yPos > getHeight()) {
                yPos = 0;
            }

            // Trigger a repaint to update the display
            repaint();
        });
    }

    public void setCameraAngle(double cameraAngle) {
        this.cameraAngle = cameraAngle;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(0, getHeight());
        g2d.scale(1, -1);

        int angleLineLength=75;
        int height=20;
        int width=20;

        int endX = (int) (cameraX+angleLineLength*Math.cos(cameraAngle));
        int endY = (int) (cameraY+angleLineLength*Math.sin(cameraAngle));

        g.drawLine(cameraX,cameraY+height,endX,endY+height); // y + height for drawing from left-top
        g.drawLine(cameraX,cameraY+height,endX,cameraY+height); // y + height for drawing from left-top

        // Draw the animated object at the current position
        g.setColor(Color.BLUE);
        g.fillRect(xPos, yPos, width, height);

        // Draw the radar object
        g.setColor(Color.RED);
        g.fillRect(radarX, radarY, 20, 20);

        // Draw the camera object
        g.setColor(Color.GREEN);
        g.fillRect(cameraX, cameraY, width, height);
    }
}
