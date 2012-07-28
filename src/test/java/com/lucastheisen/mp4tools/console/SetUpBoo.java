package com.lucastheisen.mp4tools.console;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import com.lucastheisen.mp4tools.autotagger.tag.Image;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo;
import com.lucastheisen.mp4tools.autotagger.tag.Writer;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.AppleItemListBoxWriter;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.IsoParserWriter;


public class SetUpBoo {
    public static void main( String args[] ) throws IOException {
        TagInfo tagInfo = new TagInfo();
        tagInfo.setRating( TagInfo.Rating.mpaaNotRated );
        tagInfo.setCast( "Boo", "The Carrot" );
        tagInfo.setDirectors( "Lucas" );
        tagInfo.setProducers( "Lucas" );
        tagInfo.setScreenWriters( "Boo", "Lucas" );
        tagInfo.setTitle( "Boo vs The Vegetable Drawer" );
        tagInfo.setGenre( "Animal Fiendery" );
        tagInfo.setReleaseDate( "2005-12-20" );
        tagInfo.setShortDescription( "The little PITA raids the vegetable drawer." );
        tagInfo.setLongDescription( "Boo decides its time for a snack so she proceeds to open the refridgerator door, open the vegetable drawer, root around, find some carrots, and walk off.  She doesn't even bother to shut the door." );
        try ( InputStream inputStream = SetUpBoo.class.getResourceAsStream( "/boo.jpg" )) {
            tagInfo.setImage( inputStream, Image.Type.jpg );
        }

        Writer writer = new IsoParserWriter( new AppleItemListBoxWriter() );
        writer.write( new File( "src/test/resources/boovsthevegetabledrawer.m4v" ), tagInfo );
        System.out.println( "Done" );
    }
}
