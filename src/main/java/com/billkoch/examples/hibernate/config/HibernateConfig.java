/*
 * Copyright 2016 Bill Koch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.billkoch.examples.hibernate.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.billkoch.examples.hibernate.HibernateTenantContextResolver;
import com.billkoch.examples.hibernate.SchemaBasedTenantConnectionProvider;

@Configuration
public class HibernateConfig {

	@Autowired
	private EntityManagerFactoryBuilder builder;

	@Autowired
	private JpaProperties jpaProperties;

	@Value("${tenants.datasource.schema-selection-query}")
	private String schemaSelectionQuery;

	@Bean
	@ConfigurationProperties(prefix = "tenants.datasource")
	public DataSource tenantsDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		Map<String, Object> properties = new HashMap<>();
		properties.putAll(jpaProperties.getHibernateProperties(tenantsDataSource()));

		properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
		properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider());
		properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver());

		return builder
				.dataSource(tenantsDataSource())
				.packages("com.billkoch.examples")
				.properties(properties).build();
	}

	@Bean
	public SchemaBasedTenantConnectionProvider connectionProvider() {
		return new SchemaBasedTenantConnectionProvider(tenantsDataSource(), schemaSelectionQuery);
	}

	@Bean
	public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
		return new HibernateTenantContextResolver();
	}
}
