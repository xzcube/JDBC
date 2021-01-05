package statement;

import org.junit.Test;
import util.JDBCUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**演示通过PreparedStatement替换Statement解决SQL注入问题
 * 1.PreparedStatement可以操作Blob类型的数据，而Statement做不到
 * 2.PreparedStatement可以实现更高效的批量操作
 *
 * @author xzcube
 * @date 2021/1/2 15:14
 */
public class PreparedStatementTest {
    @Test
    public void testLogin(){
        Scanner scan = new Scanner(System.in);
        System.out.println("请输入用户名：");
        String user = scan.nextLine();
        System.out.println("请输入密码：");
        String password = scan.nextLine();
        String sql = "SELECT `user`, password FROM user_table WHERE `user` = ? AND `password` = ?";
        User returnUser = getInstance(User.class, sql, user, password);
        if(returnUser != null){
            System.out.println("登录成功");
        }else {
            System.out.println("用户名不存在或密码错误");
        }
    }


    /**
     * 针对不同表的通用的查询操作，返回表中的一条记录
     * @param clazz
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
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
}
