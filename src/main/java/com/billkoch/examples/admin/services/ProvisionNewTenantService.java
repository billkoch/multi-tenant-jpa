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
package com.billkoch.examples.admin.services;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProvisionNewTenantService {

	private final JdbcTemplate adminJdbcTemplate;

	private final DataSource tenantDataSource;

	private final String tenantsDataSourceUsername;

	public ProvisionNewTenantService(JdbcTemplate adminJdbcTemplate, DataSource tenantDataSource, @Value("${tenants.datasource.username}") String tenantsDataSourceUsername) {
		this.adminJdbcTemplate = adminJdbcTemplate;
		this.tenantDataSource = tenantDataSource;
		this.tenantsDataSourceUsername = tenantsDataSourceUsername;
	}

	public void createNewSchema(String tenant) {
		adminJdbcTemplate.execute("create database " + tenant);
		adminJdbcTemplate.execute("grant select, insert, update, delete on " + tenant + ".* to " + tenantsDataSourceUsername);
	}

	public void runMigrationsForTenant(String tenant) {
		Flyway flyway = new Flyway();
		flyway.setDataSource(tenantDataSource);
		flyway.setSchemas(tenant);
		flyway.setLocations("tenants/db/migration");
		int numberOfAppliedMigrations = flyway.migrate();
		log.info("Successfully applied {} migration(s) for tenant {}!", numberOfAppliedMigrations, tenant);
	}

	public String getTenantMigrationVersion(String tenant) {
		Flyway flyway = new Flyway();
		flyway.setDataSource(tenantDataSource);
		flyway.setSchemas(tenant);
		return flyway.getBaselineVersion().getVersion();
	}
}
