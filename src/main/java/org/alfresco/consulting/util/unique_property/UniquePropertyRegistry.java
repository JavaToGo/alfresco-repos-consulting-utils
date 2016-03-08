package org.alfresco.consulting.util.unique_property;

import java.util.Set;

import org.alfresco.service.namespace.QName;

public interface UniquePropertyRegistry {
	public interface UniquePropertyRegistryListener {
		void registerType(QName type);
		void registerAspect(QName aspect);
	}
	void registerPropertyName(QName propertyName, boolean isGlobal);
	void registerPropertyName(QName propertyName, boolean isGlobal, boolean isSequence);
	void registerPropertyName(QName propertyName, boolean isGlobal, boolean isSequence, boolean isImmutable, boolean isImmovable);
	boolean isIdGlobal(QName propName);
	boolean isIdImmutable(QName propName);
	boolean isIdImmovable(QName propName);
	boolean isIdSequence(QName propName);
	boolean isIdAspect(QName propName);
	Set<QName> getProperties();
	Set<QName> getTypes();
	Set<QName> getAspects();
	Set<QName> getTypeProperties(QName type);
	Set<QName> getAspectProperties(QName aspect);
	Set<QName> getObjectProperties(QName object);
	void addListener(UniquePropertyRegistryListener listener);
}
