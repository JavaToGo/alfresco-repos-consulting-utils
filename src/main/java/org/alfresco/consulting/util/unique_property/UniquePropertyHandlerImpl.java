package org.alfresco.consulting.util.unique_property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public class UniquePropertyHandlerImpl implements UniquePropertyHandler, UniquePropertyRegistry {
	class PropertyDescriptor {
		QName containedIn;
		boolean isAspect;
		boolean isGlobal;
		boolean isImmutable;
		boolean isImmovable;
		boolean isSequence;
	}
	UniquePropertyManager uniquePropertyManager;
	ServiceRegistry serviceRegistry;

	DictionaryService dictionaryService;
	Map<QName,PropertyDescriptor> propMap = new HashMap<QName,PropertyDescriptor>();
	Map<QName, Set<QName>> typeProps = new HashMap<QName,Set<QName>>();
	Map<QName, Set<QName>> aspectProps = new HashMap<QName,Set<QName>>();
	List<UniquePropertyRegistryListener> registryListeners = new ArrayList<UniquePropertyRegistryListener> ();

	
	public void setUniquePropertyManager(UniquePropertyManager uniquePropertyManager) {
		this.uniquePropertyManager = uniquePropertyManager;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.dictionaryService = this.serviceRegistry.getDictionaryService();
	}
	
	
	@Override
	public void registerPropertyName(QName propertyName,boolean isGlobal) {
		registerPropertyName(propertyName, isGlobal, false);
	}	
	
	@Override
	public void registerPropertyName(QName propertyName,boolean isGlobal,boolean isSequence) {
		registerPropertyName(propertyName, isGlobal, isSequence,false,false);
	}
	
	private Set<QName> getContainedInSet(QName ciQName,boolean isAspect) {
		Map<QName , Set<QName>> myMap;
		if (isAspect) {
			myMap = aspectProps;
		} else {
			myMap = typeProps;
		}
		if (myMap.containsKey(ciQName)) return myMap.get(ciQName);
		myMap.put(ciQName, new HashSet<QName>());
		//Notify if not previously registered
		if (isAspect) {
			for (UniquePropertyRegistryListener listener : registryListeners) {
				listener.registerAspect(ciQName);
			}
		} else {
			for (UniquePropertyRegistryListener listener : registryListeners) {
				listener.registerType(ciQName);
			}
		}
		return myMap.get(ciQName);
	}
	
	@Override
	public void registerPropertyName(QName propertyName,boolean isGlobal,boolean isSequence,boolean isImmutable,boolean isImmovable) {
		QName containedIn;
		boolean isAspect;
		PropertyDefinition pd = dictionaryService.getProperty(propertyName);
		ClassDefinition cd = pd.getContainerClass();
		containedIn = cd.getName();
		isAspect = cd.isAspect();
		registerPropertyNameInt(propertyName, isGlobal, isSequence, isImmutable, isImmovable, containedIn, isAspect);
	}
	
	private void registerPropertyNameInt(QName propertyName,boolean isGlobal,boolean isSequence,boolean isImmutable,boolean isImmovable,QName containedIn,boolean isAspect) {
		PropertyDescriptor pd = new PropertyDescriptor();
		pd.containedIn = containedIn;
		pd.isAspect = isAspect;
		pd.isGlobal = isGlobal;
		pd.isImmovable = isImmovable;
		pd.isImmutable = isImmutable;
		pd.isSequence = isSequence;
		propMap.put(propertyName, pd);
		getContainedInSet(containedIn,isAspect).add(propertyName);
	}

	@Override
	public void onCreateProperty(QName propName,Serializable value,NodeRef parent,NodeRef node) {
		checkPropName(propName);
		if (isIdGlobal(propName)) {
			uniquePropertyManager.storeGlobalUniqueId(propName, value, node);
		} else {
			uniquePropertyManager.storeScopedUniqueId(parent, propName, value, node);
		}
	}
	protected void onUpdatePropertyInt(QName propName,Serializable oldValue,Serializable newValue,NodeRef oldParent, NodeRef newParent, NodeRef node) {
		onDeleteProperty(propName,oldValue,oldParent,node);
		onCreateProperty(propName,newValue,newParent,node);		
	}
	@Override
	public void onUpdateProperty(QName propName,Serializable oldValue,Serializable newValue,NodeRef oldParent, NodeRef newParent, NodeRef node) {
		checkPropName(propName);
		assert(oldValue != null);
		assert(newValue != null);
		assert(oldParent != null);
		assert(newParent != null);
		if (newValue.equals(oldValue) && newParent.equals(oldParent)) return;
		if (isIdImmutable(propName) && !newValue.equals(oldValue)) {
			throw new ImmutableUniqueIdRuntimeException("Property " + propName + " cannot be changed once it has been assigned");
		} else if (isIdImmovable(propName) && !isIdGlobal(propName) && !newParent.equals(oldParent)) {
			throw new ImmovableUniqueIdRuntimeException("A node created with Property " + propName + " cannot be moved once it has been created");
		} else {
			onUpdatePropertyInt(propName, oldValue, newValue, oldParent, newParent, node);
		}
	}
	@Override
	public void onDeleteProperty(QName propName,Serializable value,NodeRef parent,NodeRef node) {
		checkPropName(propName);
		if (isIdGlobal(propName)) {
			uniquePropertyManager.clearGlobalUniqueId(propName, value);
		} else {
			uniquePropertyManager.clearScopedUniqueId(parent, propName, value);
		}
	}
	private void checkPropName(QName propName) {
		if (!propMap.containsKey(propName)) throw new UnkownPropertyUniqueIdRuntimeException("Unknown Property " + propName);
	}
	@Override
	public boolean isIdGlobal(QName propName) {
		checkPropName(propName);
		return propMap.get(propName).isGlobal;
	}
	@Override
	public boolean isIdImmutable(QName propName) {
		checkPropName(propName);
		return propMap.get(propName).isImmutable;
	}
	@Override
	public boolean isIdImmovable(QName propName) {
		checkPropName(propName);
		return propMap.get(propName).isImmovable;
	}
	@Override
	public boolean isIdAspect(QName propName) {
		checkPropName(propName);
		return propMap.get(propName).isAspect;
	}
	@Override
	public boolean isIdSequence(QName propName) {
		checkPropName(propName);
		return propMap.get(propName).isSequence;
	}

	@Override
	public Set<QName> getProperties() {
		return propMap.keySet();
	}

	@Override
	public Set<QName> getTypes() {
		return typeProps.keySet();
	}

	@Override
	public Set<QName> getAspects() {
		return aspectProps.keySet();
	}

	@Override
	public Set<QName> getTypeProperties(QName type) {
		return typeProps.get(type);
	}

	@Override
	public Set<QName> getAspectProperties(QName aspect) {
		return aspectProps.get(aspect);
	}

	@Override
	public Set<QName> getObjectProperties(QName object) {
		if (isIdAspect(object)) {
			return aspectProps.get(object);
		} else {
			return typeProps.get(object);
		}
	}

	@Override
	public void addListener(UniquePropertyRegistryListener listener) {
		registryListeners.add(listener);
	}
}
