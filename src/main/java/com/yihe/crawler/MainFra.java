package com.yihe.crawler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class MainFra extends JFrame {
    /**
     * UID
     */
    private static final long serialVersionUID = 7785711670000642386L;

    private static final int INSET = 3;
    private static final Insets COMP_INSETS = new Insets(INSET, INSET, INSET, INSET);

    private JTextField txtFilterText = new JTextField();
    private JTextField txtQuerySpan = new JTextField();
    private String keyWorkds = "投票,候选,评选";
    private int crawlerSpan = 60;

    private DefaultTableModel defaultModel;

    private Crawler crawler;

    public MainFra() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setTitle("搜狗微信抓取");
        this.setPreferredSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.defaultModel = new DefaultTableModel(new String[] {"序号", "已复制", "标题", "ID"}, 0);
        this.crawler = new Crawler(this.defaultModel);

        this.setLayout(new BorderLayout());

        Dimension dim = new Dimension(100, 28);
        JPanel pane = new JPanel(new GridBagLayout());

        pane.add(new JLabel("搜索文本"), newGridBagConstraints(0, 0));
        txtFilterText.setText(keyWorkds);
        txtFilterText.setPreferredSize(dim);
        pane.add(txtFilterText, newGridBagConstraints(1, 0));

        pane.add(new JLabel("搜索间隔"), newGridBagConstraints(0, 1));
        txtQuerySpan.setPreferredSize(dim);
        txtQuerySpan.setText(String.valueOf(crawlerSpan));
        txtQuerySpan.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {
                } else {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        pane.add(txtQuerySpan, newGridBagConstraints(1, 1));

        JButton btnStart = new JButton("开始");
        JButton btnScrawler = new JButton("抓取");
        btnScrawler.setEnabled(false);

        btnStart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        crawler.start();

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
                        crawler.crawlerKeywords(keyWorkds);

                        return null;
                    }

                };

                worker.execute();

                btnScrawler.setEnabled(false);
            }
        });

        pane.add(btnStart, newGridBagConstraints(0, 2));
        pane.add(btnScrawler, newGridBagConstraints(1, 2));

        this.add(pane, BorderLayout.EAST);

        JPanel paneTable = new JPanel(new BorderLayout());

        JTable table = new JTable(defaultModel) {
            /**
             * UID
             */
            private static final long serialVersionUID = 6604219163344115368L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setRowHeight(28);
        table.getTableHeader().setPreferredSize(new Dimension(table.getWidth(), 28));
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setMaxWidth(60);
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(3));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = ((JTable)e.getSource()).rowAtPoint(e.getPoint());

                    String data = String.valueOf(((JTable)e.getSource()).getModel().getValueAt(row, 2));
                    ((JTable)e.getSource()).getModel().setValueAt("已复制", row, 1);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable trans = new StringSelection(data);

                    clipboard.setContents(trans, null);
                }
            }
        });

        paneTable.add(table.getTableHeader(), BorderLayout.NORTH);
        paneTable.add(table, BorderLayout.CENTER);

        this.add(new JScrollPane(paneTable), BorderLayout.CENTER);

        this.setVisible(true);
        this.setSize(500, 350);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                crawler.quit();
            }
        }));
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
