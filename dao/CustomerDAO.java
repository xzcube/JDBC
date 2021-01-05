package dao;

import bean.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**此接口用于规范针对customers表的常用操作
 * @author xzcube
 * @date 2021/1/3 21:35
 */
public interface CustomerDAO {
    /**
     * 将customer对象添加到数据库中
     * @param connection
     * @param customer
     */
    void insert(Connection connection, Customer customer);

    /**
     * 针对指定的id删除一条记录
     * @param connection
     * @param id
     */
    void deleteById(Connection connection, int id);

    /**
     * 针对于内存中的customer对象，去修改表中的指定记录
     * @param connection
     * @param
     */
    void updateById(Connection connection, Customer customer);

    /**
     * 针对指定的id查询得到对应的Customer对象
     * @param connection
     * @param id
     */
    Customer getCustomerById(Connection connection, int id);

    /**
     * 查询表中所有记录构成的集合
     * @param connection
     * @return
     */
    List<Customer> getAll(Connection connection);

    /**
     * 返回数据表中数据的条目数
     * @param connection
     * @return
     */
    Long getCount(Connection connection);

    /**
     * 返回数据表中最大的birth
     * @param connection
     * @return
     */
    Date getMaxBirth(Connection connection);
}
