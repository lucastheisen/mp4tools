package com.lucastheisen.mp4tools.console;


import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.lucastheisen.mp4tools.console.BlockText;


public class BlockTextTest {
    private static Logger log = LoggerFactory.getLogger( BlockTextTest.class );

    @Test
    public void testSmallBlock() {
        String original = "This is a test, this is only a test of the emergency broadcast system.  In the case of a real emergency, this message would be followed by instructions.";
        String expected = new StringBuilder( "BBB  This is a test, this is only a test of the emergency \n" )
                .append( "BBB  broadcast system.  In the case of a real emergency, \n" )
                .append( "BBB  this message would be followed by instructions." )
                .toString();
        String formatted = new BlockText( original, "BBB  ", 60 ).toString();

        log.debug( new StringBuilder( "**** FORMATTED ****\n" ).append( formatted ).append( "\n**** END FORMATTED ****" ).toString() );
        assertEquals( expected, formatted );
    }
}
