package com.lucastheisen.xml;


import java.util.List;


public interface XmlTreeNode {
    public XmlTreeNode get( String key, int index );

    public List<? extends XmlTreeNode> get( String key );

    public String getValue();

    public String getValue( String key, int index );

    public List<String> getValues( String key );
}
