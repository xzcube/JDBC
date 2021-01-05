package daoplus;

import util.JDBCUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装了针对数据表的通用操作
 * @author xzcube
 * @date 2021/1/3 21:23
 */
public abstract class BaseDAO<T> {
    private Class<T> clazz;

    //获取当前BaseDAO的子类继承的父类的泛型
    {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

        Type[] typeArguments = parameterizedType.getActualTypeArguments(); //获取父类的泛型参数
        clazz = (Class<T>) typeArguments[0];// 从一个泛型类型中获取第一个泛型参数的类型类
    }

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


    //通用的查询操作，用于返回数据表中的一条记录（考虑事务）
    public T getInstance(Connection connection, String sql, Object ...args){
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


    //针对多条数据的通用的查询方法   （考虑上事务）
    public List<T> getForList(Connection connection, String sql, Object ...args){
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
            JDBCUtil.closeResource(null, ps, rs);
        }
        return null;
    }


    //用于查询特殊值的通用方法（考虑事务上的）
    public <E> E getValue(Connection connection, String sql, Object ...args) {
        PreparedStatement pst = null;
        ResultSet resultSet = null;
        try {
            pst = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pst.setObject(i + 1, args[i]);
            }

            resultSet = pst.executeQuery();
            if(resultSet.next()){
                return (E)resultSet.getObject(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(null, pst, resultSet);
        }
        return null;
    }
}
