package dbutils;

import bean.Customer;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.Test;
import util.ConnectionUtils;
import util.JDBCUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**DBUtils 封装了针对于数据库的增删改查操作
 * @author xzcube
 * @date 2021/1/5 9:25
 */
public class QueryRunnerTest {
    //测试插入
    @Test
    public void testInsert() {
        Connection connection = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection = ConnectionUtils.getConnectionDruid();
            String sql = "insert into customers (name, email, birth) values(?, ?, ?)";
            int update = runner.update(connection, sql, "小刘", "xiaoliu@126.com", "1997-1-11");
            if (update != 0){
                System.out.println("添加成功!");
            }else {
                System.out.println("添加失败");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    //测试查询操作
    /*
    BeanHandler:是ResultSetHandler接口实现类，用于封装一条记录
     */
    @Test
    public void testQuery() {
        QueryRunner runner = new QueryRunner();

        Connection connection = null;
        try {
            connection = ConnectionUtils.getConnectionDruid();

            String sql = "select id, name, email from customers where id < ?";
            BeanListHandler<Customer> handler = new BeanListHandler(Customer.class);
            List<Customer> list = runner.query(connection, sql, handler, 25);
            list.forEach(System.out::println);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    /*
    MapHandler:ResultSetHandler接口的实现类，对应表中的一条记录
     */
    @Test
    public void testQuery2() {
        QueryRunner runner = new QueryRunner();

        Connection connection = null;
        try {
            connection = ConnectionUtils.getConnectionDruid();

            String sql = "select id, name, email from customers where id = ?";
            MapHandler handler = new MapHandler();
            Map<String, Object> map = runner.query(connection, sql, handler, 25);
            System.out.println(map);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    /*
    MapListHandler:是ResultSerHandler接口实现类，对应表中多条记录
    将字段及相应字段的值作为map中的key和value，将这些添加到list中
     */
    @Test
    public void testQuery3() {
        QueryRunner runner = new QueryRunner();

        Connection connection = null;
        try {
            connection = ConnectionUtils.getConnectionDruid();

            String sql = "select id, name, email from customers where id < ?";
            MapListHandler handler = new MapListHandler();
            List<Map<String, Object>> list = runner.query(connection, sql, handler, 25);
            list.forEach(System.out::println);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    /*
    ScalarHandler：ResultSetHandler接口的实现类之一，将单个值封装
     */
    @Test
    public void testQuery5() {
        Connection connection = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection = ConnectionUtils.getConnectionDruid();
            String sql = "select count(*) from customers";
            ScalarHandler<Object> handler = new ScalarHandler<>();
            Object query = runner.query(connection, sql, handler);
            System.out.println(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    @Test
    public void testQuery6() {
        Connection connection = null;
        try {
            QueryRunner runner = new QueryRunner();
            connection = ConnectionUtils.getConnectionDruid();
            String sql = "select max(birth) from customers";
            ScalarHandler<Object> handler = new ScalarHandler<>();
            Date query = (Date) runner.query(connection, sql, handler);
            System.out.println(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }
}
