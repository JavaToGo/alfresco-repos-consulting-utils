package org.alfresco.consulting.util.reporting_etl.audit;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import javax.sql.DataSource;

import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.BeanNameAware;

public abstract class DBAuditETLHandlerBase extends AuditETLHandlerBase  {
	
	//private static final Log logger = LogFactory.getLog(DBAuditETLHandlerBase.class);
	private boolean enabled=false;
	private String name=this.getClass().getCanonicalName();
	
	
	public void setName(String name) {
		this.name = name;
	}

	private DataSource dataSource;
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	protected Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}


	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	@Override
	abstract public boolean extractAuditEntry(Long entryId, String applicationName,
			String user, long time, Map<String, Serializable> values);

	@Override
	public String getETLHandlerName() {
		return name;
	}

}
