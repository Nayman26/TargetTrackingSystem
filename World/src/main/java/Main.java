import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Animated Panel Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        KafkaConsumerHandler kafkaConsumerHandler = new KafkaConsumerHandler("127.0.0.1:9092", "consumer-group", "CameraLosStatus");
        AnimatedPanel animatedPanel = new AnimatedPanel(kafkaConsumerHandler);
        frame.add(animatedPanel, BorderLayout.CENTER);

        JButton button1 = new JButton("Start");
        JButton button2 = new JButton("Stop");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        button1.addActionListener(e -> {
            AnimatedPanel.timer.start();
        });
        button2.addActionListener(e -> {
            AnimatedPanel.timer.stop();
        });

        frame.setVisible(true);
    }
}
