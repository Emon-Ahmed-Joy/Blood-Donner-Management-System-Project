package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * Custom panel with an animated blood-donor themed background.
 * Simulates slowly floating blood cells/drops.
 */
public class GradientPanel extends JPanel {
    private List<Particle> particles;
    private Timer timer;
    private final int PARTICLE_COUNT = 25;

    public GradientPanel() {
        setLayout(new BorderLayout());
        initParticles();
        
        // Animation Timer (60 FPS approx)
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateParticles();
                repaint();
            }
        });
        timer.start();
    }

    private void initParticles() {
        particles = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(
                rand.nextInt(1280), 
                rand.nextInt(720), 
                rand.nextInt(40) + 10, 
                rand.nextFloat() * 0.5f + 0.2f
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Static Gradient Background
        GradientPaint gp = new GradientPaint(0, 0, new Color(150, 0, 0), 0, getHeight(), new Color(255, 240, 240));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw Animated Particles
        for (Particle p : particles) {
            g2d.setColor(new Color(255, 255, 255, 30)); // Subtle white "glow"
            g2d.fillOval((int)p.x, (int)p.y, p.radius, p.radius);
            
            g2d.setColor(new Color(200, 0, 0, 20)); // Subtle red shadow
            g2d.fillOval((int)p.x + 2, (int)p.y + 2, p.radius, p.radius);
        }
    }

    // Helper class for background particles
    private static class Particle {
        float x, y;
        int radius;
        float speed;
        float dx, dy;

        Particle(float x, float y, int radius, float speed) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.speed = speed;
            Random rand = new Random();
            this.dx = (rand.nextFloat() - 0.5f) * speed;
            this.dy = (rand.nextFloat() - 0.5f) * speed;
        }

        void move(int width, int height) {
            x += dx;
            y += dy;

            // Screen wrap
            if (x < -radius) x = width;
            if (x > width) x = -radius;
            if (y < -radius) y = height;
            if (y > height) y = -radius;
        }
    }

    // Call this to stop animation if needed (performance)
    public void stopAnimation() {
        if (timer != null) timer.stop();
    }
}
