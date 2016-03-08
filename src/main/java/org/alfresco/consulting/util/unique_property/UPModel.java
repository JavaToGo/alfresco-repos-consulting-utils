package org.alfresco.consulting.util.unique_property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UPModel {
    private String name = null;
    private List<UPNamespace> imports = new ArrayList<UPNamespace>();
    private List<UPProperty> properties = new ArrayList<UPProperty>();
 
    public String getName()
    {
        return name;
    }
    
    
    public void setName(String name)
    {
        this.name = name;
    }

    public UPNamespace createImport(String uri, String prefix)
    {
    	UPNamespace namespace = new UPNamespace();
        namespace.setUri(uri);
        namespace.setPrefix(prefix);
        imports.add(namespace);
        return namespace;
    }
    
    
    public void removeImport(String uri)
    {
    	UPNamespace namespace = getImport(uri);
        if (namespace != null)
        {
            imports.remove(namespace);
        }
    }


    public List<UPNamespace> getImports()
    {
        return Collections.unmodifiableList(imports);
    }

    
    public UPNamespace getImport(String uri)
    {
        for (UPNamespace candidate : imports)
        {
            if (candidate.getUri().equals(uri))
            {
                return candidate;
            }
        }
        return null;
    }

    public UPProperty createProperty(String name)
    {
        UPProperty property = new UPProperty();
        property.setName(name);
        properties.add(property);
        return property;
    }
    
    
    public void removeProperty(String name)
    {
        UPProperty property = getProperty(name);
        if (property != null)
        {
            properties.remove(property);
        }
    }


    public List<UPProperty> getProperties()
    {
        return Collections.unmodifiableList(properties);
    }

    
    public UPProperty getProperty(String name)
    {
        for (UPProperty candidate : properties)
        {
            if (candidate.getName().equals(name))
            {
                return candidate;
            }
        }
        return null;
    }


    
}
