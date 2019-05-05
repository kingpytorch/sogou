package com.yihe.crawler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionsDialog extends JDialog {
    /**
     * UID
     */
    private static final long serialVersionUID = -720863024733275820L;
    private static final int INSET = 3;
    private static final Insets COMP_INSETS = new Insets(INSET, INSET, INSET, INSET);
    private JTextField txtFilterText = new JTextField();
    private JTextField txtQuerySpan = new JTextField();

    public OptionsDialog(Frame owner) {
        super(owner);
        this.setTitle("设置");
        Dimension dim = new Dimension(200, 28);

        JPanel infoPane = new JPanel(new GridBagLayout());

        Properties pp = new Properties();
        try {
            pp.load(new FileInputStream("config.properties"));
        } catch (Exception e1) {

        }
        String keywords = pp.getProperty("keywords", "投票,候选,评选");
        String crawlerSpan = pp.getProperty("span", "60");

        infoPane.add(new JLabel("搜索文本"), newGridBagConstraints(0, 0));
        txtFilterText.setText(keywords);
        txtFilterText.setPreferredSize(dim);
        infoPane.add(txtFilterText, newGridBagConstraints(1, 0));

        infoPane.add(new JLabel("搜索间隔(秒)"), newGridBagConstraints(0, 1));
        txtQuerySpan.setPreferredSize(dim);
        txtQuerySpan.setText(String.valueOf(crawlerSpan));
        infoPane.add(txtQuerySpan, newGridBagConstraints(1, 1));

        this.add(infoPane);

        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnOk = new JButton("确定");
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Properties pp = new Properties();
                    pp.setProperty("keywords", txtFilterText.getText().trim());
                    pp.setProperty("span", txtQuerySpan.getText().trim());
                    pp.store(new FileOutputStream("config.properties"), "LEXLOO");
                } catch (Exception ex) {

                }

                OptionsDialog.this.dispose();
            }
        });
        btnPane.add(btnOk);

        JButton btnCancel = new JButton("取消");
        btnPane.add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OptionsDialog.this.dispose();
            }
        });

        this.add(btnPane, BorderLayout.SOUTH);
    }

    private static GridBagConstraints newGridBagConstraints(int gridX, int gridY) {
        return newGridBagConstraints(gridX, gridY, 1, 1);
    }

    private static GridBagConstraints newGridBagConstraints(int gridX, int gridY, int gridWidth, int gridHeight) {
        return newGridBagConstraints(gridX, gridY, gridWidth, gridHeight, 0, 0);
    }

    private static GridBagConstraints newGridBagConstraints(int gridX,
        int gridY,
        int gridWidth,
        int gridHeight,
        int weightx,
        int weighty) {
        return new GridBagConstraints(gridX,
            gridY,
            gridWidth,
            gridHeight,
            weightx,
            weighty,
            GridBagConstraints.CENTER,
            GridBagConstraints.BOTH,
            COMP_INSETS,
            0,
            0);
    }
}
