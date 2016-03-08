package org.alfresco.consulting.util.unique_property;

public class UPNamespace {


    private String uri = null;
    private String prefix = null;
   
    
    /*package*/ UPNamespace()
    {
    }

    
    public String getUri()
    {
        return uri;
    }
    

    public void setUri(String uri)
    {
        this.uri = uri;
    }
   

    public String getPrefix()
    {
        return prefix;
    }
    

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
    
}
