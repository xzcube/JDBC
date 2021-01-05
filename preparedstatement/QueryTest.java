package preparedstatement;

import bean.Customer;
import bean.Order;
import org.junit.Test;
import util.JDBCUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**使用PreparedStatement实现针对不同表的通用的查询操作(返回表中一条记录)
 * @author xzcube
 * @date 2021/1/2 11:03
 */
public class QueryTest {
    public <T> T getInstance(Class<T> clazz, String sql, Object ...args){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtil.getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = rs.getMetaData();
            //通过ResultSetMetaData获取表的列数
            int columnCount = metaData.getColumnCount();
            if(rs.next()){
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
                    String columnLabel = metaData.getColumnLabel(i + 1);

                    //给对象指定的某个属性赋值为value(通过反射)
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源关闭
            JDBCUtil.closeResource(connection, ps, rs);
        }
        return null;
    }

    @Test
    public void test(){
        String sql = "select id, name, email from customers where id = ?";
        Customer customer = getInstance(Customer.class, sql, 12);
        System.out.println(customer);
        String sql1 = "select order_id orderId, order_name orderName, order_date orderDate from `order` where order_id = ?";
        Order order = getInstance(Order.class, sql1, 1);
        System.out.println(order);
    }


    //针对多条数据的通用的查询方法
    public <T> List<T> getForList(Class<T> clazz, String sql, Object ...args){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtil.getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = rs.getMetaData();
            //通过ResultSetMetaData获取表的列数
            int columnCount = metaData.getColumnCount();
            //创建对象集合
            List<T> list = new ArrayList<>();
            while (rs.next()){
                T t = clazz.newInstance();
                //给t对象指定的属性赋值
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
                    String columnLabel = metaData.getColumnLabel(i + 1);

                    //给对象指定的某个属性赋值为value(通过反射)
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                //将赋值后的t对象添加到列表中
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源关闭
            JDBCUtil.closeResource(connection, ps, rs);
        }
        return null;
    }

    @Test
    public void testGetForList(){
        String sql = "select order_id orderId, order_name orderName, order_date orderDate from ? where order_id < ? and order_id = ?";
        List<Order> orderList = getForList(Order.class, sql,   12, 4);
        orderList.forEach(System.out::println);
    }
}
