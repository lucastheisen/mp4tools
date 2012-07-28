package com.lucastheisen.mp4tools.console;


import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;


import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.lucastheisen.mp4tools.autotagger.tag.Image;
import com.lucastheisen.mp4tools.autotagger.tag.Reader;
import com.lucastheisen.mp4tools.autotagger.tag.Repository;
import com.lucastheisen.mp4tools.autotagger.tag.TagChimpRepository;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo;
import com.lucastheisen.mp4tools.autotagger.tag.Writer;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo.Rating;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.AppleItemListBoxParser;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.AppleItemListBoxWriter;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.IsoParserReader;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.IsoParserWriter;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.TypeSafeParserMap;


public class ConsoleApplication {
    private Console console;
    private Reader reader;
    private Repository repository;
    private Writer writer;

    private ConsoleApplication( Repository repository, Reader reader,
            Writer writer ) {
        this.repository = repository;
        this.reader = reader;
        this.writer = writer;
        this.console = new Console();
    }

    private void editTagInfo( TagInfo selectedTagInfo ) {
        // ugly, but works...  for now
        selectedTagInfo.setAmazonAsin( getTagInfoAttributeEdit( "Identifier - amazon", selectedTagInfo.getAmazonAsin() ) );
        selectedTagInfo.setImdbId( getTagInfoAttributeEdit( "Identifier - imdb", selectedTagInfo.getImdbId() ) );
        selectedTagInfo.setITunesUrl( getTagInfoAttributeEdit( "Identifier - itunes", selectedTagInfo.getITunesUrl() ) );
        selectedTagInfo.setNetflixId( getTagInfoAttributeEdit( "Identifier - netflix", selectedTagInfo.getNetflixId() ) );
        selectedTagInfo.setTagChimpId( getTagInfoAttributeEdit( "Identifier - tagChimp", selectedTagInfo.getTagChimpId() ) );
        selectedTagInfo.setTitle( getTagInfoAttributeEdit( "Title", selectedTagInfo.getTitle() ) );
        selectedTagInfo.setReleaseDate( getTagInfoAttributeEdit( "Release Date", selectedTagInfo.getReleaseDate() ) );
        selectedTagInfo.setGenre( getTagInfoAttributeEdit( "Genre", selectedTagInfo.getGenre() ) );
        selectedTagInfo.setRating( getTagInfoRatingEdit( "Rating", selectedTagInfo.getRating() ) );
        selectedTagInfo.setTotalChapters( getTagInfoAttributeEdit( "Total Chapters", selectedTagInfo.getTotalChapters() ) );
        selectedTagInfo.setImage( getTagInfoImageEdit( "Image URL", selectedTagInfo.getImage() ) );
        selectedTagInfo.setShortDescription( getTagInfoAttributeEdit( "Short Description", selectedTagInfo.getShortDescription() ) );
        selectedTagInfo.setLongDescription( getTagInfoAttributeEdit( "Long Description", selectedTagInfo.getLongDescription() ) );
        selectedTagInfo.setCast( getTagInfoAttributeListEdit( "Cast", selectedTagInfo.getCast() ) );
        selectedTagInfo.setDirectors( getTagInfoAttributeListEdit( "Directors", selectedTagInfo.getDirectors() ) );
        selectedTagInfo.setProducers( getTagInfoAttributeListEdit( "Producers", selectedTagInfo.getProducers() ) );
        selectedTagInfo.setScreenWriters( getTagInfoAttributeListEdit( "Screen Writers", selectedTagInfo.getScreenWriters() ) );
    }

    private String filenameToTitle( String filename ) {
        StringBuilder titleBuilder = new StringBuilder();
        if ( filename.indexOf( '.' ) > 0 )
            filename = filename.substring( 0, filename.lastIndexOf( '.' ) );
        char c;
        boolean lastCharacterWasDigit = false;
        boolean lastCharacterWasSpace = false;
        for ( int i = 0, count = filename.length(); i < count; i++ ) {
            c = filename.charAt( i );
            if ( Character.isAlphabetic( c ) ) {
                if ( i > 0 && !lastCharacterWasSpace
                        && Character.isUpperCase( c ) ) {
                    titleBuilder.append( ' ' );
                }
                if ( Character.isDigit( c ) ) {
                    if ( i > 0 && !lastCharacterWasSpace
                            && !lastCharacterWasDigit ) {
                        titleBuilder.append( ' ' );
                    }
                    lastCharacterWasDigit = true;
                }
                else {
                    lastCharacterWasDigit = false;
                }
                lastCharacterWasSpace = false;
                titleBuilder.append( c );
            }
            else {
                if ( !lastCharacterWasSpace ) {
                    titleBuilder.append( ' ' );
                    lastCharacterWasDigit = false;
                    lastCharacterWasSpace = true;
                }
            }
        }
        return titleBuilder.toString();
    }

    @SuppressWarnings( "unused" )
    private String formatTagInfo( TagInfo tagInfo ) {
        return formatTagInfo( tagInfo, "" );
    }

    private String formatTagInfo( TagInfo tagInfo, int index ) {
        return formatTagInfo( tagInfo, index + "  " );
    }

    private String formatTagInfo( TagInfo tagInfo, String prefix ) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter( stringWriter );
        printWriter.printf( "%sIdentifiers: amazon(%s), imdb(%s) itunes(%s) netflix(%s) tagChimp(%s)\n",
                prefix, tagInfo.getAmazonAsin(), tagInfo.getImdbId(), tagInfo.getITunesUrl(),
                tagInfo.getNetflixId(), tagInfo.getTagChimpId() );
        printWriter.printf( "%sTitle: %s\n", prefix, tagInfo.getTitle() );
        printWriter.printf( "%sRelease Date: %s     Genre: %s     Rating: %s\n",
                prefix, tagInfo.getReleaseDate(), tagInfo.getGenre(), tagInfo.getRating() );
        printWriter.printf( "%sTotal Chapters: %s\n", prefix, tagInfo.getTotalChapters() );
        printWriter.printf( "%sImage: %s\n", prefix, tagInfo.getImage() == null ? null
                : (tagInfo.getImage().getUrl() == null ? "<binary>"
                        : tagInfo.getImage().getUrl()) );
        printWriter.printf( "%sShort Description:\n%s\n", prefix, new BlockText( tagInfo.getShortDescription(), prefix
                + "      ", 70 ).toString() );
        printWriter.printf( "%sLong Description:\n%s\n", prefix, new BlockText( tagInfo.getLongDescription(), prefix
                + "      ", 70 ).toString() );
        printWriter.printf( "%sCast:\n", prefix );
        List<String> list = tagInfo.getCast();
        if ( list != null ) {
            for ( String actor : list ) {
                printWriter.printf( "%s    %s\n", prefix, actor );
            }
        }
        printWriter.printf( "%sDirectors:\n", prefix );
        list = tagInfo.getDirectors();
        if ( list != null ) {
            for ( String director : list ) {
                printWriter.printf( "%s    %s\n", prefix, director );
            }
        }
        printWriter.printf( "%sProducers:\n", prefix );
        list = tagInfo.getProducers();
        if ( list != null ) {
            for ( String producer : list ) {
                printWriter.printf( "%s    %s\n", prefix, producer );
            }
        }
        printWriter.printf( "%sScreen Writers:\n", prefix );
        list = tagInfo.getScreenWriters();
        if ( list != null ) {
            for ( String screenWriter : list ) {
                printWriter.printf( "%s    %s\n", prefix, screenWriter );
            }
        }
        printWriter.printf( "\n" );

        return stringWriter.toString();
    }

    private List<String> getTagInfoAttributeListEdit( String field,
            List<String> list ) {
        StringBuilder builder = new StringBuilder();
        if ( list != null ) {
            boolean first = true;
            for ( String item : list ) {
                if ( first ) {
                    first = false;
                }
                else {
                    builder.append( "," );
                }
                builder.append( item );
            }
        }
        String newValue = console.readLine( "%s (%s):", field, builder.toString() );
        return newValue.isEmpty() ? list
                : Arrays.asList( newValue.split( "," ) );
    }

    private String getTagInfoAttributeEdit( String field, String value ) {
        String newValue = console.readLine( "%s (%s):", field, value );
        return newValue.isEmpty() ? value : newValue;
    }

    private Image getTagInfoImageEdit( String field, Image value ) {
        Image newImage = null;

        String message = "";
        while ( newImage == null ) {
            String urlString = console.readLine( "%s%s (%s):", message, field, value == null ? null : value.getUrl().toString() );
            try {
                Image.Type imageType = null;
                if ( urlString.endsWith( ".jpg" ) ) {
                    imageType = Image.Type.jpg;
                }
                if ( urlString.endsWith( ".png" ) ) {
                    imageType = Image.Type.png;
                }
                newImage = new Image( urlString, imageType );
            }
            catch ( MalformedURLException e ) {
                message = "Malformed URL - ";
            }
        }

        return newImage;
    }

    private Rating getTagInfoRatingEdit( String field, Rating value ) {
        String newValue = console.readLine( "%s (%s):", field, value );
        String[] tokens = newValue.split( "\\|" );
        Rating rating = null;
        if ( tokens.length > 1 ) {
            rating = Rating.fromStandardAndRating( tokens[0], tokens[1] );
        }
        else {
            rating = Rating.fromRating( newValue );
        }
        return rating;
    }

    private void loop() {
        boolean another = true;
        while ( another ) {
            String fileMessage = "";
            File file = new File( "thisfiledoesnotexistunlessyouarereallystrange.zzz" );
            while ( !file.exists() ) {
                file = new File( console.readLine( "%sFilename: ", fileMessage ) );
                fileMessage = "File does not exist\n\n";
            }

            TagInfo originalTagInfo = reader.read( file );
            console.printf( "Current Tag Info:\n%s\n", formatTagInfo( originalTagInfo, "    " ) );
            String continueString = console.readLine( "Continue? (yes): " );
            if ( continueString.isEmpty()
                    || continueString.equalsIgnoreCase( "yes" ) ) {

                String defaultTitle = filenameToTitle( file.getName() );
                String title = console.readLine( "Title (%s): ", defaultTitle );
                if ( title.isEmpty() ) title = defaultTitle;

                List<TagInfo> tagInfoList = repository.search( title, 1 );

                TagInfo selectedTagInfo = null;
                if ( tagInfoList.size() > 0 ) {
                    int i = 0;
                    for ( TagInfo tagInfo : tagInfoList ) {
                        console.printf( formatTagInfo( tagInfo, i ) );
                        i++;
                    }

                    String choiceMessage = "";
                    int maxChoice = i - 1;
                    int choice = -1;
                    while ( choice < 0 || choice > maxChoice ) {
                        String choiceString = console.readLine( "%sChoice (0-%d): ", choiceMessage, maxChoice );
                        choiceMessage = "Choice out of range\n\n";

                        try {
                            choice = Integer.parseInt( choiceString );
                        }
                        catch ( NumberFormatException nfe ) {
                            choiceMessage = "Invalid choice: "
                                    + nfe.getMessage()
                                    + "\n\n";
                            choice = -1;
                        }
                    }

                    selectedTagInfo = tagInfoList.get( choice );

                    console.printf( "Selected Tag Info:\n%s\n", formatTagInfo( selectedTagInfo, "    " ) );
                }

                String editString = null;
                if ( selectedTagInfo == null ) {
                    editString = "yes";
                    selectedTagInfo = new TagInfo();
                }
                else {
                    editString = console.readLine( "Edit? (no): " );
                }
                if ( editString.equalsIgnoreCase( "yes" ) ) {
                    boolean satisfied = false;
                    while ( !satisfied ) {
                        editTagInfo( selectedTagInfo );
                        console.printf( "Selected Tag Info:\n%s\n", formatTagInfo( selectedTagInfo, "    " ) );
                        String satisfiedString = console.readLine( "Satisfied? (no): " );
                        satisfied = satisfiedString.equalsIgnoreCase( "yes" );
                    }
                }

                try {
                    writer.write( file, selectedTagInfo );
                    console.printf( "Tagging successful\n\n" );
                }
                catch ( Exception e ) {
                    console.printf( "Tagging failed: %s\n\n", e.getMessage() );
                }
            }

            String anotherString = console.readLine( "Another? (yes): " );
            another = (anotherString.isEmpty() || anotherString.equalsIgnoreCase( "yes" ));
        }
    }

    public static void main( String[] args ) {
        Repository repository = new TagChimpRepository( "http", "www.tagchimp.com", "/ape/search.php", "10664939374FF64C1FA0EFE" );
        Writer writer = new IsoParserWriter( new AppleItemListBoxWriter() );

        TypeSafeParserMap parserMap = new TypeSafeParserMap();
        parserMap.add( AppleItemListBox.class, new AppleItemListBoxParser() );
        IsoParserReader reader = new IsoParserReader( parserMap );

        new ConsoleApplication( repository, reader, writer ).loop();
    }
}
