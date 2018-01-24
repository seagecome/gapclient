package com.ethan.gapclient.dal.config.slave;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@MapperScan(basePackages=SlaveGapDataSourceConfig.BASEPACKAGES,sqlSessionFactoryRef="slaveSqlSessionFactory")
public class SlaveGapDataSourceConfig {
	protected static final String BASEPACKAGES = "com.ethan.gapclient.dal.dao.slave";
	protected static final String MAPPER_LOCATION = "classpath:com/ethan/gapclient/dal/dao/slave/*.xml";

	@Value("${gap.slave.datasource.url}")
	private String url;
	@Value("${gap.slave.datasource.username}")
	private String userName;
	@Value("${gap.slave.datasource.password}")
	private String password;
	@Value("${gap.slave.datasource.driverClassName}")
	private String driverClass;
	
	@Bean(name="slaveDataSource")
	@Primary
	public DataSource slaveDataSource(){
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driverClass);
		return dataSource;
	}
	
	@Bean(name="slaveTransactionManager")
	@Primary
	public DataSourceTransactionManager slaveTransactionManager(){
		return new DataSourceTransactionManager(slaveDataSource());
	}
	
	@Bean(name="slaveSqlSessionFactory")
	@Primary
	public SqlSessionFactory slaveSqlSessionFactory(@Qualifier("slaveDataSource")DataSource slaveDataSource) throws Exception{
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(slaveDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
        	.getResources(SlaveGapDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
	}
}
