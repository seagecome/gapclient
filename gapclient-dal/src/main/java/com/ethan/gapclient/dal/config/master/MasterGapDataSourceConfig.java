package com.ethan.gapclient.dal.config.master;

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
@MapperScan(basePackages=MasterGapDataSourceConfig.BASEPACKAGES,sqlSessionFactoryRef="masterSqlSessionFactory")
public class MasterGapDataSourceConfig {
	protected static final String BASEPACKAGES = "com.ethan.gapclient.dal.dao.master";
	protected static final String MAPPER_LOCATION = "classpath:com/ethan/gapclient/dal/dao/master/*.xml";

	@Value("${gap.master.datasource.url}")
	private String url;
	@Value("${gap.master.datasource.username}")
	private String userName;
	@Value("${gap.master.datasource.password}")
	private String password;
	@Value("${gap.master.datasource.driverClassName}")
	private String driverClass;
	
	@Bean(name="masterDataSource")
	@Primary
	public DataSource masterDataSource(){
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driverClass);
		return dataSource;
	}
	
	@Bean(name="masterTransactionManager")
	@Primary
	public DataSourceTransactionManager masterTransactionManager(){
		return new DataSourceTransactionManager(masterDataSource());
	}
	
	@Bean(name="masterSqlSessionFactory")
	@Primary
	public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource")DataSource masterDataSource) throws Exception{
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
        	.getResources(MasterGapDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
	}
}
