package com.lucastheisen.mp4tools.autotagger.tag;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Image {
    private static Logger log = LoggerFactory.getLogger( Image.class );

    private URL url;
    private Type type;
    private byte[] bytes;

    public Image( byte[] bytes, Type type ) {
        this.bytes = bytes;
        this.type = type;
    }
    
    public Image( InputStream inputStream, Type type ) throws IOException {
        this( getBytesFromStream( inputStream ), type );
    }

    public Image( String url, Type type ) throws MalformedURLException {
        this( new URL( url ), type );
    }
    
    public Image( URL url, Type type ) {
        this.url = url;
        this.type = type;
    }
    
    private static byte[] getBytesFromStream( InputStream inputStream ) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ( (nRead = inputStream.read( data, 0, data.length )) != -1 ) {
            buffer.write( data, 0, nRead );
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public URL getUrl() {
        return url;
    }

    public byte[] getBytes() {
        if ( bytes == null ) {
            //TODO: check the URL connection headers to get content type to verify either png or jpg (safer than trusting extension)
            try ( InputStream inputStream = url.openStream()) {
                bytes = getBytesFromStream( inputStream );
            }
            catch ( MalformedURLException murle ) {
                bytes = null;
                log.warn( "Malformed image url: {}", url );
            }
            catch ( Exception e ) {
                bytes = null;
                log.warn( "Unable to read from {}: {}", url, e );
            }
        }
        return bytes;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setUrl( String url ) throws MalformedURLException {
        this.url = new URL( url );
    }

    public void setUrl( URL url ) {
        this.url = url;
        this.bytes = null;
    }

    public void setBytes( byte[] bytes ) {
        this.bytes = bytes;
    }
    
    public enum Type {
        jpg, png;
    }
}
