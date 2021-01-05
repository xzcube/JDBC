package databaseconnection;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author xzcube
 * @date 2021/1/4 21:19
 */
public class DruidTest {
    @Test
    public void getConnection() throws Exception {
        Properties pro = new Properties();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        pro.load(is);
        DataSource source = DruidDataSourceFactory.createDataSource(pro);
        Connection connection = source.getConnection();
        System.out.println(connection);
    }
}
