package com.lucastheisen.mp4tools.autotagger.tag;


import static org.junit.Assert.assertEquals;


import java.net.URI;
import java.net.URISyntaxException;


import org.junit.Test;


public class UriTest {
    @Test
    public void testUriConstructor() throws URISyntaxException {
        String uriString = "https://www.tagchimp.com/ape/search.php?token=10664939374FF64C1FA0EFE&type=search&title=Tomorrow%20Never%20Dies&totalChapters=1";
        URI uri = new URI( "https", null, "www.tagchimp.com", -1, "/ape/search.php",
                "token=10664939374FF64C1FA0EFE&type=search&title=Tomorrow Never Dies&totalChapters=1",
                null );
        assertEquals( uriString, uri.toString() );
    }
}
