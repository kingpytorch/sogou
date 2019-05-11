package com.yihe.crawler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class MainFra extends JFrame {
    /**
     * UID
     */
    private static final long serialVersionUID = 8108922838159780144L;

    private DefaultTableModel defaultModel;

    private Crawler crawler;

    public MainFra() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setTitle("搜狗微信抓取");
        this.setSize(new Dimension(800, 600));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.defaultModel = new DefaultTableModel(new String[] {"序号", "已复制", "标题", "ID"}, 0);
        this.crawler = new Crawler(this.defaultModel);

        this.setLayout(new BorderLayout());

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
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                crawler.quit();
            }
        }));

        ToolPane toolPane = new ToolPane(this.crawler);
        this.add(toolPane, BorderLayout.NORTH);

        this.initMenu(this);
    }

    private void initMenu(final JFrame mainFrame) {
        JMenuBar menuBar = new JMenuBar();

        JMenu systemMenu = new JMenu("系统");

        JMenuItem menuOption = new JMenuItem("设置");
        menuOption.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dlg = new OptionsDialog(mainFrame);
                dlg.setSize(350, 200);
                dlg.setResizable(false);
                dlg.setLocationRelativeTo(null);

                dlg.setVisible(true);
            }
        });

        systemMenu.add(menuOption);

        systemMenu.addSeparator();

        JMenuItem menuExit = new JMenuItem("退出");
        menuExit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        systemMenu.add(menuExit);
        menuBar.add(systemMenu);

        mainFrame.setJMenuBar(menuBar);
    }
}