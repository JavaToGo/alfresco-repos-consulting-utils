package org.alfresco.consulting.util.unique_property;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.consulting.util.unique_property.UniquePropertyRegistry.UniquePropertyRegistryListener;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UniquePropertyContentPolicyFactory implements UniquePropertyRegistryListener {
	
    private static Log logger = LogFactory.getLog(UniquePropertyContentPolicyFactory.class);
	private UniquePropertyRegistry uniquePropertyRegistry;
	private UniquePropertyHandler uniquePropertyHandler;
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ServiceRegistry serviceRegistry;
    
    
    
    public void setUniquePropertyRegistry(
			UniquePropertyRegistry uniquePropertyRegistry) {
		this.uniquePropertyRegistry = uniquePropertyRegistry;
	}

	public void setUniquePropertyHandler(UniquePropertyHandler uniquePropertyHandler) {
		this.uniquePropertyHandler = uniquePropertyHandler;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.nodeService = this.serviceRegistry.getNodeService();
	}

	class TargetPolicy implements 
    	NodeServicePolicies.OnAddAspectPolicy, NodeServicePolicies.OnRemoveAspectPolicy, NodeServicePolicies.OnUpdateNodePolicy, 
    	NodeServicePolicies.OnMoveNodePolicy, NodeServicePolicies.OnCreateNodePolicy,  NodeServicePolicies.OnUpdatePropertiesPolicy,  
    	NodeServicePolicies.OnDeleteNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy {
    	UniquePropertyContentPolicyFactory upcp;
    	QName containedIn;

    	TargetPolicy(UniquePropertyContentPolicyFactory upcp,QName containedIn) {
    		this.upcp = upcp;
    		this.containedIn = containedIn;
    	}
		@Override
		public void beforeDeleteNode(NodeRef nodeRef) {
			upcp.beforeDeleteNode(containedIn,nodeRef);
		}

		@Override
		public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
			upcp.onDeleteNode(containedIn,childAssocRef, isNodeArchived);
		}

		@Override
		public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
			upcp.onUpdateProperties(containedIn,nodeRef, before, after);
		}

		@Override
		public void onCreateNode(ChildAssociationRef childAssocRef) {
			upcp.onCreateNode(containedIn,childAssocRef);
		}

		@Override
		public void onMoveNode(ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef) {
			upcp.onMoveNode(containedIn,oldChildAssocRef, newChildAssocRef);
		}

		@Override
		public void onUpdateNode(NodeRef nodeRef) {
			upcp.onUpdateNode(containedIn,nodeRef);
		}

		@Override
		public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName) {
			upcp.onRemoveAspect(containedIn,nodeRef, aspectTypeQName);
		}

		@Override
		public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
			upcp.onAddAspect(containedIn,nodeRef, aspectTypeQName);
		}
    	
    }
	
	protected void registerAspectInt(QName aspectName) {
		   TargetPolicy tp = new TargetPolicy(this,aspectName);
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                 aspectName,
                 new JavaBehaviour(tp, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"),
                 aspectName,
                 new JavaBehaviour(tp, "onUpdateNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"),
                 aspectName,
                 new JavaBehaviour(tp, "onDeleteNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onMoveNode"),
                 aspectName,
                 new JavaBehaviour(tp, "onMoveNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                 aspectName,
                 new JavaBehaviour(tp, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
	       this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"),
                 aspectName,
                 new JavaBehaviour(tp, "beforeDeleteNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                 aspectName,
                 new JavaBehaviour(tp, "onAddAspect", NotificationFrequency.TRANSACTION_COMMIT));
	       this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onRemoveAspect"),
                 aspectName,
                 new JavaBehaviour(tp, "onRemoveAspect", NotificationFrequency.TRANSACTION_COMMIT));
	}

	protected void registerTypeInt(QName typeName) {
		   TargetPolicy tp = new TargetPolicy(this,typeName);
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                 typeName,
                 new JavaBehaviour(tp, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"),
                 typeName,
                 new JavaBehaviour(tp, "onUpdateNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"),
                 typeName,
                 new JavaBehaviour(tp, "onDeleteNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onMoveNode"),
                 typeName,
                 new JavaBehaviour(tp, "onMoveNode", NotificationFrequency.TRANSACTION_COMMIT));
		   this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                 typeName,
                 new JavaBehaviour(tp, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
	       this.policyComponent.bindClassBehaviour(
                 QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"),
                 typeName,
                 new JavaBehaviour(tp, "beforeDeleteNode", NotificationFrequency.TRANSACTION_COMMIT));
	}

	public void initialise() {
		//TODO: Is this thread safe?
	   for (QName typeName : uniquePropertyRegistry.getTypes()) {
		   registerTypeInt(typeName);
	   }
	   for (QName aspectName : uniquePropertyRegistry.getAspects()) {
		   registerAspectInt(aspectName);
	   }
	   uniquePropertyRegistry.addListener(this);
       logger.debug("INITIALISED");
    }
	    
	public void beforeDeleteNode(QName containedIn, NodeRef nodeRef) {
		for (QName propName : uniquePropertyRegistry.getObjectProperties(containedIn)) {
			uniquePropertyHandler.onDeleteProperty(propName, nodeService.getProperty(nodeRef, propName), nodeService.getPrimaryParent(nodeRef).getParentRef(), nodeRef);
		}
	}

	public void onDeleteNode(QName containedIn, ChildAssociationRef childAssocRef,
			boolean isNodeArchived) {
		// TODO Auto-generated method stub
		
	}

	public void onUpdateProperties(QName containedIn, NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		for (QName propName : uniquePropertyRegistry.getObjectProperties(containedIn)) {
			uniquePropertyHandler.onUpdateProperty(propName, before.get(propName), after.get(propName), nodeService.getPrimaryParent(nodeRef).getParentRef(), nodeService.getPrimaryParent(nodeRef).getParentRef(), nodeRef);
		}
	}

	public void onCreateNode(QName containedIn, ChildAssociationRef childAssocRef) {
		for (QName propName : uniquePropertyRegistry.getObjectProperties(containedIn)) {
			NodeRef nodeRef = childAssocRef.getChildRef();
			//NodeRef parentRef = childAssocRef.getParentRef(); not sure if we should use this instead
			uniquePropertyHandler.onDeleteProperty(propName, nodeService.getProperty(nodeRef, propName), nodeService.getPrimaryParent(nodeRef).getParentRef(), nodeRef);
		}
	}

	public void onMoveNode(QName containedIn, ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef) {
		NodeRef nodeRef = oldChildAssocRef.getChildRef();
		for (QName propName : uniquePropertyRegistry.getObjectProperties(containedIn)) {
			uniquePropertyHandler.onUpdateProperty(propName, nodeService.getProperty(nodeRef, propName),nodeService.getProperty(nodeRef, propName), oldChildAssocRef.getParentRef(), newChildAssocRef.getParentRef(), nodeRef);
		}
	}

	public void onUpdateNode(QName containedIn, NodeRef nodeRef) {
		// TODO Auto-generated method stub
		
	}

	public void onRemoveAspect(QName containedIn, NodeRef nodeRef, QName aspectTypeQName) {
		for (QName propName : uniquePropertyRegistry.getObjectProperties(containedIn)) {
			uniquePropertyHandler.onDeleteProperty(propName, nodeService.getProperty(nodeRef, propName), nodeService.getPrimaryParent(nodeRef).getParentRef(), nodeRef);
		}
	}

	public void onAddAspect(QName containedIn, NodeRef nodeRef, QName aspectTypeQName) {
		for (QName propName : uniquePropertyRegistry.getObjectProperties(containedIn)) {
			uniquePropertyHandler.onDeleteProperty(propName, nodeService.getProperty(nodeRef, propName), nodeService.getPrimaryParent(nodeRef).getParentRef(), nodeRef);
		}
	}

	@Override
	public void registerType(QName type) {
		registerTypeInt(type);
	}

	@Override
	public void registerAspect(QName aspect) {
		registerAspectInt(aspect);
	}

}
