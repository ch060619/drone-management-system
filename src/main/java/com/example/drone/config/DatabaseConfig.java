package com.example.drone.config;

import com.example.drone.exception.DatabaseConnectionException;
import com.example.drone.repository.adapter.MySQLAdapter;
import com.example.drone.repository.adapter.SQLiteAdapter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 数据库配置类.
 * <p>
 * 根据配置文件中的 database.type 属性，自动选择并配置对应的数据库适配器。
 * 支持 SQLite 和 MySQL 数据库切换，使用工厂模式 + 策略模式实现。
 * </p>
 * <p>
 * 配置内容包括：
 * <ul>
 *   <li>数据源（DataSource）：通过 DatabaseAdapter 获取</li>
 *   <li>MyBatis SqlSessionFactory：配置 Mapper XML 文件位置</li>
 *   <li>事务管理器（TransactionManager）：支持声明式事务</li>
 * </ul>
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.example.drone.repository")
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    /**
     * 数据库类型配置.
     * <p>
     * 从配置文件中读取 database.type 属性。
     * 有效值：sqlite、mysql
     * </p>
     */
    @Value("${database.type}")
    private String databaseType;

    /**
     * MyBatis Mapper XML 文件位置.
     * <p>
     * 从配置文件中读取 mybatis.mapper-locations 属性。
     * SQLite：classpath:mapper/sqlite/*.xml
     * MySQL：classpath:mapper/mysql/*.xml
     * </p>
     */
    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;

    /**
     * SQLite 数据库文件路径.
     */
    @Value("${database.sqlite.path:./data/drone.db}")
    private String sqlitePath;

    /**
     * MySQL 数据库连接配置.
     */
    @Value("${database.mysql.url:}")
    private String mysqlUrl;

    @Value("${database.mysql.username:}")
    private String mysqlUsername;

    @Value("${database.mysql.password:}")
    private String mysqlPassword;

    @Value("${database.mysql.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String mysqlDriverClassName;

    /**
     * 配置数据源 Bean.
     * <p>
     * 根据数据库类型创建对应的适配器并获取数据源。
     * SQLite 使用 SQLiteDataSource，MySQL 使用 Druid 连接池。
     * </p>
     *
     * @return 数据源对象
     */
    @Bean
    public DataSource dataSource() {
        logger.info("配置数据源，数据库类型: {}", databaseType);

        try {
            DataSource dataSource;
            if ("sqlite".equalsIgnoreCase(databaseType)) {
                SQLiteAdapter adapter = new SQLiteAdapter(sqlitePath);
                dataSource = adapter.getDataSource();
            } else if ("mysql".equalsIgnoreCase(databaseType)) {
                MySQLAdapter adapter = new MySQLAdapter(mysqlUrl, mysqlUsername, mysqlPassword, mysqlDriverClassName);
                dataSource = adapter.getDataSource();
            } else {
                logger.error("不支持的数据库类型: {}", databaseType);
                throw new DatabaseConnectionException("不支持的数据库类型: " + databaseType
                        + "，支持的类型: sqlite, mysql");
            }

            logger.info("数据源配置完成，数据库类型: {}", databaseType);
            return dataSource;
        } catch (DatabaseConnectionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("数据源初始化失败，数据库类型: {}", databaseType, e);
            throw new DatabaseConnectionException("数据库初始化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 配置 MyBatis SqlSessionFactory.
     * <p>
     * 设置数据源和 Mapper XML 文件位置。
     * 根据数据库类型加载对应目录下的 Mapper 文件。
     * </p>
     *
     * @param dataSource 数据源对象
     * @return SqlSessionFactory 对象
     * @throws Exception 配置失败时抛出异常
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        logger.info("配置 MyBatis SqlSessionFactory，Mapper 文件位置: {}", mapperLocations);

        try {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource);

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sessionFactory.setMapperLocations(resolver.getResources(mapperLocations));

            sessionFactory.setTypeAliasesPackage("com.example.drone.domain.entity");

            logger.info("MyBatis SqlSessionFactory 配置完成");
            return sessionFactory.getObject();
        } catch (Exception e) {
            logger.error("MyBatis SqlSessionFactory 配置失败，Mapper 位置: {}", mapperLocations, e);
            throw new DatabaseConnectionException("MyBatis 配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 配置事务管理器.
     * <p>
     * 使用 DataSourceTransactionManager 管理事务。
     * 支持 @Transactional 注解的声明式事务。
     * </p>
     *
     * @param dataSource 数据源对象
     * @return 事务管理器对象
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        logger.info("配置事务管理器");
        return new DataSourceTransactionManager(dataSource);
    }
}