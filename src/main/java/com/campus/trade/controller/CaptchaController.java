package com.campus.trade.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int WIDTH = 100;
    private static final int HEIGHT = 40;

    @GetMapping
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            captcha.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        HttpSession session = request.getSession();
        session.setAttribute("captcha", captcha.toString());

        g.setFont(new Font("Arial", Font.BOLD, 24));
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawString(String.valueOf(captcha.charAt(i)), 20 + i * 22, 28 + random.nextInt(6) - 3);
        }

        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
        g.dispose();
    }
}
