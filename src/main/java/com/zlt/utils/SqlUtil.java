package com.zlt.utils;

import com.zlt.annotation.Column;
import com.zlt.annotation.Id;
import com.zlt.annotation.ManyToOne;
import com.zlt.annotation.Table;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * 实现自动化的增删改查
 *
 */
public class SqlUtil {

    private static String driver;

    private static String url;

    private static String username;

    private static String password;


    static {
        //需要先给上面4个变量赋值
        // src下面的文件主要通过类加载器去读取
        InputStream resourceAsStream = SqlUtil.class.getClassLoader().getResourceAsStream("db.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        try {
            //加载驱动
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     * @return
     */
    public static Connection getConnection(){
            try {
                return DriverManager.getConnection(url,username,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return null;
    }

    /**
     * 关闭资源
     * @param rs
     * @param pst
     * @param conn
     */
    public static void close(ResultSet rs, PreparedStatement pst, Connection conn){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(pst != null){
            try {
                pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 可以统一的执行增删改
     * @param sql
     * @param params
     * @return
     */
    public static int update(String sql,Object... params){
        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if(params != null && params.length > 0){
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject( i + 1,params[i]);
                }
            }
            return preparedStatement.executeUpdate() ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            SqlUtil.close(null,preparedStatement,connection);
        }
    }

    public static void main(String[] args) {
        /*Department department = new Department(1,"行政部2",null,0);
        int delete = update(department);
        System.out.println(delete);*/
        /*List<Employee> select = select(Employee.class, "select * from employee");
        for (Employee employee : select) {
            System.out.println(employee);
        }*/
        List<Map<String, Object>> select = select("select count(*) c from department");
        select.forEach(e->{
            System.out.println(e);
        });
        /*int i = insertReturnId(department);
        System.out.println(i);
        System.out.println(department);*/
        /*int insert = insert(department);
        System.out.println(insert);*/
        /*Employee employee = new Employee(0,"张三",null);
        int insert = insert(employee);
        System.out.println(insert);*/
    }

    /**
     * 参数是一个对象 自动将这个对象存入到数据库 返回影响行数
     * insert into 表名 (字段名,字段名...) values (?,?...)
     * @param t
     * @param <T>
     * @return
     */
    public static <T> int insert(T t){
        if(t == null){
            return 0;
        }
        //判断类上是否有Table注解
        Class<?> clazz = t.getClass();
        if(!clazz.isAnnotationPresent(Table.class)){
            return 0;
        }

        String tableName = clazz.getAnnotation(Table.class).value();
        if("".equals(tableName)){//注解上没有设置表名 表名就是类名
            tableName = clazz.getSimpleName();
        }

        //根据对象生成sql语句
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder valuesSqlBuilder = new StringBuilder();
        valuesSqlBuilder.append(" ) values ( ");
        List params = new ArrayList();
        sqlBuilder.append("insert into ");
        //拼接表名
        sqlBuilder.append(tableName);
        sqlBuilder.append(" ( ");
        //拼接字段名
        //先获取类中所有的属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Object value = null;//属性的值
            try {
                field.setAccessible(true);//设置属性可以访问
                value= field.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(value != null){//如果值为null就不添加这个属性
                if(field.isAnnotationPresent(Id.class)){
                    String columnName = field.getAnnotation(Id.class).value();
                    if("".equals(columnName)){
                        columnName = field.getName();
                    }
                    sqlBuilder.append(columnName);
                    sqlBuilder.append(",");
                    valuesSqlBuilder.append("?,");
                        params.add(value);
                } else if(field.isAnnotationPresent(Column.class)){
                    String columnName = field.getAnnotation(Column.class).value();
                    if("".equals(columnName)){
                        columnName = field.getName();
                    }
                    sqlBuilder.append(columnName);
                    sqlBuilder.append(",");
                    valuesSqlBuilder.append("?,");
                        params.add(value);
                } else if(field.isAnnotationPresent(ManyToOne.class)){
                    String columnName = field.getAnnotation(ManyToOne.class).value();
                    Class<?> type = field.getType();
                    Field field1 = getIdName(type);//主键属性
                    if("".equals(columnName)){//如果ManyToOne 没有命名就去类中找id的名字
                        columnName = field1.getAnnotation(Id.class).value();//
                        if("".equals(columnName)){
                            columnName = field1.getName();
                        }
                    }
                    sqlBuilder.append(columnName);
                    sqlBuilder.append(",");
                    valuesSqlBuilder.append("?,");
                    //寻找对应的值
                    field1.setAccessible(true);
                    try {
                        // 取属性中主键的值
                        params.add(field1.get(value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        //sql基本拼接完成，但是多了逗号
        sqlBuilder.deleteCharAt(sqlBuilder.length()-1);
        valuesSqlBuilder.deleteCharAt(valuesSqlBuilder.length()-1);
        valuesSqlBuilder.append(")");
        //把两段拼接在一起
        sqlBuilder.append(valuesSqlBuilder);
        System.out.println(sqlBuilder.toString()); //查看最后拼接出的sql语句
        return update(sqlBuilder.toString(),params.toArray());

    }

    /**
     *
     * @param clazz
     * @return
     */
    private static Field getIdName(Class clazz){
        if(clazz.isAnnotationPresent(Table.class)){
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(Id.class)){
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 参数是一个对象 自动将这个对象存入到数据库 返回影响行数
     * 自动生成的主键放入对象中
     * @param t
     * @param <T>
     * @return
     */
    public static <T> int insertReturnId(T t){
        if(t == null){
            return 0;
        }
        //判断类上是否有Table注解
        Class<?> clazz = t.getClass();
        if(!clazz.isAnnotationPresent(Table.class)){
            return 0;
        }

        String tableName = clazz.getAnnotation(Table.class).value();
        if("".equals(tableName)){//注解上没有设置表名 表名就是类名
            tableName = clazz.getSimpleName();
        }

        //根据对象生成sql语句
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder valuesSqlBuilder = new StringBuilder();
        valuesSqlBuilder.append(" ) values ( ");
        List params = new ArrayList();
        sqlBuilder.append("insert into ");
        //拼接表名
        sqlBuilder.append(tableName);
        sqlBuilder.append(" ( ");
        //拼接字段名
        //先获取类中所有的属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Object value = null;//属性的值
            try {
                field.setAccessible(true);//设置属性可以访问
                value= field.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(value != null){//如果值为null就不添加这个属性
                if(field.isAnnotationPresent(Id.class)){
                    String columnName = field.getAnnotation(Id.class).value();
                    if("".equals(columnName)){
                        columnName = field.getName();
                    }
                    sqlBuilder.append(columnName);
                    sqlBuilder.append(",");
                    valuesSqlBuilder.append("?,");
                    params.add(value);
                } else if(field.isAnnotationPresent(Column.class)){
                    String columnName = field.getAnnotation(Column.class).value();
                    if("".equals(columnName)){
                        columnName = field.getName();
                    }
                    sqlBuilder.append(columnName);
                    sqlBuilder.append(",");
                    valuesSqlBuilder.append("?,");
                    params.add(value);
                } else if(field.isAnnotationPresent(ManyToOne.class)){
                    String columnName = field.getAnnotation(ManyToOne.class).value();
                    Class<?> type = field.getType();
                    Field field1 = getIdName(type);//主键属性
                    if("".equals(columnName)){//如果ManyToOne 没有命名就去类中找id的名字
                        columnName = field1.getAnnotation(Id.class).value();//
                        if("".equals(columnName)){
                            columnName = field1.getName();
                        }
                    }
                    sqlBuilder.append(columnName);
                    sqlBuilder.append(",");
                    valuesSqlBuilder.append("?,");
                    //寻找对应的值
                    field1.setAccessible(true);
                    try {
                        // 取属性中主键的值
                        params.add(field1.get(value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        //sql基本拼接完成，但是多了逗号
        sqlBuilder.deleteCharAt(sqlBuilder.length()-1);
        valuesSqlBuilder.deleteCharAt(valuesSqlBuilder.length()-1);
        valuesSqlBuilder.append(")");
        //把两段拼接在一起
        sqlBuilder.append(valuesSqlBuilder);

        Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(sqlBuilder.toString(),PreparedStatement.RETURN_GENERATED_KEYS);
            if(params != null && params.size() > 0){
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject( i + 1,params.get(i));
                }
            }
            int result = preparedStatement.executeUpdate() ;
            //获取生成的id
            rs = preparedStatement.getGeneratedKeys();
            rs.next();
            Field field = getIdName(clazz);
            field.setAccessible(true);
            Class<?> type = field.getType();
            if(type == Integer.class || type == int.class){
                field.set(t,rs.getInt(1));
            } else if(type == Long.class || type == long.class){
                field.set(t,rs.getLong(1));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            SqlUtil.close(rs,preparedStatement,connection);
        }
        return 0;
    }

    /**
     * 参数是一个对象 自动识别对象中的主键根据主键删除 返回影响行数
     * delete from 表名 where 主键=?
     * @param t
     * @param <T>
     * @return
     */
    public static <T> int delete(T t){
        if(t == null){
            return 0;
        }
        //判断类上是否有Table注解
        Class<?> clazz = t.getClass();
        if(!clazz.isAnnotationPresent(Table.class)){
            return 0;
        }

        String tableName = clazz.getAnnotation(Table.class).value();
        if("".equals(tableName)){//注解上没有设置表名 表名就是类名
            tableName = clazz.getSimpleName();
        }

        //根据对象生成sql语句
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("delete from ");
        sqlBuilder.append(tableName);
        sqlBuilder.append(" where ");
        Field field = getIdName(clazz);
        String idName = field.getAnnotation(Id.class).value();
        if("".equals(idName)){
            idName = field.getName();
        }
        sqlBuilder.append(idName);
        sqlBuilder.append(" = ?");
        field.setAccessible(true);
        Object value = null;
        try {
            value = field.get(t);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return update(sqlBuilder.toString(),value);
    }

    /**
     * 参数是一个对象 主键作为where后面的条件 其他属性不为空的就会修改 返回影响行数
     * update 表名 set 字段1=x,... where 主键=x
     * @param t
     * @param <T>
     * @return
     */
    public static <T> int update(T t){
        if(t == null){
            return 0;
        }
        //判断类上是否有Table注解
        Class<?> clazz = t.getClass();
        if(!clazz.isAnnotationPresent(Table.class)){
            return 0;
        }

        String tableName = clazz.getAnnotation(Table.class).value();
        if("".equals(tableName)){//注解上没有设置表名 表名就是类名
            tableName = clazz.getSimpleName();
        }

        //根据对象生成sql语句
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("update ");
        sqlBuilder.append(tableName);
        sqlBuilder.append(" set ");
        List params = new ArrayList();//存放参数
        String idName = null;//主键的名称
        Object idValue = null;//主键的值
        //取出所有的属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {//遍历所有的属性
            field.setAccessible(true);
            //属性的值不为空才需要处理
            Object value = null;
            try {
                value = field.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(value != null){
                if(field.isAnnotationPresent(Id.class)){
                    idName = field.getAnnotation(Id.class).value();
                    if("".equals(idName)){
                        idName = field.getName();
                    }
                    idValue = value;
                } else if(field.isAnnotationPresent(Column.class)){
                    String columnName = field.getAnnotation(Column.class).value();
                    if("".equals(columnName)){
                        columnName = field.getName();
                    }
                    sqlBuilder.append(columnName);
                    sqlBuilder.append("=?,");
                    params.add(value);
                }
            }
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);//去除最后的逗号
        sqlBuilder.append(" where ");
        sqlBuilder.append(idName);
        sqlBuilder.append("=?");
        params.add(idValue);
        return update(sqlBuilder.toString(),params.toArray());
    }

    /**
     * 单表和多表的查询
     * 多表的时候要求所有的属性名是不能一样的
     * @param clazz
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static <T> List<T> select (Class<T> clazz,String sql,Object ... params){
        Connection connection = getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();
        try {
            pst = connection.prepareStatement(sql);
            if(params != null && params.length > 0){
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i + 1,params[i]);
                }
            }
            rs = pst.executeQuery();
            //处理结果集
            while(rs.next()){
                T t = clazz.newInstance();//无参构造创建对象
                list.add(t);
                //给对象的属性赋值
                //先找到查询结果中的所有字段名
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);//第几列的名字
                    //
                    setValue(t,columnName,rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            SqlUtil.close(rs,pst,connection);
        }
        return  list;
    }

    /**
     *
     * @param sql
     * @param params
     * @return
     */
    public static List<Map<String,Object>> select(String sql,Object...params){
        Connection connection = getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String,Object>> list = new ArrayList<>();
        try {
            pst = connection.prepareStatement(sql);
            if(params != null && params.length > 0){
                for (int i = 0; i < params.length; i++) {
                    pst.setObject(i + 1,params[i]);
                }
            }
            rs = pst.executeQuery();
            //处理结果集
            while(rs.next()){
                Map<String,Object> map = new HashMap<>();
                list.add(map);
                //给对象的属性赋值
                //先找到查询结果中的所有字段名
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);//第几列的名字
                    map.put(columnName,rs.getObject(columnName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }  finally {
            SqlUtil.close(rs,pst,connection);
        }
        return  list;
    }

    /**
     * 将数据库中查询的值赋值到对象中
     * @param t
     * @param columnName
     * @param rs
     */
    private static void setValue(Object t,String columnName,ResultSet rs) throws SQLException, IllegalAccessException, InstantiationException {
        Class clazz = t.getClass();
        Field field = getField(clazz,columnName);
        if(field != null){
            field.setAccessible(true);
            Class<?> type = field.getType();
            if(type == Integer.class || type == int.class){
                field.set(t,rs.getInt(columnName));
            } else if(type == Double.class || type == double.class){
                field.set(t,rs.getDouble(columnName));
            } else if(type == Long.class || type == long.class){
                field.set(t,rs.getLong(columnName));
            } else if(type == String.class){
                field.set(t,rs.getString(columnName));
            }
        } else {
            //这个字段可能是ManyToOne标识的属性里面的属性
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                if(declaredField.isAnnotationPresent(ManyToOne.class)){//找到有ManyToOne标识的属性
                    String value = declaredField.getAnnotation(ManyToOne.class).value();
                    Class<?> type = declaredField.getType();
                    //赋值之前先判断这个属性的值是否为null
                    Object o = declaredField.get(t);
                    if(o == null){
                        o = type.newInstance();
                        declaredField.set(t,o);
                    }
                    if(value.equals(columnName)){//表示现在赋值的这个是主键
                        Field idName = getIdName(type);
                        idName.setAccessible(true);
                        //就将值赋值给属性
                        Class<?> type1 = idName.getType();
                        if(type1 == Integer.class || type1 == int.class){
                            idName.set(o,rs.getInt(columnName));
                        } else if(type1 == Double.class || type1 == double.class){
                            idName.set(o,rs.getDouble(columnName));
                        } else if(type1 == Long.class || type1 == long.class){
                            idName.set(o,rs.getLong(columnName));
                        } else if(type1 == String.class){
                            idName.set(o,rs.getString(columnName));
                        }
                    }else{
                        setValue(o,columnName,rs);
                    }
                }
            }
        }
    }

    /**
     * 通过字段名找出对应的Field
     * @param clazz
     * @param name
     * @return
     */
    private static Field getField(Class clazz,String name){
        if(clazz.isAnnotationPresent(Table.class)){
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                String columnName = null;
                if(field.isAnnotationPresent(Id.class))
                    columnName = field.getAnnotation(Id.class).value();
                else if(field.isAnnotationPresent(Column.class))
                    columnName = field.getAnnotation(Column.class).value();
                if("".equals(columnName)){
                    columnName = field.getName();
                }
                //判断名字是否相同
                if(name.equals(columnName)){
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 查询单个数据
     * 如果没有就返回null
     * @param clazz
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static <T> T selectOne(Class<T> clazz,String sql ,Object ... params){
        List<T> select = select(clazz, sql, params);
        return select == null || select.isEmpty() ? null : select.get(0);
    }
}
