package blob;

import bean.Customer;
import org.junit.Test;
import util.JDBCUtil;

import java.io.*;
import java.sql.*;

/**使用PrePareStatement操作Blob数据
 * @author xzcube
 * @date 2021/1/2 22:04
 */
public class BlobTest {

    @Test
    public void test() {
        Connection connection = null;
        PreparedStatement pst = null;
        try {
            connection = JDBCUtil.getConnection();
            String sql = "delete from customers where name = ?";
            pst = connection.prepareStatement(sql);
            pst.setObject(1, "哪吒");
            int len = pst.executeUpdate();

            if(len != 0){
                System.out.println("删除成功");
            }else {
                System.out.println("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, pst);
        }
    }


    //向数据表customers中插入Blob类型的字段
    @Test
    public void testInsert() {
        Connection connection = null;
        PreparedStatement pst = null;
        try {
            connection = JDBCUtil.getConnection();
            String sql = "insert into customers(name, email, birth, photo) values(?, ?, ?, ?)";
            pst = connection.prepareStatement(sql);
            FileInputStream fis = new FileInputStream(new File("智乃.jpg"));

            pst.setObject(1, "小闫");
            pst.setObject(2, "xiaoyan@126.com");
            pst.setObject(3, "2001-06-04");
            pst.setBlob(4, fis);

            int len = pst.executeUpdate();
            if(len != 0){
                System.out.println("添加成功");
            }else {
                System.out.println("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, pst);
        }
    }

    //查询数据表中的Blob类型字段
    @Test
    public void testQuery() {
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet resultSet = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            connection = JDBCUtil.getConnection();

            String sql = "select id, name, email, birth, photo from customers where id = ?";
            pst = connection.prepareStatement(sql);
            pst.setInt(1, 21);

            resultSet = pst.executeQuery();
            if(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                Date birth = resultSet.getDate("birth");

                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);

                Blob photo = resultSet.getBlob("photo");

                //将Blob类型的字段下载下来，以文件的方式保存在本地
                is = photo.getBinaryStream();
                fos = new FileOutputStream("小闫.jpg");
                byte[] buffer = new byte[1024];
                int len;

                while ((len = is.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

           JDBCUtil.closeResource(connection, pst, resultSet);
        }

    }
}
