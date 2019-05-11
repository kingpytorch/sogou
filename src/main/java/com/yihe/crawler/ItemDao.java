package com.yihe.crawler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * 存储查询7天的数据<br/>
 * 字段包括MD5ID、标题、是否已处理、数据日期
 * 
 * @author lexloo
 * @date 2019/05/11
 */
public class ItemDao {
    public static void crateTable() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DB.getInstance().getConnection();
            DatabaseMetaData meta = conn.getMetaData();

            ResultSet rsTables = meta.getTables(null, null, "ITEMS",
                new String[] {"TABLE"});
            if (!rsTables.next()) {
                stmt = conn.createStatement();
                String sql = "create table items"
                    + "(md5 varchar(32),"
                    + "caption varchar(500),"
                    + "status varchar(6),"
                    + "date_str varchar(10),"
                    + "keyword varchar(50),"
                    + "modify_time timestamp,"
                    + "primary key(md5))";
                stmt.execute(sql);
            }
            rsTables.close();
        } finally {
            releaseConnection(conn, stmt, null);
        }
    }

    public static void addItem(Item item) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DB.getInstance().getConnection();

            stmt = conn
                .prepareStatement("insert into items values(?,?,?,?,?,?)");
            stmt.setString(1, item.getMd5());
            stmt.setString(2, item.getCaption());
            stmt.setString(3, item.getStatus());
            stmt.setString(4, item.getDateStr());
            stmt.setString(5, item.getKeyword());
            stmt.setObject(6, item.getModifyTime());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                releaseConnection(conn, stmt, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Item> getItems() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DB.getInstance().getConnection();
            stmt = conn
                .prepareStatement("select md5, caption, status, date_str, keyword from items order by modify_time");
            rs = stmt.executeQuery();
            List<Item> items = new ArrayList<Item>();
            while (rs.next()) {
                Item item =
                    new Item(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), null);

                items.add(item);
            }

            return items;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                releaseConnection(conn, stmt, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    public static void updateStatus(String md5) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DB.getInstance().getConnection();
            stmt = conn
                .prepareStatement("update items set status = '√' where md5 = ?");
            stmt.setString(1, md5);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                releaseConnection(conn, stmt, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除旧数据
     * 
     * @param days 保留天数
     */
    public static void deleteOldData(int days) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DB.getInstance().getConnection();
            stmt = conn
                .prepareStatement("delete from items where modify_time > ?");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -days);
            stmt.setObject(1, calendar.getTime());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                releaseConnection(conn, stmt, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void releaseConnection(Connection conn, Statement stmt,
        ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }
}