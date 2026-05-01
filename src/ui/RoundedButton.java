package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * A modern, rounded button with hover effects and gradients.
 * @author Emon Ahmed Joy
 */
public class RoundedButton extends JButton {
    private Color normalColor = new Color(180, 0, 0);
    private Color hoverColor = new Color(220, 20, 20);
    private Color currentColor = normalColor;
    private int radius = 25; // Increased for a more modern pill-shape

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = normalColor;
                repaint();
            }
        });
    }

    public RoundedButton(String text, Color baseColor, Color hoverColor) {
        this(text);
        this.normalColor = baseColor;
        this.hoverColor = hoverColor;
        this.currentColor = normalColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dynamic Gradient
        Color color2 = currentColor.darker();
        GradientPaint gp = new GradientPaint(0, 0, currentColor, 0, getHeight(), color2);
        g2.setPaint(gp);
        
        // Draw the pill shape
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        
        // Subtle Inner Highlight
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, radius, radius));
        
        g2.dispose();
        super.paintComponent(g);
    }
}
