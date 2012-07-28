package com.lucastheisen.mp4tools.autotagger.tag;


import static org.junit.Assert.assertEquals;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


import javax.xml.stream.XMLStreamReader;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.lucastheisen.xml.XmlPullUtils;
import com.lucastheisen.xml.XmlStreamProcessor;
import com.lucastheisen.xml.XmlTreeNode;


public class XmlPullUtilsTest {
    private static Logger log = LoggerFactory.getLogger( XmlPullUtilsTest.class );

    @Test
    public void textSimpleXml() throws IOException {
        String xml = "<simple>value</simple>";
        final StringBuilder textBuffer = new StringBuilder();

        try ( InputStream inputStream = new ByteArrayInputStream( xml.getBytes( Charset.defaultCharset() ) )) {
            XmlPullUtils.process(
                    new XmlStreamProcessor() {
                        @Override
                        public void process( XMLStreamReader reader ) {
                            XmlPullUtils.advanceToChildElement( reader, "simple" );
                            XmlPullUtils.advanceToEndOfElement( reader, textBuffer );
                        }
                    }, inputStream );
        }

        assertEquals( "value", textBuffer.toString() );
    }

    @Test
    public void testBuildXmlTreeNode() throws IOException {
        String xml = new StringBuilder()
                .append( "<library>\n" )
                .append( "  <book isbn='1234'>\n" )
                .append( "    <author>Robert Ludlum</author>\n" )
                .append( "    <title>The Bourne Identity</title>\n" )
                .append( "  </book>\n" )
                .append( "  <book isbn='5678'>\n" )
                .append( "    <author>Neil Stephenson</author>\n" )
                .append( "    <title>Cryptonomicon</title>\n" )
                .append( "  </book>\n" )
                .append( "</library>" )
                .toString();

        final List<XmlTreeNode> books = new ArrayList<>();

        try ( InputStream inputStream = new ByteArrayInputStream( xml.getBytes( Charset.defaultCharset() ) )) {
            XmlPullUtils.process(
                    new XmlStreamProcessor() {
                        @Override
                        public void process( XMLStreamReader reader ) {
                            XmlPullUtils.advanceToChildElement( reader, "library" );
                            XmlPullUtils.advanceToChildElement( reader, "book" );
                            do {
                                books.add( XmlPullUtils.buildXmlNodeTree( reader ) );
                            } while ( XmlPullUtils.advanceToNextSiblingElement( reader ) );
                        }
                    }, inputStream );
        }

        XmlTreeNode book1 = books.get( 0 );
        log.trace( "book1: {}", book1 );
        assertEquals( "1234", book1.get( "isbn", 0 ).getValue() );
        assertEquals( "Robert Ludlum", book1.get( "author", 0 ).getValue() );
        assertEquals( "The Bourne Identity", book1.get( "title", 0 ).getValue() );

        XmlTreeNode book2 = books.get( 1 );
        assertEquals( "5678", book2.get( "isbn", 0 ).getValue() );
        assertEquals( "Neil Stephenson", book2.get( "author", 0 ).getValue() );
        assertEquals( "Cryptonomicon", book2.get( "title", 0 ).getValue() );
    }
}
