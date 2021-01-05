package util;

import bean.Customer;
import dao.CustomerDAOImpl;
import org.junit.Test;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 操作数据库的工具类
 * @author xzcube
 * @date 2020/12/31 18:52
 */
public class JDBCUtil {
    //获取数据库连接
    public static Connection getConnection() throws Exception{
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        Properties pro = new Properties();
        pro.load(is);

        String url = pro.getProperty("url");
        String user = pro.getProperty("user");
        String password = pro.getProperty("password");
        String driverClass = pro.getProperty("driverClass");
        Class.forName(driverClass);
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }


    public static void closeResource(Connection connection, Statement ps){
        //资源关闭
        if(ps != null) {
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }


    public static void closeResource(Connection connection, Statement ps, ResultSet res){
        //资源关闭
        if(ps != null) {
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if(res != null){
            try {
                res.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}

class DAOTest{
    @Test
    public void test() throws SQLException {
        CustomerDAOImpl dao = new CustomerDAOImpl();
        Connection connection = ConnectionUtils.getConnectionC3P0();
        Customer customer = dao.getCustomerById(connection, 18);
        System.out.println(customer);
    }
}

