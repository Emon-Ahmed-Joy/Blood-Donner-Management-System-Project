package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * A custom rounded button with hover effects.
 * @author Emon Ahmed Joy
 */
public class RoundedButton extends JButton {
    private Color normalColor = new Color(180, 0, 0);
    private Color hoverColor = new Color(220, 20, 20);
    private float alpha = 0.0f;
    private Timer fadeTimer;
    private int radius = 25;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 18));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startFade(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                startFade(false);
            }
        });
    }

    private void startFade(boolean in) {
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        fadeTimer = new Timer(15, e -> {
            if (in) {
                alpha += 0.1f;
                if (alpha >= 1.0f) { alpha = 1.0f; fadeTimer.stop(); }
            } else {
                alpha -= 0.1f;
                if (alpha <= 0.0f) { alpha = 0.0f; fadeTimer.stop(); }
            }
            repaint();
        });
        fadeTimer.start();
    }

    public RoundedButton(String text, Color baseColor, Color hoverColor) {
        this(text);
        this.normalColor = baseColor;
        this.hoverColor = hoverColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Interpolate colors
        int r = (int) (normalColor.getRed() + alpha * (hoverColor.getRed() - normalColor.getRed()));
        int g_val = (int) (normalColor.getGreen() + alpha * (hoverColor.getGreen() - normalColor.getGreen()));
        int b = (int) (normalColor.getBlue() + alpha * (hoverColor.getBlue() - normalColor.getBlue()));
        Color currentColor = new Color(r, g_val, b);

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
