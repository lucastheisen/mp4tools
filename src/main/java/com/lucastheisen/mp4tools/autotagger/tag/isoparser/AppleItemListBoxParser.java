package com.lucastheisen.mp4tools.autotagger.tag.isoparser;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


import javax.xml.stream.XMLStreamReader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.apple.AppleArtistBox;
import com.coremedia.iso.boxes.apple.AppleCoverBox;
import com.coremedia.iso.boxes.apple.AppleCustomGenreBox;
import com.coremedia.iso.boxes.apple.AppleDescriptionBox;
import com.coremedia.iso.boxes.apple.AppleEncoderBox;
import com.coremedia.iso.boxes.apple.AppleGenericBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.apple.AppleRecordingYearBox;
import com.coremedia.iso.boxes.apple.AppleSynopsisBox;
import com.coremedia.iso.boxes.apple.AppleTrackTitleBox;
import com.lucastheisen.mp4tools.autotagger.tag.Image;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo.Rating;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.AppleBoxUtils.PlistKey;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.AppleBoxUtils.PlistSchema;
import com.lucastheisen.perl.PerlHash;
import com.lucastheisen.xml.XmlPullUtils;
import com.lucastheisen.xml.XmlStreamProcessor;


public class AppleItemListBoxParser implements BoxParser<AppleItemListBox> {
    private static Logger log = LoggerFactory.getLogger( AppleItemListBoxParser.class );

    @Override
    public void parse( AppleItemListBox appleItemListBox, final TagInfo tagInfo ) {
        log.trace( "parsing AppleItemListBox" );
        final List<String> artistsFromAppleArtistBox = new ArrayList<>();
        final List<String> castFromPlist = new ArrayList<>();

        for ( Box box : appleItemListBox.getBoxes() ) {
            if ( box instanceof AppleTrackTitleBox ) {
                tagInfo.setTitle( ((AppleTrackTitleBox) box).getValue() );
            }
            else if ( box instanceof AppleEncoderBox ) {
                if ( log.isTraceEnabled() )
                    log.trace( ((AppleEncoderBox) box).getValue() );
            }
            else if ( box instanceof AppleArtistBox ) {
                String artistListString = ((AppleArtistBox) box).getValue();
                if ( artistListString != null
                        && !artistListString.trim().isEmpty() ) {
                    for ( String artist : artistListString.split( "," ) ) {
                        artistsFromAppleArtistBox.add( artist.trim() );
                    }
                }
            }
            else if ( box instanceof AppleCustomGenreBox ) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for ( String genre : ((AppleCustomGenreBox) box).getValue().split( "," ) ) {
                    genre = genre.trim();
                    if ( !genre.isEmpty() ) {
                        if ( first ) {
                            first = false;
                        }
                        else {
                            builder.append( "," );
                        }
                        builder.append( genre );
                    }
                }
                tagInfo.setGenre( builder.length() > 0 ? builder.toString()
                        : null );
            }
            else if ( box instanceof AppleRecordingYearBox ) {
                tagInfo.setReleaseDate( ((AppleRecordingYearBox) box).getValue() );
            }
            else if ( box instanceof AppleDescriptionBox ) {
                tagInfo.setShortDescription( ((AppleDescriptionBox) box).getValue() );
            }
            else if ( box instanceof AppleSynopsisBox ) {
                tagInfo.setLongDescription( ((AppleSynopsisBox) box).getValue() );
            }
            else if ( box instanceof AppleCoverBox ) {
                Image.Type imageType = null;
                switch ( AppleBoxUtils.getFlags( (AppleCoverBox) box ) ) {
                case 0xe:
                    imageType = Image.Type.png;
                    break;
                case 0xd:
                    imageType = Image.Type.jpg;
                    break;
                }
                tagInfo.setImage( new Image( AppleBoxUtils.getData( (AppleCoverBox) box ), imageType ) );
            }
            else if ( box instanceof AppleGenericBox ) {
                String meaning = AppleBoxUtils.getMeaning( (AppleGenericBox) box );
                String name = AppleBoxUtils.getName( (AppleGenericBox) box );
                byte[] data = AppleBoxUtils.getData( (AppleGenericBox) box );
                if ( data != null && "com.apple.iTunes".equals( meaning ) ) {
                    if ( "iTunEXTC".equals( name ) ) {
                        String[] ratingTokens = new String( data, AppleBoxUtils.CHARSET ).split( "\\|" );
                        if ( ratingTokens.length > 1 ) {
                            tagInfo.setRating( Rating.fromStandardAndRating( ratingTokens[0], ratingTokens[1] ) );
                        }
                        else {
                            tagInfo.setRating( Rating.fromRating( ratingTokens[0] ) );
                        }
                    }
                    else if ( "iTunMOVI".equals( name ) ) {
                        XmlPullUtils.process(
                                new XmlStreamProcessor() {
                                    @Override
                                    public void process( XMLStreamReader reader ) {
                                        if ( XmlPullUtils.advanceToChildElement( reader, PlistSchema.plist.toString() ) ) {
                                            if ( XmlPullUtils.advanceToChildElement( reader, PlistSchema.dict.toString() ) ) {
                                                PerlHash map = processDict( reader );
                                                PerlHash cast = map.get( PlistKey.cast.toString() );
                                                if ( cast != null ) {
                                                    for ( PerlHash member : cast ) {
                                                        castFromPlist.add( (String) member.get( "name" ).value() );
                                                    }
                                                }
                                                PerlHash directors = map.get( PlistKey.directors.toString() );
                                                if ( directors != null ) {
                                                    List<String> directorList = new ArrayList<>();
                                                    for ( PerlHash director : directors ) {
                                                        directorList.add( (String) director.get( "name" ).value() );
                                                    }
                                                    tagInfo.setDirectors( directorList );
                                                }
                                                PerlHash producers = map.get( PlistKey.producers.toString() );
                                                if ( producers != null ) {
                                                    List<String> producerList = new ArrayList<>();
                                                    for ( PerlHash producer : producers ) {
                                                        producerList.add( (String) producer.get( "name" ).value() );
                                                    }
                                                    tagInfo.setProducers( producerList );
                                                }
                                                PerlHash screenWriters = map.get( PlistKey.screenwriters.toString() );
                                                if ( screenWriters != null ) {
                                                    List<String> screenWriterList = new ArrayList<>();
                                                    for ( PerlHash screenWriter : screenWriters ) {
                                                        screenWriterList.add( (String) screenWriter.get( "name" ).value() );
                                                    }
                                                    tagInfo.setScreenWriters( screenWriterList );
                                                }
                                            }
                                        }
                                    }
                                }, new ByteArrayInputStream( data ) );
                    }
                }
            }
        }

        if ( castFromPlist.size() > artistsFromAppleArtistBox.size() ) {
            tagInfo.setCast( castFromPlist );
        }
        else if ( artistsFromAppleArtistBox.size() > 0 ) {
            tagInfo.setCast( artistsFromAppleArtistBox );
        }
    }

    public PerlHash processDict( XMLStreamReader reader ) {
        PerlHash map = new PerlHash();

        XmlPullUtils.advanceToChildElement( reader, PlistSchema.key.toString() );
        do {
            StringBuilder keyBuilder = new StringBuilder();
            XmlPullUtils.advanceToEndOfElement( reader, keyBuilder );
            XmlPullUtils.advanceToNextSiblingElement( reader );
            PlistSchema tag = PlistSchema.valueOf( reader.getLocalName() );
            switch ( tag ) {
            case array:
                if ( XmlPullUtils.advanceToChildElement( reader, PlistSchema.dict.toString() ) ) {
                    PerlHash array = new PerlHash();
                    do {
                        array.add( processDict( reader ) );
                    } while ( XmlPullUtils.advanceToNextSiblingElement( reader ) );
                    map.put( keyBuilder.toString(), array );
                }
                break;
            case string:
                StringBuilder valueBuilder = new StringBuilder();
                XmlPullUtils.advanceToEndOfElement( reader, valueBuilder );
                map.put( keyBuilder.toString(), valueBuilder.toString() );
                break;
            default:
                log.warn( "unknown element {}, dont know what to do...", reader.getLocalName() );
            }
        } while ( XmlPullUtils.advanceToNextSiblingElement( reader ) );

        return map;
    }
}
