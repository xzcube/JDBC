package preparedstatement;

import org.junit.Test;
import util.JDBCUtil;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author xzcube
 * @date 2020/12/31 17:17
 *
 * 使用PreparedStatement来替换Statement，实现对数据表的增删改查操作
 * 增删改：不需要返回
 * 查：需要返回
 */
public class PrepareStatementTest {
    //向customers表中添加一条记录
    @Test
    public void testInsert(){
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            //获取连接
            InputStream is = PrepareStatementTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties pro = new Properties();
            pro.load(is);
            String url = pro.getProperty("url");
            String user = pro.getProperty("user");
            String password = pro.getProperty("password");
            String driverClass = pro.getProperty("driverClass");

            Class.forName(driverClass);

            connection = DriverManager.getConnection(url, user, password);
            System.out.println(connection);

            //预编译sql语句，返回preparedStatement实例
            String sql = "insert into customers(name, email, birth)values(?, ?, ?)";
            ps = connection.prepareStatement(sql);

            //填充占位符
            ps.setString(1, "哪吒");
            ps.setString(2, "nezha@gmail.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse("2001-6-4");
            System.out.println(date.getTime());
            ps.setDate(3, new java.sql.Date((date.getTime())));

            //执行sql操作
            ps.execute();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
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
    }

    //修改customers中的一条记录
    @Test
    public void testUpdate(){
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            //1.获取数据库连接
            connection = JDBCUtil.getConnection();

            //2.预编译sql语句，返回PreparedStatement的实例
            String sql = "update customers set name = ? where id = ?";
            ps = connection.prepareStatement(sql);

            //3.填充占位符
            ps.setObject(1, "莫扎特");
            ps.setObject(2, 18);

            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.资源关闭
            JDBCUtil.closeResource(connection, ps);
        }
    }

    //通用的增删改操作
    public int update(String sql, Object...args) {//sql中占位符的个数与可变形参的长度相同
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = JDBCUtil.getConnection();
            ps = connection.prepareStatement(sql);

            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]); //小心参数声明错误
            }

            //执行
            /*
            如果执行的是查询操作，有返回结果，则此方法返回true
            如果是增删改，没有返回结果，此方法返回false
            ps.execute();
             */

            //返回一个int类型的数据，表明该操作影响了几条数据
            int len = ps.executeUpdate();
            return len;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtil.closeResource(connection, ps);
        }
        return 0;
    }

    //测试通用的增删改操作
    @Test
    public void testCommonUpdate(){
//        String sql = "delete from customers where id = ?";
//        update(sql, 3);
        String sql = "update `order` set order_name = ? where order_id = ?";
        update(sql, "DD", "2");
    }
}
