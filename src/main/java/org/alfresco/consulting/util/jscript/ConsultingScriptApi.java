package org.alfresco.consulting.util.jscript;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.consulting.util.dynamic_constraints.DynamicConstraintService;
import org.alfresco.consulting.util.unique_property.UniquePropertyManager;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttributeService.AttributeQueryCallback;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.io.IOUtils;

public class ConsultingScriptApi extends BaseScopableProcessorExtension {
	private ServiceRegistry serviceRegistry;
	private DictionaryService dictionaryService;
	private UniquePropertyManager uniquePropertyManager;
	private AttributeService attributeService;
	private NamespaceService namespaceService;

	private DynamicConstraintService dynamicConstraintService;
	
	private QName resolveToQName(String qname) {
		return QName.resolveToQName(namespaceService, qname);
	}
	
	public void setDynamicConstraintService(
			DynamicConstraintService dynamicConstraintService) {
		this.dynamicConstraintService = dynamicConstraintService;
	}
	// Dynamic Constraint Methods
	public void createListConstraint(String constraintName,String dataType) {
		dynamicConstraintService.createListConstraint(resolveToQName( constraintName), resolveToQName( dataType));
	}
	public boolean listConstraintExists(String constraintName) {
		return dynamicConstraintService.listConstraintExists(resolveToQName(constraintName));
	}
	public void addValueToList(String constraintName,Serializable value) {
		dynamicConstraintService.addValueToList(resolveToQName(constraintName), value);
	}
	public void addValueToList(String constraintName,Serializable value,String displayValue) {
		dynamicConstraintService.addValueToList(resolveToQName(constraintName), value, displayValue);
	}
	public void addValueToList(String constraintName,Serializable value,String displayValue, boolean enabled) {
		dynamicConstraintService.addValueToList(resolveToQName(constraintName), value, displayValue, enabled);
	}
	public void addValueToList(String constraintName,Serializable value, boolean enabled) {
		dynamicConstraintService.addValueToList(resolveToQName(constraintName), value, enabled);
	}
	public void removeValueFromList(String constraintName,Serializable value) {
		dynamicConstraintService.removeValueFromList(resolveToQName(constraintName), value);
	}
	public void disableValue(String constraintName,Serializable value) {
		dynamicConstraintService.disableValue(resolveToQName(constraintName), value);
	}
	public void enableValue(String constraintName,Serializable value) {
		dynamicConstraintService.enableValue(resolveToQName(constraintName), value);	
	}
	public String getValueFromList(String constraintName,Serializable value) {
		return 	dynamicConstraintService.getValueFromList(resolveToQName(constraintName), value);
	}
	public void validateDynamicConstraint(String constraintName,String constraintValue) {
		dynamicConstraintService.validateDynamicConstraint(resolveToQName(constraintName), constraintValue);			
	}
	public boolean valueExists(String constraintName,String constraintValue) {
		return dynamicConstraintService.valueExists(resolveToQName(constraintName), constraintValue);	
	}
	public boolean valueEnabled(String constraintName,String constraintValue) {
		return dynamicConstraintService.valueEnabled(resolveToQName(constraintName), constraintValue);	
	}
	/*
	public Map<Serializable,String> getListConstraintMap(QName constraintName) {
		
	}
	public String getListConstraintJSON(QName constraintName) {
		
	}*/

	//TODO: Add Readme
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.dictionaryService = this.serviceRegistry.getDictionaryService();
		this.attributeService = this.serviceRegistry.getAttributeService();
		this.namespaceService = this.serviceRegistry.getNamespaceService();
	}
    public String getResourceContent(String path)  {
        InputStream is =this.getClass().getResourceAsStream(path);
        String content = "";
        try {
                content = IOUtils.toString(is);
        } catch (IOException e) {
                throw new RuntimeException(e);
        }
        return content;
	}
	
	public String getReadmeContent(String moduleId) {
	        return getResourceContent("/alfresco/module/" + moduleId + "/README.html");
	}

	public void setUniquePropertyManager(UniquePropertyManager uniquePropertyManager) {
		this.uniquePropertyManager = uniquePropertyManager;
	}

	public ScriptNode getGlobalNodeById(QName propertyName,Serializable id) {
		PropertyDefinition pd = dictionaryService.getProperty(propertyName);
		Serializable val = id;
		if (pd.getDataType().equals(DataTypeDefinition.BOOLEAN)) {
			val = (Boolean) id;
		}
		else if (pd.getDataType().equals(DataTypeDefinition.LONG)) {
			val = (Long) id;
		}
		else if (pd.getDataType().equals(DataTypeDefinition.INT)) {
			val = (Integer) id;
		}
		else if (pd.getDataType().equals(DataTypeDefinition.DOUBLE)) {
			val = (Double) id;
		}
		else if (pd.getDataType().equals(DataTypeDefinition.FLOAT)) {
			val = (Float) id;
		}
		else if (pd.getDataType().equals(DataTypeDefinition.TEXT)) {
			val = (String) id;
		}
		else if (pd.getDataType().equals(DataTypeDefinition.MLTEXT)) {
			val = (String) id;
		}
		else if (pd.getDataType().equals(DataTypeDefinition.DATE)) {
			val = (Date) id;
		}
		else if (pd.getDataType().equals(DataTypeDefinition.DATETIME)) {
			val = (Date) id;
		}
		NodeRef nodeRef = uniquePropertyManager.getGlobalNodeById(propertyName, val);
		if (nodeRef == null) return null;
		
		return new ScriptNode(nodeRef, this.serviceRegistry, getScope());
	}
    public String listAttributes(final long start,final long length,String top) {
        Serializable keys[] = new Serializable[1];
        keys[0]=top;
        final Map<Long,Map<String,Serializable>> ret = new HashMap<Long,Map<String,Serializable>>();
        AttributeQueryCallback cb = new AttributeQueryCallback() {
                int count=0;

                @Override
                public boolean handleAttribute(Long id, Serializable value, Serializable[] keys) {
                        // TODO Auto-generated method stub
                        if (count >= (start+length)) {
                                return false;
                        }
                        if (count >= start) {
                                Map<String,Serializable> obj = new HashMap<String,Serializable>();
                                obj.put("KEY-0", keys[0]);
                                obj.put("KEY-1", keys[1]);
                                obj.put("KEY-2", keys[2]);
                                obj.put("VALUE", value);
                                ret.put(id, obj);
                        }
                        count++;
                        return true;
                }

        };
        attributeService.getAttributes(cb, keys);
        JSONObject jobj = new JSONObject(ret);
        try {
                return  jobj.toString(4);
        } catch (JSONException e) {
                return jobj.toString();
        }
}

	
}
