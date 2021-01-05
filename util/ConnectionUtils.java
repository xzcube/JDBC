package util;

import bean.Customer;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import daoplus.CustomerDAOImpl;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author xzcube
 * @date 2021/1/4 17:05
 */
public class ConnectionUtils {

    /**
     * 使用c3p0数据库连接池技术
     * @return
     * @throws SQLException
     */
    //数据库连接池只需提供一个即可
    private static ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");
    public static Connection getConnectionC3P0() throws SQLException {
        Connection connection = cpds.getConnection();
        return connection;
    }


    /**
     * 使用DBCP数据库连接池技术来获取连接
     * @return
     * @throws Exception
     */
    //创建一个数据库连接池
    private static DataSource source = null;
    static {
        try {
            Properties pro = new Properties();

            //创建配置文件的流
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
            pro.load(is);
            //实例化连接池
            source = BasicDataSourceFactory.createDataSource(pro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnectionDBCP() throws Exception{

        Connection connection = source.getConnection();
        return connection;
    }


    /**
     * 使用Druid数据库连接池获取连接
     * @return
     */
    private static DataSource druidSource;
    static {
        try {
            Properties pro = new Properties();
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
            pro.load(is);
            druidSource = DruidDataSourceFactory.createDataSource(pro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnectionDruid() throws SQLException {

        Connection connection = druidSource.getConnection();
        return connection;
    }
}


class DruidTest{
    @Test
    public void test() throws SQLException {
        CustomerDAOImpl dao = new CustomerDAOImpl();
        Connection connection = ConnectionUtils.getConnectionDruid();
        Customer customer = dao.getCustomerById(connection, 18);
        System.out.println(customer);
    }

}

