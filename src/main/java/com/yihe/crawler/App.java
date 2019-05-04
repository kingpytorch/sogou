package com.yihe.crawler;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * 抓取
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame fra = new MainFra();
                fra.setPreferredSize(new Dimension(800, 600));
                fra.pack();
            }
        });
    }
}
