package transaction;

import org.junit.Test;
import util.JDBCUtil;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 通用的增删改查操作
 * @author xzcube
 * @date 2021/1/3 16:34
 */
public class TransactionTest {
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

    /*
    针对于数据表user_table来说
    AA用户给BB用户转账100

    ****************************************************************************************
    未考虑数据库事务情况下的转账操作
     */
    @Test
    public void testUpdate(){
        String sql1 = "update user_table set balance = balance - 100 where user = ?";
        int aa = update(sql1, "AA");

        //模拟一个转账异常
        System.out.println(10/0);

        String sql2 = "update user_table set balance = balance + 100 where user = ?";
        int bb = update(sql2, "BB");
        if(aa != 0 && bb != 0){
            System.out.println("转账成功");
        }else {
            System.out.println("转账失败");
        }
    }

    /*
    **************************************************************************************
    *考虑数据库事务的转账操作
     */
    //通用的增删改操作   考虑事务上的
    public int update(Connection connection, String sql, Object...args) {//sql中占位符的个数与可变形参的长度相同
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);

            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]); //小心参数声明错误
            }

            //返回一个int类型的数据，表明该操作影响了几条数据
            int len = ps.executeUpdate();
            return len;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtil.closeResource(null, ps);
        }
        return 0;
    }


    @Test
    public void testUpdateWithTx() {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();

            //关闭数据的自动提交功能
            connection.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance - 100 where user = ?";
            int aa = update(connection, sql1, "AA");

            //模拟转账过程中的异常
            System.out.println(10/0);

            String sql2 = "update user_table set balance = balance + 100 where user = ?";
            int bb = update(connection, sql2, "BB");

            if(aa != 0 && bb != 0){
                System.out.println("转账成功");
            }else {
                System.out.println("转账失败");
            }

            //提交数据
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();

            //如果出现异常，进入catch中，回滚数据
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {

            //修改为自动提交数据（主要针对数据库连接池）
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            //关闭连接
            JDBCUtil.closeResource(connection, null);
        }
    }


    //通用的查询操作，用于返回数据表中的一条记录（考虑事务）
    public <T> T getInstance(Connection connection, Class<T> clazz, String sql, Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
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
            JDBCUtil.closeResource(null, ps, rs);
        }
        return null;
    }

    @Test
    public void testTransactionSelect() throws Exception {
        Connection connection = JDBCUtil.getConnection();

        //设置数据库的隔离级别
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        //获取当前连接的隔离级别
        System.out.println(connection.getTransactionIsolation());

        //取消自动提交数据
        connection.setAutoCommit(false);
        String sql = "select user, password, balance from user_table where user = ?";
        User user = getInstance(connection, User.class, sql, "CC");
        System.out.println(user);
    }

    @Test
    public void testTransactionUpdate() throws Exception {
        Connection connection = JDBCUtil.getConnection();

        //取消自动提交
        connection.setAutoCommit(false);
        String sql = "update user_table set balance = ? where user = ?";
        update(connection, sql, 4000, "CC");
        Thread.sleep(10000);
        System.out.println("修改结束");
    }
}
