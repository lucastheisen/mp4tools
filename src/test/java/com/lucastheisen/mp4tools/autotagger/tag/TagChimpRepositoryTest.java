package com.lucastheisen.mp4tools.autotagger.tag;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.lucastheisen.mp4tools.autotagger.tag.TagChimpRepository;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo;


public class TagChimpRepositoryTest {
    private static Logger log = LoggerFactory.getLogger( TagChimpRepositoryTest.class );

    /*
     * Not really a valid test because it relies on an exernal, non-mocked
     * service
     */
    // @Test
    public void connect() {
        URI uri = null;
        try {
            uri = new URI( "http", null, "www.tagchimp.com", -1, "/ape/search.php", "token=10664939374FF64C1FA0EFE&type=search&title=Tomorrow Never Dies&totalChapters=1", null );
        }
        catch ( URISyntaxException urise ) {
            assertTrue( urise.getMessage(), false );
        }

        try ( BufferedReader reader = new BufferedReader( new InputStreamReader( uri.toURL().openStream() ) )) {
            String line;
            while ( (line = reader.readLine()) != null ) {
                log.debug( line );
            }
        }
        catch ( IOException e ) {
            assertTrue( e.getMessage(), false );
        }
    }

    /*
     * Not really a valid test because it relies on an exernal, non-mocked
     * service
     */
    // @Test
    public void search() {
        // https://www.tagchimp.com/ape/search.php?token=10664939374FF64C1FA0EFE&type=search&title=Tomorrow%20Never%20Dies&totalChapters=1
        TagChimpRepository repository = new TagChimpRepository( "http", "www.tagchimp.com", "/ape/search.php", "10664939374FF64C1FA0EFE" );
        log.debug( "**** Results ****" );
        for ( TagInfo tagInfo : repository.search( "Tomorrow Never Dies", 1 ) ) {
            log.debug( tagInfo.toString() );
        }
        log.debug( "**** End Results ****" );
    }

    @Test
    public void parseASearchResult() {
        TagChimpRepository repository = new TagChimpRepository( null, null, null, null );
        List<TagInfo> results = repository.process( getClass().getResourceAsStream( "/tagchimpsearchresult.xml" ) );
        assertEquals( 24, results.size() );
    }
}
