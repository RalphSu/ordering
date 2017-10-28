package net.samhouse.db.config;

import net.samhouse.db.dao.OrderDao;
import net.samhouse.db.dao.StepDao;
import net.samhouse.db.dao.impl.OrderDaoImpl;
import net.samhouse.db.dao.impl.StepDaoImpl;
import net.samhouse.db.service.OrderService;
import net.samhouse.db.service.StepService;
import net.samhouse.db.service.impl.OrderServiceImpl;
import net.samhouse.db.service.impl.StepServiceImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Java config for db
 */
@Configuration
public class OrderDBConfig {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * DataSource
     *
     * @return DataSource
     */
    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * Used for @Transcational annotation in unit test so far
     * But seems the test data inserted to db desn't get cleaned
     * TODO - need more investigation
     *
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * JdbcTemplate for working with SQL
     *
     * @return JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public OrderDao orderDao() {
        return new OrderDaoImpl();
    }

    @Bean
    public StepDao stepDao() {
        return new StepDaoImpl();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl();
    }

    @Bean
    public StepService stepService() {
        return new StepServiceImpl();
    }
}
