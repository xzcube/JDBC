package preparedstatement;

import bean.Order;
import org.junit.Test;
import util.JDBCUtil;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 针对Order表格的查询操作
 * @author xzcube
 * @date 2021/1/1 21:13
 */
public class OrderForQuery {
    @Test
    public void testQuery() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = JDBCUtil.getConnection();
            String sql = "select order_id, order_name, order_date from `order` where order_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, 1);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int id = (int)rs.getObject(1);
                String name = (String)rs.getObject(2);
                Date date = (Date)rs.getObject(3);
                Order order = new Order(id, name, date);
                System.out.println(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        JDBCUtil.closeResource(connection, ps);
    }

    /**
     * 通用的针对Order表的查询操作
     */
    public Order orderForQuery(String sql, Object ...args) {
        /*
        针对表的字段值和类的属性名不相同的情况
        1.必须在声明sql时，使用类的属性名来命名字段的别名
        2.使用getColumnLabel()方法来替换getColumnName()方法来获取列的别名
            如果sql中没有给列起别名，getColumnLabel()获取的就是列名
         */
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = JDBCUtil.getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行，获取结果集
            resultSet = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            if(resultSet.next()){
                Order order = new Order();
                for (int i = 0; i < columnCount; i++) {
                    //获取每个列的列值：通过结果集
                    Object columnValue = resultSet.getObject(i + 1);
                    //获取每个列的列名：通过结果集的元数据
                    //获取列的列名：getColumnName()
                    //获取列的别名：getColumnLabel() --推荐使用
                    String columnLabel = metaData.getColumnLabel(i + 1);

                    //将指定的对象属性赋值为对应的columnValue：通过反射
                    Field field = Order.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(order, columnValue);
                }
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, ps, resultSet);
        }
        return null;
    }

    @Test
    public void testOrderForQuery(){
        String sql = "select order_id orderId, order_name orderName, order_date orderDate from `order` where order_id = ?";
        Order order = orderForQuery(sql, 1);
        System.out.println(order);
    }

}
