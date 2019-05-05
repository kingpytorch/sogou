package com.yihe.crawler;

import javax.swing.SwingUtilities;

/**
 * 抓取
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainFra();
            }
        });
    }
}
