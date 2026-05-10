package ui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Programmatically draws clean, vector-style icons for cross-platform consistency.
 * Fixes the issue where emojis are missing on Linux.
 * @author Emon Ahmed Joy
 */
public class VectorIcon implements Icon {
    public enum Type {
        USER, EMAIL, LOCK, KEY, ADMIN, PLUS, EDIT, SEARCH, SHIELD, HEART
    }

    private final Type type;
    private final int size;
    private final Color color;

    public VectorIcon(Type type, int size) {
        this(type, size, new Color(180, 0, 0)); // Default theme red
    }

    public VectorIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.translate(x, y);

        float s = size;
        switch (type) {
            case USER:
                g2.fill(new Ellipse2D.Float(s * 0.3f, s * 0.1f, s * 0.4f, s * 0.4f));
                g2.fill(new Arc2D.Float(s * 0.15f, s * 0.55f, s * 0.7f, s * 0.4f, 0, 180, Arc2D.CHORD));
                break;
            case EMAIL:
                g2.setStroke(new BasicStroke(s * 0.08f));
                g2.draw(new RoundRectangle2D.Float(s * 0.1f, s * 0.25f, s * 0.8f, s * 0.5f, s * 0.1f, s * 0.1f));
                Path2D emailFlap = new Path2D.Float();
                emailFlap.moveTo(s * 0.1f, s * 0.25f);
                emailFlap.lineTo(s * 0.5f, s * 0.55f);
                emailFlap.lineTo(s * 0.9f, s * 0.25f);
                g2.draw(emailFlap);
                break;
            case LOCK:
                g2.fill(new RoundRectangle2D.Float(s * 0.2f, s * 0.45f, s * 0.6f, s * 0.45f, s * 0.1f, s * 0.1f));
                g2.setStroke(new BasicStroke(s * 0.12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Arc2D.Float(s * 0.3f, s * 0.15f, s * 0.4f, s * 0.5f, 0, 180, Arc2D.OPEN));
                break;
            case KEY:
                g2.setStroke(new BasicStroke(s * 0.1f));
                g2.draw(new Ellipse2D.Float(s * 0.15f, s * 0.15f, s * 0.35f, s * 0.35f));
                g2.draw(new Line2D.Float(s * 0.45f, s * 0.45f, s * 0.85f, s * 0.85f));
                g2.draw(new Line2D.Float(s * 0.7f, s * 0.7f, s * 0.85f, s * 0.55f));
                g2.draw(new Line2D.Float(s * 0.8f, s * 0.8f, s * 0.95f, s * 0.65f));
                break;
            case ADMIN:
                g2.fill(new RoundRectangle2D.Float(s * 0.15f, s * 0.2f, s * 0.7f, s * 0.6f, s * 0.1f, s * 0.1f));
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(s * 0.4f, s * 0.35f, s * 0.2f, s * 0.2f));
                break;
            case PLUS:
                g2.setStroke(new BasicStroke(s * 0.15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Float(s * 0.5f, s * 0.2f, s * 0.5f, s * 0.8f));
                g2.draw(new Line2D.Float(s * 0.2f, s * 0.5f, s * 0.8f, s * 0.5f));
                break;
            case EDIT:
                g2.rotate(Math.toRadians(45), s / 2, s / 2);
                g2.fill(new RoundRectangle2D.Float(s * 0.35f, s * 0.1f, s * 0.3f, s * 0.7f, s * 0.05f, s * 0.05f));
                Path2D tip = new Path2D.Float();
                tip.moveTo(s * 0.35f, s * 0.8f);
                tip.lineTo(s * 0.5f, s * 0.95f);
                tip.lineTo(s * 0.65f, s * 0.8f);
                g2.fill(tip);
                break;
            case SEARCH:
                g2.setStroke(new BasicStroke(s * 0.1f));
                g2.draw(new Ellipse2D.Float(s * 0.15f, s * 0.15f, s * 0.45f, s * 0.45f));
                g2.draw(new Line2D.Float(s * 0.55f, s * 0.55f, s * 0.85f, s * 0.85f));
                break;
            case SHIELD:
                Path2D shield = new Path2D.Float();
                shield.moveTo(s * 0.15f, s * 0.2f);
                shield.curveTo(s * 0.15f, s * 0.2f, s * 0.5f, s * 0.1f, s * 0.85f, s * 0.2f);
                shield.lineTo(s * 0.85f, s * 0.6f);
                shield.curveTo(s * 0.85f, s * 0.85f, s * 0.5f, s * 0.95f, s * 0.5f, s * 0.95f);
                shield.curveTo(s * 0.5f, s * 0.95f, s * 0.15f, s * 0.85f, s * 0.15f, s * 0.6f);
                shield.closePath();
                g2.fill(shield);
                break;
            case HEART:
                Path2D heart = new Path2D.Float();
                heart.moveTo(s * 0.5f, s * 0.85f);
                heart.curveTo(s * 0.1f, s * 0.6f, s * 0.1f, s * 0.2f, s * 0.5f, s * 0.2f);
                heart.moveTo(s * 0.5f, s * 0.85f);
                heart.curveTo(s * 0.9f, s * 0.6f, s * 0.9f, s * 0.2f, s * 0.5f, s * 0.2f);
                g2.fill(heart);
                g2.fill(new Ellipse2D.Float(s * 0.22f, s * 0.15f, s * 0.35f, s * 0.35f));
                g2.fill(new Ellipse2D.Float(s * 0.43f, s * 0.15f, s * 0.35f, s * 0.35f));
                break;
        }

        g2.dispose();
    }

    @Override
    public int getIconWidth() { return size; }

    @Override
    public int getIconHeight() { return size; }
}
