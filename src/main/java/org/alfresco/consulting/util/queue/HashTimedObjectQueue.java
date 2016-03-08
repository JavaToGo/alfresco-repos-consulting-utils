package org.alfresco.consulting.util.queue;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.attributes.DuplicateAttributeException;
import org.alfresco.service.cmr.attributes.AttributeService.AttributeQueryCallback;
import org.alfresco.util.PropertyCheck;

public class HashTimedObjectQueue<T extends Serializable> {
	
	public interface ObjectProcessor<T extends Serializable> {
		void processEvent(String namespace,String name,long timestamp,long triggertime,T obj);
	}

	private static final String DEFAULT_NAMESPACE = "org.alfresco.consulting.util.queue";
	
	private ServiceRegistry serviceRegistry;
	private String namespace = DEFAULT_NAMESPACE;
	private String name;
	private static final long ALL_OBECTS=-1;
	
	private boolean clear = false;
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	protected ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected String getName() {
		return this.name;
	}
	
	protected String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setClear(Boolean clear) {
		this.clear = Boolean.TRUE.equals(clear);
	}
	
	protected Boolean doClear() {
		return this.clear;
	}
	
	public void init() {
		this.validate();
		
		if (this.clear) {
			this.serviceRegistry.getAttributeService().removeAttributes(this.namespace, this.name);
		}
	}
	
	public void validate() {
		PropertyCheck.mandatory(this, "serviceRegistry", this.serviceRegistry);
		PropertyCheck.mandatory(this, "name", this.name);
	}
	
	
	
	public void addObject(T obj) {
		try {
			this.serviceRegistry.getAttributeService().createAttribute(System.currentTimeMillis(), this.namespace, this.name, obj);
		} catch (DuplicateAttributeException dae) {
			// suppress; will remain in queue once
		}
	}
	
	public void processObjects(final long snapshotTime, ObjectProcessor<T> op) {
		visitObjects(snapshotTime,op,true);
	}
	
	public void processObjects(ObjectProcessor<T> op) {
		processObjects(System.currentTimeMillis(),op);
	}
	
	public void visitObjects(ObjectProcessor<T> op) {
		visitObjects(System.currentTimeMillis(),op,false);
	}
	
	public void visitObjects(final long snapshotTime, ObjectProcessor<T> op) {
		visitObjects(snapshotTime,op,false);
	}

	//TODO: Add Logging
	public void visitObjects(final long snapshotTime, final ObjectProcessor<T> op,final boolean remove) {
		visitObjects(snapshotTime,op,remove,ALL_OBECTS);
	}
	public void visitObjects(final long snapshotTime, final ObjectProcessor<T> op,final boolean remove,final long limit) {
		AttributeQueryCallback callback = new AttributeQueryCallback() {
			long count = 0;
			@Override
			@SuppressWarnings("unchecked")
			public boolean handleAttribute(Long id, Serializable value, Serializable[] keys) {
				if (((Number)value).longValue() <= snapshotTime) {
					if (remove) {
						removeObject((T) value);
					}
					count++;
					op.processEvent((String) keys[0], (String) keys[1], (Long) keys[2], snapshotTime, (T) value);
				}
				if ((limit >= 0) && (count >= limit)) {
					return false;
				}
				return true;
			}
		};
		
		this.serviceRegistry.getAttributeService().getAttributes(callback, this.namespace, this.name);
	}
	
	public void removeObject(T obj) {
		this.serviceRegistry.getAttributeService().removeAttribute(this.namespace, this.name, obj);
	}
	
	public Collection<T> getObjects() {
		return getObjects(System.currentTimeMillis());
	}
	public Collection<T> getObjects(boolean remove) {
		return getObjects(System.currentTimeMillis(),remove);
	}
	public Collection<T> getObjects(final long snapshotTime) {
		return getObjects(snapshotTime,false);
	}
	public Collection<T> getObjects(final long snapshotTime,boolean remove) {
		final Queue<T> objs = new LinkedList<T>();
		
		ObjectProcessor<T> op = new ObjectProcessor<T>() {

			@Override
			public void processEvent(String namespace, String name,
					long timestamp, long triggertime, T obj) {
				objs.add(obj);
				
			}
		};
		
		visitObjects(snapshotTime, op, remove);
		return objs;
	}
	public long countObjects() {
		return countObjects(System.currentTimeMillis());
	}
	public long countObjects(boolean remove) {
		return countObjects(System.currentTimeMillis(),remove);
	}
	public long countObjects(final long snapshotTime) {
		return countObjects(snapshotTime,false);
	}
	class ObjectCountProcessor<T extends Serializable> implements ObjectProcessor<T> {
		private long count=0;
		
		long getCount() {
			return count;
		}

		@Override
		public void processEvent(String namespace, String name,
				long timestamp, long triggertime, T obj) {
			count++;
			
		}
	};
	public long countObjects(final long snapshotTime,boolean remove) {
		
		ObjectCountProcessor<T> op = new ObjectCountProcessor<T>();
		
		visitObjects(snapshotTime, op, remove);
		return op.getCount();
	}

}
