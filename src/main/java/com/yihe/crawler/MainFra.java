package com.yihe.crawler;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
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

        this.defaultModel = new DefaultTableModel(new String[] {"序号", "已复制", "日期", "关键字", "标题", "ID"}, 0);
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
        table.getColumnModel().getColumn(0).setCellRenderer(new MyCellRenderer());
        table.getColumnModel().getColumn(1).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setCellRenderer(new MyCellRenderer());
        table.getColumnModel().getColumn(2).setMaxWidth(72);
        table.getColumnModel().getColumn(2).setCellRenderer(new MyCellRenderer());
        table.getColumnModel().getColumn(3).setMaxWidth(60);
        table.getColumnModel().getColumn(3).setCellRenderer(new MyCellRenderer());
        table.getColumnModel().removeColumn(table.getColumnModel().getColumn(5));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable component = (JTable)e.getSource();
                    int row = component.rowAtPoint(e.getPoint());

                    String data = String.valueOf(component.getModel().getValueAt(row, 4));

                    String status = String.valueOf(component.getModel().getValueAt(row, 1));
                    if (!"√".equals(status)) {
                        component.getModel().setValueAt("√", row, 1);
                        ItemDao.updateStatus(String.valueOf(component.getModel().getValueAt(row, 5)));
                    }

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
        this.initData();
    }

    private void initMenu(final JFrame mainFrame) {
        JMenuBar menuBar = new JMenuBar();

        JMenu systemMenu = new JMenu("系统");

        JMenuItem menuOption = new JMenuItem("设置");
        menuOption.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dlg = new OptionsDialog(mainFrame);
                dlg.setSize(350, 240);
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

    private void initData() {
        try {
            ItemDao.crateTable();
            ItemDao.deleteOldData(this.getSaveDays());
            int count = 1;
            for (Item item : ItemDao.getItems()) {
                this.defaultModel
                    .addRow(new String[] {String.valueOf(count), item.getStatus(), item.getDateStr(), item.getKeyword(),
                        item.getCaption(), item.getMd5()});
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSaveDays() {
        Properties pp = new Properties();
        try {
            pp.load(new FileInputStream("config.properties"));
        } catch (Exception ex) {

        }

        int days = Integer.parseInt(pp.getProperty("saveDays", "2"));
        if (days > 7) {
            days = 7;
        }

        return days;
    }

    class MyCellRenderer extends DefaultTableCellRenderer {
        /**
         * UID
         */
        private static final long serialVersionUID = 4862364547990169509L;

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}