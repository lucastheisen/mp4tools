package com.lucastheisen.xml;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.lucastheisen.mp4tools.autotagger.tag.RepositorySearchException;


public class XmlPullUtils {
    private static Logger log = LoggerFactory.getLogger( XmlPullUtils.class );

    /**
     * Advances the reader until it finds a child (depth 1) element of the
     * current element with a local name matching <code>localName</code>. If the
     * element with the given name is found, then the reader will stop at the
     * {@link javax.xml.stream.XMLStreamConstants#START_ELEMENT START_ELEMENT}
     * and <code>true</code> will be returned. Otherwise, the reader will stop
     * at the {@link javax.xml.stream.XMLStreamConstants#END_ELEMENT
     * END_ELEMENT} of the current element.
     * 
     * @param reader
     * @param localName
     * @return True if found, false otherwise.
     * @throws XMLStreamException
     */
    public static boolean advanceToChildElement( XMLStreamReader reader,
            String localName ) {
        int depth = 0;
        try {
            while ( reader.hasNext() ) {
                int code = reader.next();
                if ( code == XMLStreamConstants.START_ELEMENT ) {
                    depth++;
                    if ( depth == 1 && reader.getLocalName().equals( localName ) ) {
                        return true;
                    }
                }
                else if ( code == XMLStreamConstants.END_ELEMENT ) {
                    if ( depth == 0 ) break; // ended current element
                    depth--;
                }
            }
        }
        catch ( XMLStreamException e ) {
            throw new XmlPullException( e.getMessage(), e );
        }
        return false;
    }

    /**
     * Advances the reader until it finds the end of the current element. Simply
     * calls {@link #advanceToEndOfElement(XMLStreamReader, StringBuilder)
     * advanceToEndOfElement(reader,null)}.
     * 
     * @param reader
     * @throws XMLStreamException
     * @throws NoSuchElementException
     *             If no end element is found for the current element, which
     *             means either their is not current element (before the first
     *             element), or the XML is not valid.
     */
    public static void advanceToEndOfElement( XMLStreamReader reader ) {
        advanceToEndOfElement( reader, null );
    }

    /**
     * Advances the reader until it finds the end of the current element. If
     * textBuffer is not null, all text (
     * {@link javax.xml.stream.XMLStreamConstants#CDATA CDATA} or
     * {@link javax.xml.stream.XMLStreamConstants#CHARACTERS CHARACTERS})
     * encountered along the way will be appended to it. Intended to be called
     * from a {@link javax.xml.stream.XMLStreamConstants#START_ELEMENT
     * START_ELEMENT} to ensure all the text is included.
     * 
     * @param reader
     * @param textBuffer
     *            A buffer for all non-ignorable text found along the way
     * @throws XMLStreamException
     * @throws NoSuchElementException
     *             If no end element is found for the current element, which
     *             means either their is not current element (before the first
     *             element), or the XML is not valid.
     */
    public static void advanceToEndOfElement( XMLStreamReader reader,
            StringBuilder textBuffer ) {
        int depth = 0;
        while ( true ) {
            try {
                int code = reader.next();

                if ( code == XMLStreamConstants.START_ELEMENT ) {
                    depth++;
                }
                if ( code == XMLStreamConstants.END_ELEMENT ) {
                    if ( depth == 0 ) return; // ended current element
                    depth--;
                }
                if ( textBuffer != null && code == XMLStreamConstants.CDATA
                        || code == XMLStreamConstants.CHARACTERS ) {
                    textBuffer.append( reader.getText() );
                }
            }
            catch ( XMLStreamException e ) {
                throw new XmlPullException( e.getMessage(), e );
            }
        }
    }

    /**
     * Advances the reader until it finds the start of the next sibling element.
     * If a sibling was found, then the reader will stop at the
     * {@link javax.xml.stream.XMLStreamConstants#START_ELEMENT START_ELEMENT}
     * of the sibling and return true. Otherwise, the reader will stop at the
     * {@link javax.xml.stream.XMLStreamConstants#END_ELEMENT END_ELEMENT} of
     * the parent of the current element and return false. Safe even if already
     * at the <code>END_ELEMENT</code> of a current element.
     * 
     * @param reader
     * @return <code>true</code>, if a next sibling was found,
     *         <code>false</code> otherwise.
     * @throws XMLStreamException
     * @throws NoSuchElementException
     *             If no sibling, and no end element is found for the parent
     *             element, which means either the current element is the
     *             document element, or the XML is not valid.
     */
    public static boolean advanceToNextSiblingElement( XMLStreamReader reader ) {
        if ( reader.getEventType() != XMLStreamConstants.END_ELEMENT )
            advanceToEndOfElement( reader );
        while ( true ) {
            try {
                int code = reader.next();
                if ( code == XMLStreamConstants.START_ELEMENT ) {
                    return true;
                }
                if ( code == XMLStreamConstants.END_ELEMENT ) {
                    return false;
                }
            }
            catch ( XMLStreamException e ) {
                throw new XmlPullException( e.getMessage(), e );
            }
        }
    }

    public static XmlTreeNode buildXmlNodeTree( XMLStreamReader reader ) {
        return buildXmlNodeTree( reader, 0 );
    }

    private static XmlTreeNode buildXmlNodeTree( XMLStreamReader reader,
            int depth ) {
        if ( reader.getEventType() != XMLStreamConstants.START_ELEMENT )
            throw new IllegalStateException( "must be START_ELEMENT" );
        String localName = reader.getLocalName();
        log.trace( "entering buildXmlNodeTree for {}, depth {}", localName, depth );

        XmlTreeNode nodeTree = new XmlTreeNode();
        for ( int i = 0, count = reader.getAttributeCount(); i < count; i++ ) {
            nodeTree.add( reader.getAttributeLocalName( i ), reader.getAttributeValue( i ) );
        }

        StringBuilder textBuffer = new StringBuilder();
        while ( true ) {
            try {
                int code = reader.next();
                if ( code == XMLStreamConstants.START_ELEMENT ) {
                    nodeTree.add( reader.getLocalName(), buildXmlNodeTree( reader, depth + 1 ) );
                }
                else if ( code == XMLStreamConstants.END_ELEMENT ) {
                    break;
                }
                else if ( code == XMLStreamConstants.CDATA
                        || code == XMLStreamConstants.CHARACTERS ) {
                    textBuffer.append( reader.getText() );
                }
            }
            catch ( XMLStreamException e ) {
                throw new XmlPullException( e.getMessage(), e );
            }
        }

        nodeTree.setValue( textBuffer.toString() );
        log.trace( "leaving buildXmlNodeTree for {}", localName );
        return nodeTree;
    }

    public static void process( XmlStreamProcessor xmlStreamProcesor,
            InputStream inputStream ) {
        XMLStreamReader reader = null;
        try {
            reader = XMLInputFactory.newInstance().createXMLStreamReader( inputStream );

            xmlStreamProcesor.process( reader );
        }
        catch ( FactoryConfigurationError
                | XMLStreamException e ) {
            throw new RepositorySearchException( e );
        }
        finally {
            if ( reader != null ) {
                try {
                    reader.close();
                }
                catch ( XMLStreamException e ) {
                    log.error( "Failed to close XMLStreamReader politely: {}\n{}", e.getMessage(), e );
                }
            }
        }
    }

    private static class XmlTreeNode implements
            com.lucastheisen.xml.XmlTreeNode {
        private String value;
        private Map<String, List<XmlTreeNode>> map;

        private XmlTreeNode() {
        }

        private XmlTreeNode( String value ) {
            this.value = value;
        }

        private void add( String key, String value ) {
            add( key, new XmlTreeNode( value ) );
        }

        private void add( String key, XmlTreeNode value ) {
            if ( map == null ) {
                map = new HashMap<>();
            }

            List<XmlTreeNode> valueList = map.get( key );
            if ( valueList == null ) {
                valueList = new ArrayList<>();
                map.put( key, valueList );
            }

            valueList.add( value );
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getValue( String key, int index ) {
            XmlTreeNode value = get( key, index );
            return value == null ? null : value.getValue();
        }

        @Override
        public List<String> getValues( String key ) {
            List<XmlTreeNode> list = map.get( key );

            if ( list == null ) return null;

            List<String> values = new ArrayList<>();
            for ( XmlTreeNode node : list ) {
                values.add( node.getValue() );
            }
            return values;
        }

        @Override
        public List<XmlTreeNode> get( String key ) {
            return map.get( key );
        }

        @Override
        public XmlTreeNode get( String key, int index ) {
            List<XmlTreeNode> list = map.get( key );
            return list == null ? null : list.get( index );
        }

        private void setValue( String value ) {
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder( "{value=" )
                    .append( value );
            if ( map != null ) {
                builder.append( ",map=" )
                        .append( "{" );
                for ( String key : map.keySet() ) {
                    builder.append( key )
                            .append( "=" )
                            .append( map.get( key ) );
                }
            }
            return builder.append( "}" ).toString();
        }
    }

}
