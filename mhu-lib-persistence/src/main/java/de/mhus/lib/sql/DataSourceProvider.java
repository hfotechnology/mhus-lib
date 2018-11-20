/**
 * Copyright 2018 Mike Hummel
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
package de.mhus.lib.sql;

import java.sql.Connection;

import javax.sql.DataSource;

import de.mhus.lib.core.MActivator;
import de.mhus.lib.core.config.IConfig;

/**
 * <p>DataSourceProvider class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class DataSourceProvider extends DbProvider {

	private DataSource dataSource;
	private Dialect dialect;

	/**
	 * <p>Constructor for DataSourceProvider.</p>
	 */
	public DataSourceProvider() {}

	/**
	 * <p>Constructor for DataSourceProvider.</p>
	 *
	 * @param dataSource a {@link javax.sql.DataSource} object.
	 * @param dialect a {@link de.mhus.lib.sql.Dialect} object.
	 * @param config a {@link de.mhus.lib.core.directory.ResourceNode} object.
	 * @param activator a {@link de.mhus.lib.core.MActivator} object.
	 */
	public DataSourceProvider(DataSource dataSource, Dialect dialect, IConfig config, MActivator activator) {
		doInitialize(config, activator);
		setDataSource(dataSource);
		setDialect(dialect);
	}

	/** {@inheritDoc} */
	@Override
	public InternalDbConnection createConnection() throws Exception {
		if (dataSource == null) return null;
		Connection con = null;
		try {
			con = dataSource.getConnection();
		} catch (Exception e) {
			// special behavior for e.g. mysql, retry to get a connection after gc()
			// Caused by: com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: Too many connections
			if (e.getMessage() != null && e.getMessage().indexOf("Too many connections") > -1) {
				System.gc();
				con = dataSource.getConnection();
			} else
				throw e;
		}
		getDialect().prepareConnection(con);
		return new JdbcConnection(this, con);
	}

	/** {@inheritDoc} */
	@Override
	public Dialect getDialect() {
		if (dialect == null)
			// default is mysql, can ignore sync!
			dialect = new DialectMysql();
		return dialect;
	}

	/**
	 * <p>Getter for the field <code>dataSource</code>.</p>
	 *
	 * @return a {@link javax.sql.DataSource} object.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * <p>Setter for the field <code>dataSource</code>.</p>
	 *
	 * @param dataSource a {@link javax.sql.DataSource} object.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * <p>Setter for the field <code>dialect</code>.</p>
	 *
	 * @param dialect a {@link de.mhus.lib.sql.Dialect} object.
	 */
	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

}
