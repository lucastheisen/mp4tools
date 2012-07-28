package com.lucastheisen.mp4tools.edit;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;


import org.junit.Test;


public class IsoParserMuxerTest {
    @Test
    public void testMux() throws IOException {
        Muxer muxer = new IsoParserMuxer( false );

        File output = null;
        File h264 = null;
        File aac = null;
        try {
            output = File.createTempFile( "boo", ".mp4" );
            h264 = extractResource( "/boo.h264" );
            aac = extractResource( "/boo.aac" );
            muxer.mux( output, h264, aac );
        }
        finally {
            if ( output != null ) {
                output.delete();
            }
            if ( h264 != null ) {
                h264.delete();
            }
            if ( aac != null ) {
                aac.delete();
            }
        }
    }

    public File extractResource( String resourcePath )
            throws IOException {
        String[] tokens = resourcePath.split( "/" );
        String nameAndPath = tokens[tokens.length - 1];
        int dotIndex = nameAndPath.lastIndexOf( '.' );
        String name = nameAndPath.substring( 0, dotIndex );
        String extension = nameAndPath.substring( dotIndex );
        File destination = File.createTempFile( name, extension );

        try ( InputStream inStream = getClass().getResourceAsStream( resourcePath ) ;
                OutputStream outStream = new FileOutputStream( destination )) {
            ReadableByteChannel inChannel = Channels.newChannel( inStream );
            WritableByteChannel outChannel = Channels.newChannel( outStream );
            final ByteBuffer buffer = ByteBuffer.allocateDirect( 16 * 1024 );
            while ( inChannel.read( buffer ) != -1 ) {
                // prepare the buffer to be drained
                buffer.flip();
                // write to the channel, may block
                outChannel.write( buffer );
                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buffer.compact();
            }
            // EOF will leave buffer in fill state
            buffer.flip();
            // make sure the buffer is fully drained.
            while ( buffer.hasRemaining() ) {
                outChannel.write( buffer );
            }
        }

        return destination;
    }
}
