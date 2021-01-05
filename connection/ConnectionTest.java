package connection;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest {
    //方式1
    @Test
    public void testConnection1() throws SQLException {
        //获取Driver实现类对象
        Driver driver = new com.mysql.cj.jdbc.Driver();
        //jdbc:mysql:协议
        //localhost：ip地址
        //3306：端口号
        //test：数据库名
        String url = "jdbc:mysql://localhost:3306/test";
        //将用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "mysql1234");
        Connection conn = driver.connect(url, info);
        System.out.println(conn);
    }

    //方式2：对方式1的迭代(在如下的程序中不出现第三方API，使程序具有更好的可移植性)
    @Test
    public void testConnection2() throws Exception {
        //使用反射获取Driver实现类对象
        Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //提供要连接的数据库
        String url = "jdbc:mysql://localhost:3306/test";

        //提供需要连接的用户名和密码
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "mysql1234");

        //4.获取连接
        Connection connect = driver.connect(url, info);
        System.out.println(connect);
    }

    //方式三：使用DriverManager代替Driver
    @Test
    public void testConnection3() throws Exception {
        //获取Driver实现类的对象
        Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //提供另外三个连接的基本信息
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "mysql1234";

        //注册驱动
        DriverManager.registerDriver(driver);

        //获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    //方式四：在方式三的基础上进行优化
    //我们只需要加载驱动，不用显示地注册驱动
    @Test
    public void testConnection4() throws Exception{
        //提供Driver实现类的对象
        //在mysql的Driver实现类中，声明了静态代码块，随着类的加载而执行，这个代码块帮我们生成了Driver类的对象并注册了驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        //提供基本信息
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "mysql1234";

        //建立连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    //方式5：将数据库连接所需要的基本信息声明在配置文件中，通过读取配置文件的方式获取连接信息
    /*
    1.实现了数据与代码的分离，实现了解耦
    2.如果需要修改配置文件信息，就可以避免程序重新打包
     */
    @Test
    public void testConnection5() throws Exception{
        //读取配置文件中的四个基本信息
        InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
        /*
        class.getResourceAsStream(String path) 如果path是以/开头的，表示从根目录搜索（即src文件夹）
        如果path不是以/开头的，则是从当前类所在包下搜索
        class.getClassLoader().getResourceAsStream() 无论path是不是以/开头的，都会从根目录下搜索
         */
        //InputStream rs = ConnectionTest.class.getResourceAsStream("/jdbc.properties");
        Properties pros = new Properties();

        pros.load(is);
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //加载驱动
        Class.forName(driverClass);

        //获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }
}
