package ui;

import java.awt.*;
import javax.swing.*;

/**
 * Custom panel with a blood-donor themed gradient background.
 */
public class GradientPanel extends JPanel {
    public GradientPanel() {
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient from a deep blood red to a soft white/gray
        GradientPaint gp = new GradientPaint(0, 0, new Color(180, 0, 0), 0, getHeight(), new Color(255, 245, 245));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add some subtle "circular" design elements for a modern feel
        g2d.setColor(new Color(255, 255, 255, 20));
        g2d.fillOval(getWidth() - 200, -50, 300, 300);
        g2d.fillOval(-100, getHeight() - 150, 400, 400);
    }
}
