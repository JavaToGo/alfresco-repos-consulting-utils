package org.alfresco.consulting.util.unique_property;

public class UPProperty {
	String name;
	boolean isGlobal;
	boolean isImmutable;
	boolean isImmovable;
	boolean isSequence;
    /*package*/ UPProperty()
    {
    }

    
    /*package*/ UPProperty(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }

    
    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isGlobal()
    {
        return isGlobal;
    }
    
    
    public void setGlobal(boolean isGlobal)
    {
        this.isGlobal = isGlobal;
    }
    
    
    public boolean isImmutable()
    {
        return isImmutable;
    }
    
    public void setImmutable(boolean isImmutable)
    {
        this.isImmutable = isImmutable;
    }

    public boolean isImmovable()
    {
        return isImmovable;
    }
    
    
    public void setImmovable(boolean isImmovable)
    {
        this.isImmovable = isImmovable;
    }
    
    
    public boolean isSequence()
    {
        return isSequence;
    }
    
    public void setSequence(boolean isSequence)
    {
        this.isSequence = isSequence;
    }
    
}
