package preparedstatement;

import bean.Customer;
import org.junit.Test;
import util.JDBCUtil;

import java.lang.reflect.Field;
import java.sql.*;

/** 针对Customers表的查询操作
 * @author xzcube
 * @date 2021/1/1 19:30
 */
public class CustomerForQuery {
    @Test
    public void testQuery1() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = JDBCUtil.getConnection();
            String sql = "select id, name, email, birth from customers where id = ?";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, 1);

            //执行并返回结果集
            resultSet = ps.executeQuery();

            //处理结果集
            if(resultSet.next()){//判断结果集的下一条是否有数据，如果有下一条，返回true，并指针下移
                //获取当前这条数据的各个字段值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                //方式1：直接显示
                //System.out.println("id = " + id + ",name = " + name);

                //方式二：将数据封装在数组中
                //Object[] data = new Object[]{id, name, email, birth};

                //方式三：将信息封装在一个类的对象中
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtil.closeResource(connection, ps, resultSet);
        }
    }


    /**
     * 针对customers表的查询操作
     * @param sql 需要预编译的sql语句
     * @param args 需要填写的占位符
     * @return 一个Customer类的对象
     * @throws Exception
     */
    public Customer queryForCustomers(String sql, Object ... args) {
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
                Customer customer = new Customer();
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
                    String columnName = metaData.getColumnName(i + 1);

                    //给customer对象指定的某个属性赋值为value(通过反射)
                    Field field = Customer.class.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(customer, columnValue);
                }
                return customer;
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
    public void testQueryForCustomers(){
        String sql = "select id, name, birth, email from customers where id = ?";
        String sql1 = "select name, email from customers where name = ?";
        Customer customer = queryForCustomers(sql1, "周杰伦");
        System.out.println(customer);

//        Customer customer = queryForCustomers(sql, 13);
//        System.out.println(customer);
    }
}
