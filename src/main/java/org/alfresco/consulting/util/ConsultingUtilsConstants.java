package org.alfresco.consulting.util;

import org.alfresco.service.namespace.QName;

public interface ConsultingUtilsConstants {
	// Namespace for all types and aspects related to consulting utilities
        public static final String CONSULTING_UTILS_MODEL_PREFIX = "alfconsult";
        public static final String CONSULTING_UTILS_MODEL_1_0_URI = "http://www.alfresco.com/consulting/utils/model/1.0";

	public static final QName DYNAMIC_CONSTRAINT =           	QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "DynamicConstraint");
	public static final QName DYNAMIC_CONSTRAINT_DATA_TYPE =    QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "DynamicConstraintDataType");

	public static final QName TYPE_CONSTRAINT_VALUE =		QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "constraintValue");
	public static final QName TYPE_CONSTRAINT_CONTAINER =	QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "constraintContainer");
	public static final QName PROP_STRING_VALUE =			QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "stringValue");
	public static final QName PROP_VALUE_PROPERTY =			QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "valueProperty");
	public static final QName PROP_VALUE_CHECK_SUM =		QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "valueCheckSum");
	public static final QName PROP_DISPLAY_VALUE =			QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "displayValue");
	public static final QName PROP_ACTIVE =					QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "active");
	public static final QName ASSOC_CONSTRAINT =            QName.createQName(CONSULTING_UTILS_MODEL_1_0_URI, "constraint");
	

}
