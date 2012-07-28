package com.lucastheisen.mp4tools.autotagger.tag;


import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.apple.AppleDataBox;
import com.coremedia.iso.boxes.apple.AppleGenericBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.apple.AppleMeanBox;
import com.coremedia.iso.boxes.apple.AppleNameBox;
import com.googlecode.mp4parser.AbstractFullBox;


public class IsoParserTest {
    private static Logger log = LoggerFactory.getLogger( IsoParserTest.class );

    private void append( StringBuilder builder, String prefix, Box box ) {
        builder.append( prefix )
                .append( "(" )
                .append( box.getClass() )
                .append( ")" )
                .append( box.getType() )
                .append( ": " )
                .append( box.getSize() );
    }

    public void appendBoxes( StringBuilder builder, String prefix,
            ContainerBox containerBox ) {
        for ( Box box : containerBox.getBoxes() ) {
            append( builder, prefix, box );
            builder.append( " (" );

            boolean first = true;
            if ( box instanceof AbstractFullBox ) {
                AbstractFullBox afBox = (AbstractFullBox) box;
                builder.append( "flags=" ).append( afBox.getFlags() );
                first = false;
            }

            if ( box instanceof HandlerBox ) {
                HandlerBox handlerBox = (HandlerBox) box;
                if ( first ) {
                    first = false;
                }
                else {
                    builder.append( "," );
                }
                builder.append( "name=" )
                        .append( handlerBox.getName() )
                        .append( ",handlerType=" )
                        .append( handlerBox.getHandlerType() );
            }
            else if ( box instanceof AppleItemListBox ) {
                // AppleItemListBox ailBox = (AppleItemListBox) box;
            }
            else if ( box instanceof AppleGenericBox ) {
                // AppleGenericBox agBox = (AppleGenericBox) box;
            }
            else if ( box instanceof AppleMeanBox ) {
                AppleMeanBox amBox = (AppleMeanBox) box;
                if ( first ) {
                    first = false;
                }
                else {
                    builder.append( "," );
                }
                builder.append( "meaning=" )
                        .append( amBox.getMeaning() );
            }
            else if ( box instanceof AppleNameBox ) {
                AppleNameBox anBox = (AppleNameBox) box;
                if ( first ) {
                    first = false;
                }
                else {
                    builder.append( "," );
                }
                builder.append( "name=" )
                        .append( anBox.getName() );
            }
            else if ( box instanceof AppleDataBox ) {
                AppleDataBox adBox = (AppleDataBox) box;
                if ( first ) {
                    first = false;
                }
                else {
                    builder.append( "," );
                }
                builder.append( "fourBytes=" )
                        .append( new String( adBox.getFourBytes() ) )
                        .append( ",data=" )
                        .append( adBox.getFlags() == 13
                                || adBox.getFlags() == 14 ? "<binary>"
                                : new String( adBox.getData() ) );
            }

            builder.append( ")" );

            if ( box instanceof ContainerBox ) {
                appendBoxes( builder, prefix + "\t", (ContainerBox) box );
            }
        }
    }

    @Test
    public void testGetFields() throws IOException {
        String name = "boovsthevegetabledrawer.m4v";
        // ReadableByteChannel rbc = new FileInputStream( file ).getChannel();
        try ( ReadableByteChannel rbc = Channels.newChannel( getClass().getResourceAsStream( "/boovsthevegetabledrawer.m4v" ) ) ;) {
            IsoFile isoFile = new IsoFile( rbc );

            StringBuilder builder = new StringBuilder( "--- " ).append( name ).append( " ---" );
            appendBoxes( builder, "\n\t", isoFile );
            builder.append( "\n--- END " ).append( name ).append( " ---" );

            log.info( builder.toString() );
        }
    }
}
