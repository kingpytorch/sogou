package com.yihe.crawler;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class ToolPane extends JPanel {
    /**
     * UID
     */
    private static final long serialVersionUID = -1932668073593661325L;
    private static final Dimension DIM = new Dimension(100, 28);
    private JTextField txtFilterText = new JTextField();
    private JTextField txtQuerySpan = new JTextField();
    private String keyWorkds = "投票,候选,评选";
    private int crawlerSpan = 60;
    private JButton btnStart = new JButton("开始");
    private JButton btnScrawler = new JButton("抓取");
    private Crawler crawler;

    public ToolPane(Crawler crawler) {
        this.crawler = crawler;

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        this.add(new JLabel("搜索文本"));

        txtFilterText.setText(keyWorkds);
        txtFilterText.setPreferredSize(DIM);
        this.add(txtFilterText);

        this.add(new JLabel("搜索间隔"));
        txtQuerySpan.setPreferredSize(DIM);
        txtQuerySpan.setText(String.valueOf(crawlerSpan));
        this.add(txtQuerySpan);

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
                        ToolPane.this.crawler.crawlerKeywords(keyWorkds);

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
