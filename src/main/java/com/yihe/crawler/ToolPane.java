package com.yihe.crawler;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class ToolPane extends JPanel {
    /**
     * UID
     */
    private static final long serialVersionUID = -1932668073593661325L;
    private JButton btnStart = new JButton("开始(登录、扫描微信)");
    private JButton btnScrawler = new JButton("抓取");

    private Crawler crawler;

    public ToolPane(Crawler crawler) {
        this.crawler = crawler;

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnScrawler.setEnabled(false);

        btnStart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        ToolPane.this.crawler.start();

                        return null;
                    }

                };

                worker.execute();
                btnStart.setEnabled(false);
                btnScrawler.setEnabled(true);
            }
        });

        btnScrawler.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        ToolPane.this.crawler.crawlerKeywords();

                        return null;
                    }

                };

                worker.execute();

                btnScrawler.setEnabled(false);
            }
        });

        this.add(btnStart);
        this.add(btnScrawler);
    }
}
