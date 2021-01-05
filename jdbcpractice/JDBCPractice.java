package jdbcpractice;

import org.junit.Test;

import java.sql.*;
import java.util.Scanner;

/**
 * @author xzcube
 * @date 2021/1/2 18:56
 */


public class JDBCPractice {
    /*
    使用JDBC实现往用户表中添加1个用户，注意密码存储使用mysql的password()函数进行加密
    */
    @Test
    public void test1(){
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            Scanner scan = new Scanner(System.in);
            System.out.print("请输入用户名：");
            String userName = scan.nextLine();
            System.out.print("请输入密码：");
            String password = scan.nextLine();
            System.out.print("请输入邮箱：");
            String email = scan.nextLine();

            //注册驱动
            /*Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/test01_bookstore";
            connection = DriverManager.getConnection(url, "root", "mysql1234");*/
            connection = connection();
            String sql = "insert into users (username, password, email) value(?, sha(?), ?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, password);
            ps.setString(3, email);
            int len = ps.executeUpdate();
            System.out.println(len>0?"添加成功":"添加失败");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    /*
    使用JDBC实现往图书表中添加1本图书
     */
    @Test
    public void test2() {
        Connection connection = null;
        PreparedStatement pst = null;
        try {
            connection = connection();
            String sql = "insert into books( `title`, `author`, `price`, `sales`, `stock`, `img_path`) " +
                    "values(?, ?, ?, ?, ?, ?)";
            pst = connection.prepareStatement(sql);
            pst.setString(1, "《从入门到放弃》");//1表示第1个?
            pst.setString(2, "柴林燕");//2表示第2个?
            pst.setDouble(3, 88.8);//3表示第3个?
            pst.setInt(4, 0);
            pst.setInt(5, 100);
            pst.setString(6, "upload/books/从入门到放弃.jpg");
            int len = pst.executeUpdate();
            System.out.println(len > 0 ? "添加成功":"添加失败");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(pst != null) {
                try {
                    pst.close();
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

    @Test
    public void test3(){
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            String sql = "select username, password from users where username = ? and password = sha(?)";
            pst = connection.prepareStatement(sql);
            Scanner scan = new Scanner(System.in);
            System.out.print("请输入用户名：");
            String userName = scan.nextLine();
            System.out.print("请输入密码：");
            String password = scan.nextLine();

            pst.setString(1, userName);
            pst.setString(2, password);
            resultSet = pst.executeQuery();

            if(resultSet.next()){
                System.out.println("登录成功");
            }else {
                System.out.println("登录失败。用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(pst != null) {
                try {
                    pst.close();
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

    @Test
    public void test4() throws Exception{
        Connection connection = connection();
        String sql = "select * from books";
        PreparedStatement pst = connection.prepareStatement(sql);
        ResultSet resultSet = pst.executeQuery();
        while (resultSet.next()){
            for (int i = 0; i < 7; i++) {
                System.out.print(resultSet.getObject(i + 1) + "\t");
            }
            System.out.println();
        }

    }

    @Test
    public void test5() throws Exception{
        Connection connection = connection();
        String sql = "select * from books where sales = (select max(sales) from books)";
        PreparedStatement pst = connection.prepareStatement(sql);
        ResultSet resultSet = pst.executeQuery();
        if (resultSet.next()){
            for (int i = 0; i < 7; i++) {
                System.out.print(resultSet.getObject(i + 1) + "\t");
            }
            System.out.println();
        }
        connection.close();
        pst.close();
        resultSet.close();
    }

    @Test
    public void test6() throws Exception{
        Connection connection = connection();
        String sql = "update books set stock = 100 where stock < 10";
        PreparedStatement pst = connection.prepareStatement(sql);
        int len = pst.executeUpdate();
        System.out.println(len>=0 ? "修改成功" : "修改失败");
        connection.close();
        pst.close();
    }

    @Test
    public void test7(){

    }

    public Connection connection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/test01_bookstore";
        Connection connection = DriverManager.getConnection(url, "root", "mysql1234");
        return connection;
    }
}
