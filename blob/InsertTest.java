package blob;

import org.junit.Test;
import util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 使用PreparedStatement实现批量数据操作
 *
 * update、delete本身就具有批量操作的效果
 * 此时的批量操作主要指的是批量插入
 * 使用PrepareStatement实现更高效的批量插入
 *
 * 题目：向goods表中插入20000条数据
 *
 * @author xzcube
 * @date 2021/1/3 10:16
 */
public class InsertTest {
    //批量插入的方式:使用PreparedStatement  花费时间：77203ms
    @Test
    public void insert() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtil.getConnection();
            String sql = "insert into goods(name) values (?)";
            ps = connection.prepareStatement(sql);
            for (int i = 1; i<= 20000; i++) {
                ps.setObject(1, "name_" + i);
                ps.execute();
            }
            Long end = System.currentTimeMillis();
            System.out.println("运行时间:" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }

    /*
    批量插入的方式
    1.addBatch() executeBatch() clearBatch()
    2.mysql服务器是默认关闭批量处理的，让mysql开启批量处理的支持，需要修改配置文件的url
    ?rewriteBatchedStatements=true 写在配置文件的url后面
    3.需要使用较新的mysql的驱动
     */

    @Test
    public void insert2() { //运行时间：1779ms
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtil.getConnection();
            String sql = "insert into goods(name) values (?)";
            ps = connection.prepareStatement(sql);
            for (int i = 1; i<= 20000; i++) {
                ps.setObject(1, "name_" + i);

                //1."攒"sql
                ps.addBatch();
                if(i % 500 == 0){
                    //2.执行sql
                    ps.executeBatch();

                    ///3.清空sql
                    ps.clearParameters();
                }
            }
            Long end = System.currentTimeMillis();
            System.out.println("运行时间:" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }

    //批量插入的方法四 不允许自动提交数据
    @Test
    public void insert3() { //运行时间：插入1000000条数据  运行时间25483ms 优化后(13202ms)
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtil.getConnection();

            //设置不允许自动提交数据
            connection.setAutoCommit(false);
            String sql = "insert into goods(name) values (?)";
            ps = connection.prepareStatement(sql);
            for (int i = 1; i<= 1000000; i++) {
                ps.setObject(1, "name_" + i);

                //1."攒"sql
                ps.addBatch();
                if(i % 500 == 0){
                    //2.执行sql
                    ps.executeBatch();

                    ///3.清空sql
                    ps.clearParameters();
                }
            }
            //提交数据
            connection.commit();
            Long end = System.currentTimeMillis();
            System.out.println("运行时间:" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, ps);
        }
    }
}
