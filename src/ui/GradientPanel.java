package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * High-performance animated background panel.
 * Uses sub-pixel rendering for ultra-smooth movement.
 */
public class GradientPanel extends JPanel {
    private List<Particle> particles;
    private Timer timer;
    private final int PARTICLE_COUNT = 30;

    public GradientPanel() {
        setDoubleBuffered(true);
        setLayout(new GridBagLayout()); // Default to centered layout
        initParticles();
        
        // 60 FPS Animation
        timer = new Timer(16, e -> {
            updateParticles();
            repaint();
        });
        timer.start();
    }

    private void initParticles() {
        particles = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(
                rand.nextInt(1920), 
                rand.nextInt(1080), 
                rand.nextInt(50) + 20, 
                rand.nextFloat() * 0.4f + 0.1f
            ));
        }
    }

    private void updateParticles() {
        for (Particle p : particles) {
            p.move(getWidth(), getHeight());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // High quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Background Gradient
        GradientPaint gp = new GradientPaint(0, 0, new Color(140, 0, 0), 0, getHeight(), new Color(255, 245, 245));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw Animated Particles with sub-pixel precision
        for (Particle p : particles) {
            // Shadow/Glow
            g2d.setColor(new Color(255, 255, 255, 15));
            g2d.fill(new Ellipse2D.Double(p.x, p.y, p.radius, p.radius));
            
            // Core
            g2d.setColor(new Color(180, 0, 0, 10));
            g2d.fill(new Ellipse2D.Double(p.x + 2, p.y + 2, p.radius * 0.8, p.radius * 0.8));
        }

        // Essential for smooth animation on some systems (Linux/Windows)
        Toolkit.getDefaultToolkit().sync();
    }

    private static class Particle {
        double x, y;
        double radius;
        double dx, dy;

        Particle(double x, double y, double radius, double speed) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            Random rand = new Random();
            this.dx = (rand.nextDouble() - 0.5) * speed;
            this.dy = (rand.nextDouble() - 0.5) * speed;
        }

        void move(int width, int height) {
            x += dx;
            y += dy;
            if (x < -radius) x = width;
            if (x > width) x = -radius;
            if (y < -radius) y = height;
            if (y > height) y = -radius;
        }
    }

    /**
     * Helper to create a standard semi-transparent "Card" with a soft shadow effect.
     */
    public static JPanel createCard(int width, int height) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth()-5, getHeight()-5, 20, 20);
                
                // Draw card background
                g2.setColor(new Color(255, 255, 255, 245));
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(width, height));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        return card;
    }
}
