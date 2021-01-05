package databaseconnection;


import bean.Customer;
import daoplus.CustomerDAOImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.jupiter.api.Test;
import util.ConnectionUtils;
import util.JDBCUtil;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author xzcube
 * @date 2021/1/4 19:38
 */
public class DBCPTest {
    /**
     * 测试DBCP数据库连接池技术
     */
    //方式1（不推荐）
    public static Connection TestGetConnection() throws SQLException {
        //创建DBCP的数据库连接池
        BasicDataSource source = new BasicDataSource();

        //设置基本信息
        source.setDriverClassName("com.mysql.cj.jdbc.Driver");
        source.setUrl("jdbc:mysql://localhost:3306/test");
        source.setUsername("root");
        source.setPassword("mysql1234");

        //还可以设置其它连接池属性
        source.setInitialSize(10);

        //获取连接
        Connection connection = source.getConnection();
        return connection;
    }


    //方式2：使用配置文件
    @Test
    public void TestGetConnection2() throws Exception {
        Properties pro = new Properties();

        //创建配置文件的流
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");

        pro.load(is);
        //创建一个DBCP数据库连接池
        BasicDataSource source = BasicDataSourceFactory.createDataSource(pro);

        Connection connection = source.getConnection();
        System.out.println(connection);
    }

}

//测试DBCP数据库连接池的使用
class DBCPConnection{
    @Test
    public void test(){
        CustomerDAOImpl dao = new CustomerDAOImpl();
        Customer customer = null;
        Connection connection = null;
        try {
            connection = ConnectionUtils.getConnectionDBCP();
            customer = dao.getCustomerById(connection, 18);
            System.out.println(customer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }

    }
}
