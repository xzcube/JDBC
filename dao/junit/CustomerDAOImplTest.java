package dao.junit;

import bean.Customer;
import dao.CustomerDAOImpl;
import org.junit.jupiter.api.Test;
import util.JDBCUtil;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * @author xzcube
 * @date 2021/1/3 22:02
 */
class CustomerDAOImplTest {

    private CustomerDAOImpl dao = new CustomerDAOImpl();

    @Test
    void insert() {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            Customer customer = new Customer(1, "小刘", "xiaoliu@126.com", new Date(25246248L));
            dao.insert(connection, customer);
            System.out.println("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    @Test
    void deleteById() {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            dao.deleteById(connection, 13);

            System.out.println("删除成功");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    @Test
    void updateById() {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            Customer customer = new Customer(18, "莫扎特", "mozhat@126.com", new Date(3784982374629L));
            dao.updateById(connection, customer);
            System.out.println("修改成功");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    @Test
    void getCustomerById() {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            Customer customer = dao.getCustomerById(connection, 18);
            System.out.println(customer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    @Test
    void getAll() {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            List<Customer> list = dao.getAll(connection);
            list.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    @Test
    void getCount() {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();

            Long count = dao.getCount(connection);
            System.out.println("表中的记录数为：" + count);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }

    @Test
    void getMaxBirth() {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();

            Date birth = dao.getMaxBirth(connection);
            System.out.println("表中的最大生日为：" + birth);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(connection, null);
        }
    }
}
