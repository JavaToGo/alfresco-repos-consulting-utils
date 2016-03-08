package org.alfresco.consulting.util.dynamic_constraints.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.alfresco.consulting.util.ConsultingUtilsConstants;
import org.alfresco.consulting.util.dynamic_constraints.DynamicConstraintService;
import org.alfresco.consulting.util.folder_hierarchy.FolderHierarchyHelper;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

public class DynamicConstraintServiceNodeImpl extends AbstractLifecycleBean implements
		DynamicConstraintService  {
	ServiceRegistry serviceRegistry;
	NodeService nodeService;
	NodeLocatorService nodeLocatorService;
	FolderHierarchyHelper folderHierarchyHelper;
	NodeRef dcRoot;
	private NamespaceService namespaceService;
	
    NodeRef getDcRoot() {
		if (dcRoot==null) {
			dcRoot = AuthenticationUtil.runAsSystem(new RunAsWork<NodeRef>() {
	
				@Override
				public NodeRef doWork() throws Exception {
					List<String> folders = Arrays.asList(new String[] {"Data Dictionary","org.alfresco.consulting","Dynamic Constraints"});
					return folderHierarchyHelper.getFolder(nodeLocatorService.getNode(CompanyHomeNodeLocator.NAME, null, null),folders,true);
				} });
		}
		return dcRoot;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.nodeService = this.serviceRegistry.getNodeService();
		this.nodeLocatorService = this.serviceRegistry.getNodeLocatorService();
		this.namespaceService = this.serviceRegistry.getNamespaceService();
	}
	public void setFolderHierarchyHelper(FolderHierarchyHelper folderHierarchyHelper) {
		this.folderHierarchyHelper = folderHierarchyHelper;
	}
	private String genCkSumString(String value) {

		CRC32 crc = new CRC32();
		crc.update(value.getBytes());
		return Long.toHexString(crc.getValue()) +"-"+value.length();
	}
	private String genCkSumString(QName value) {
		return genCkSumString(value.toPrefixString());
	}
	private String genCkSumString(Serializable value) {
		return genCkSumString(value.toString());
	}
	private NodeRef getConstraintValueRef(QName constraintName, Serializable constraintValue) {
		return nodeService.getChildByName(getConstraint(constraintName), ConsultingUtilsConstants.ASSOC_CONSTRAINT,genCkSumString(constraintValue) );
	}
	@Override
	public void createListConstraint(QName constraintName, QName dataType) {
		if (DataTypeDefinition.TEXT.equals(dataType)) {
			NodeRef constraintContainer = nodeService.createNode(getDcRoot(), ContentModel.ASSOC_CONTAINS, constraintName, ConsultingUtilsConstants.TYPE_CONSTRAINT_CONTAINER).getChildRef();
			nodeService.setProperty(constraintContainer, ContentModel.PROP_NAME, constraintName.toPrefixString(namespaceService));
			nodeService.setProperty(constraintContainer, ConsultingUtilsConstants.PROP_VALUE_PROPERTY, ConsultingUtilsConstants.PROP_STRING_VALUE);
		} else {
			throw new DynamicConstraintException("Type Not Supported");
		}
	}
	
	private NodeRef getConstraint(QName constraintName) {
		String nameAsString = constraintName.toPrefixString(namespaceService);
		return nodeService.getChildByName(getDcRoot(), ContentModel.ASSOC_CONTAINS, nameAsString);
	}

	@Override
	public boolean listConstraintExists(QName constraintName) {
		return (null != getConstraint(constraintName));
	}

	@Override
	public void addValueToList(QName constraintName, Serializable value) {
		addValueToList(constraintName,value,value.toString(),true);
	}

	@Override
	public void addValueToList(QName constraintName, Serializable value,
			String displayValue) {
		addValueToList(constraintName,value,displayValue,true);
	}

	@Override
	public void addValueToList(QName constraintName, Serializable value,
			String displayValue, boolean enabled) {
		String sVal = value.toString();
		String cksum = genCkSumString(value);
		NodeRef container = getConstraint(constraintName);
		NodeRef valRef = nodeService.createNode(container, ConsultingUtilsConstants.ASSOC_CONSTRAINT, QName.createQName(ConsultingUtilsConstants.CONSULTING_UTILS_MODEL_1_0_URI, cksum), ConsultingUtilsConstants.TYPE_CONSTRAINT_VALUE).getChildRef();
		nodeService.setProperty(valRef, ConsultingUtilsConstants.PROP_DISPLAY_VALUE, displayValue);
		nodeService.setProperty(valRef, ConsultingUtilsConstants.PROP_ACTIVE, enabled);
		nodeService.setProperty(valRef, ContentModel.PROP_NAME, cksum);
		QName valProp = (QName) nodeService.getProperty(container, ConsultingUtilsConstants.PROP_VALUE_PROPERTY);
		nodeService.setProperty(valRef, valProp, value);
	}

	@Override
	public void addValueToList(QName constraintName, Serializable value,
			boolean enabled) {
		addValueToList(constraintName,value,value.toString(),enabled);
	}

	@Override
	public void removeValueFromList(QName constraintName, Serializable value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableValue(QName constraintName, Serializable value) {
		NodeRef valRef = getConstraintValueRef(constraintName, value);
		nodeService.setProperty(valRef, ConsultingUtilsConstants.PROP_ACTIVE, false);
	}

	@Override
	public void enableValue(QName constraintName, Serializable value) {
		NodeRef valRef = getConstraintValueRef(constraintName, value);
		nodeService.setProperty(valRef, ConsultingUtilsConstants.PROP_ACTIVE, true);
	}

	@Override
	public String getValueFromList(QName constraintName, Serializable value) {
		NodeRef valRef =getConstraintValueRef(constraintName, value);
		if (valRef == null) { return null; }
		QName valProp = (QName) nodeService.getProperty(getConstraint(constraintName), ConsultingUtilsConstants.PROP_VALUE_PROPERTY);
		return (String) nodeService.getProperty(valRef, valProp);
	}

	@Override
	public void validateDynamicConstraint(QName constraintName,
			String constraintValue) {
		if (null == getValueFromList(constraintName,constraintValue)) {
			throw new DynamicConstraintException(constraintValue + " not a valid value for " + constraintName.toPrefixString());
		}

	}

	@Override
	public boolean valueExists(QName constraintName, String constraintValue) {
		// TODO Auto-generated method stub
		return (null != getValueFromList(constraintName,constraintValue));
	}

	@Override
	public boolean valueEnabled(QName constraintName, String constraintValue) {
		if (!valueExists(constraintName, constraintValue)) {
			return false;
		}
		return (Boolean) nodeService.getProperty(getConstraintValueRef(constraintName, constraintValue) , ConsultingUtilsConstants.PROP_ACTIVE);
	}

	@Override
	public Map<Serializable, String> getListConstraintMap(QName constraintName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getListConstraintJSON(QName constraintName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void onBootstrap(ApplicationEvent event) {
		//Create the Project Root -- Must run as system b/c CompanyHome must be accessed via a user 
		getDcRoot();
	}
	@Override
	protected void onShutdown(ApplicationEvent event) {
		// TODO Auto-generated method stub
		
	}

}
