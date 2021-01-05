package databaseconnection;

import org.junit.Test;
import com.mchange.v2.c3p0.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author xzcube
 * @date 2021/1/4 15:28
 */
public class C3P0Test {
    //方式1：
    @Test
    public void testGetConnection() throws Exception{
        //获取c3p0数据库连接池
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass( "com.mysql.cj.jdbc.Driver" ); //loads the jdbc driver
        cpds.setJdbcUrl( "jdbc:mysql://localhost:3306/test" );
        cpds.setUser("root");
        cpds.setPassword("mysql1234");

        /*
        通过设置相关参数对数据库连接池进行管理
        设置初始的数据库连接池的连接数
        * */
        cpds.setInitialPoolSize(10);

        Connection connection = cpds.getConnection();

        System.out.println(connection);

        //销毁c3p0数据库连接池
        //DataSources.destroy(cpds);
    }


    //方式2:使用配置文件
    static ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");
    public static Connection testGetConnection2() throws SQLException {
        Connection connection = cpds.getConnection();
        return connection;
    }
}
