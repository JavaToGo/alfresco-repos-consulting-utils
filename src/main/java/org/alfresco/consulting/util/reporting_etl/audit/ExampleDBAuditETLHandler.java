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

public class ExampleDBAuditETLHandler extends DBAuditETLHandlerBase {
	
	private static final Log logger = LogFactory.getLog(ExampleDBAuditETLHandler.class);
	private static final String QUERY="INSERT into audit_hist (orig_id, path, name, info_area, attr_name, old_value, new_value, action, changed_by, changed_time) values (?,?,?,?,?,?,?,?,?,?)";
	private static final String PATH_KEY = "/alfresco-access/transaction/path";
	private static final String ADD_KEY = "/alfresco-access/transaction/properties/add";
	private static final String FROM_KEY = "/alfresco-access/transaction/properties/from";
	private static final String TO_KEY = "/alfresco-access/transaction/properties/to";
	private static final String ADD_PROP_ACTION = "AddProperty";
	private static final String UPDATE_PROP_ACTION = "UpdateProperty";
	private static final String NONE_VALUE="..NONE..";
	@Override
	public boolean extractAuditEntry(Long entryId, String applicationName,
			String user, long time, Map<String, Serializable> values) {
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(QUERY);
			String path = (String) values.get(PATH_KEY);
			pstmt.setLong(1, entryId);
			pstmt.setString(2, path);
			pstmt.setString(3, "Name TBD");
			pstmt.setString(4, "Information Area TBD");
			pstmt.setString(5, NONE_VALUE); // Attr Name
			pstmt.setString(6, NONE_VALUE); // Old Value
			pstmt.setString(7, NONE_VALUE); // New Value
			pstmt.setString(8, NONE_VALUE);
			pstmt.setString(9, user);
			pstmt.setTimestamp(10, new Timestamp(time));
			if (values.containsKey(ADD_KEY)) {
				pstmt.setString(8,ADD_PROP_ACTION);
				Map<QName,Object> addedValues = (Map<QName,Object>) values.get(ADD_KEY);
				for (QName key : addedValues.keySet()) {
					pstmt.setString(5, key.toString());
					pstmt.setString(7, addedValues.get(key).toString());
					pstmt.executeUpdate();
					conn.commit();
				}
			}
			if (values.containsKey(TO_KEY)) {
				pstmt.setString(8,UPDATE_PROP_ACTION);
				Map<QName,Object> toValues = (Map<QName,Object>) values.get(TO_KEY);
				Map<QName,Object> fromValues = (Map<QName,Object>) values.get(FROM_KEY);
				for (QName key : toValues.keySet()) {
					pstmt.setString(5, key.toString());
					pstmt.setString(6, fromValues.get(key).toString());
					pstmt.setString(7, toValues.get(key).toString());
					pstmt.executeUpdate();
					conn.commit();
				}
			}
			if (logger.isDebugEnabled()) {
				JSONObject obj = new JSONObject(values);
				logger.debug(String.format("%s: %d: %s, %s, %d: %s",getETLHandlerName(),entryId,applicationName,user,time,obj.toString()));
			}
		} catch (SQLException e) {
			JSONObject obj = new JSONObject(values);
			logger.error(String.format("ERROR Saving: %s: %d: %s, %s, %d: %s",getETLHandlerName(),entryId,applicationName,user,time,obj.toString()), e);;
		}
		return true;
	}
	

}
