package io.ibax.config;

import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

@Configuration
public class DruidConfig {
	
	@ConfigurationProperties(prefix="spring.datasource")
	@Bean
	public DataSource druidDataSource() {
		
		 return new DruidDataSource(); 
	}
	
	
	@Bean 
	public ServletRegistrationBean statViewServlet(){
	  
		  ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*"); 
		  HashMap<String,String> map = new HashMap<String, String>(); 
		  map.put("loginUsername", "admin");
		  map.put("loginPassword", "123456"); 
		  map.put("allow", ""); 
		  map.put("deny","182.168.0.1");
		  
		  bean.setInitParameters(map);
		  
		  return bean;
	  
	 }
	  
		
	@Bean 
	public FilterRegistrationBean<Filter> webStatFilter(){
		  FilterRegistrationBean bean = new FilterRegistrationBean();
		  bean.setFilter(new WebStatFilter());
		  
		  HashMap<String,String> map = new HashMap<String, String>();
		  map.put("exclusions", "*.js,*.css.,/druid/*"); bean.setInitParameters(map);
		  
		  bean.setUrlPatterns(Arrays.asList("/*"));
		  
		  return bean; 
	}
		 
}
