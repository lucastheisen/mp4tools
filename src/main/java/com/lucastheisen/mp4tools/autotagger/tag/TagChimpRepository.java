package com.lucastheisen.mp4tools.autotagger.tag;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import javax.xml.stream.XMLStreamReader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.lucastheisen.mp4tools.autotagger.tag.TagInfo.Rating;
import com.lucastheisen.xml.XmlPullUtils;
import com.lucastheisen.xml.XmlStreamProcessor;
import com.lucastheisen.xml.XmlTreeNode;


public class TagChimpRepository implements Repository {
    private static Logger log = LoggerFactory.getLogger( TagChimpRepository.class );
    private String accessToken;
    private String hostname;
    private String path;
    private String scheme;

    public TagChimpRepository( String scheme, String hostname, String path,
            String accessToken ) {
        this.scheme = scheme;
        this.hostname = hostname;
        this.path = path;
        this.accessToken = accessToken;
    }

    public URI getSearchUri( String title, int chapters )
            throws URISyntaxException {
        String query = new StringBuilder( "type=search&token=" )
                .append( accessToken )
                .append( "&title=" )
                .append( title )
                .append( "&totalChapters=" )
                .append( chapters )
                .toString();
        return new URI( scheme, null, hostname, -1, path, query, null );
    }

    /*
     * non Javadoc comment
     * 
     * Externalized to allow for unit testing.
     */
    List<TagInfo> process( InputStream inputStream ) {
        final List<TagInfo> tagInfoList = new ArrayList<>();
        XmlPullUtils.process(
                new XmlStreamProcessor() {
                    @Override
                    public void process( XMLStreamReader reader ) {
                        if ( XmlPullUtils.advanceToChildElement( reader, Schema.items.toString() ) ) {
                            if ( XmlPullUtils.advanceToChildElement( reader, Schema.movie.toString() ) ) {
                                do {
                                    if ( log.isTraceEnabled() )
                                        log.trace( "processing movie {}", reader.getLocalName() );
                                    tagInfoList.add( processMovie( reader ) );
                                    if ( log.isTraceEnabled() )
                                        log.trace( "finished processing movie {}", reader.getLocalName() );
                                } while ( XmlPullUtils.advanceToNextSiblingElement( reader ) );
                            }
                        }
                        else {
                            log.warn( "No <items> element found" );
                        }
                    }
                }, inputStream );
        return tagInfoList;
    }

    private TagInfo processMovie( XMLStreamReader reader ) {
        XmlTreeNode movie = XmlPullUtils.buildXmlNodeTree( reader );
        XmlTreeNode movieTags = movie.get( Schema.movieTags.toString(), 0 );
        XmlTreeNode info = movieTags.get( Schema.info.toString(), 0 );
        XmlTreeNode movieChapters = movie.get( Schema.movieChapters.toString(), 0 );

        TagInfo tagInfo = new TagInfo();
        tagInfo.setAmazonAsin( movie.getValue( Schema.amazonASIN.toString(), 0 ) );
        tagInfo.setImdbId( movie.getValue( Schema.imdbID.toString(), 0 ) );
        tagInfo.setITunesUrl( movie.getValue( Schema.iTunesURL.toString(), 0 ) );
        tagInfo.setNetflixId( movie.getValue( Schema.netflixID.toString(), 0 ) );
        tagInfo.setTagChimpId( movie.getValue( Schema.tagChimpID.toString(), 0 ) );
        tagInfo.setTitle( info.getValue( Schema.movieTitle.toString(), 0 ) );
        tagInfo.setReleaseDate( info.getValue( Schema.releaseDate.toString(), 0 ) );
        tagInfo.setTotalChapters( movieChapters.getValue( Schema.totalChapters.toString(), 0 ) );
        tagInfo.setGenre( info.getValue( Schema.genre.toString(), 0 ) );
        tagInfo.setRating( Rating.fromRating( info.getValue( Schema.rating.toString(), 0 ) ) );
        tagInfo.setShortDescription( info.getValue( Schema.shortDescription.toString(), 0 ) );
        tagInfo.setLongDescription( info.getValue( Schema.longDescription.toString(), 0 ) );
        try {
            String urlString = movieTags.getValue( Schema.coverArtSmall.toString(), 0 );
            Image.Type imageType = null;
            if ( urlString.endsWith( ".jpg" ) ) {
                imageType = Image.Type.jpg;
            }
            if ( urlString.endsWith( ".png" ) ) {
                imageType = Image.Type.png;
            }
            tagInfo.setImage( new Image( urlString, imageType ) );
        }
        catch ( MalformedURLException murle ) {
            if ( log.isWarnEnabled() )
                log.warn( "Invalid image url: {}", movieTags.getValue( Schema.coverArtSmall.toString(), 0 ) );
        }

        XmlTreeNode cast = info.get( Schema.cast.toString(), 0 );
        if ( cast != null ) {
            tagInfo.setCast( cast.getValues( Schema.actor.toString() ) );
        }
        XmlTreeNode directors = info.get( Schema.directors.toString(), 0 );
        if ( directors != null ) {
            tagInfo.setDirectors( directors.getValues( Schema.directors.toString() ) );
        }
        XmlTreeNode producers = info.get( Schema.producers.toString(), 0 );
        if ( producers != null ) {
            tagInfo.setProducers( producers.getValues( Schema.producers.toString() ) );
        }
        XmlTreeNode screenWriters = info.get( Schema.screenWriters.toString(), 0 );
        if ( screenWriters != null ) {
            tagInfo.setScreenWriters( screenWriters.getValues( Schema.screenWriters.toString() ) );
        }

        return tagInfo;
    }

    @Override
    public List<TagInfo> search( String title, int chapters ) {
        URL url;
        try {
            url = getSearchUri( title, chapters ).toURL();
        }
        catch ( MalformedURLException | URISyntaxException e ) {
            throw new RepositorySearchException( e.getMessage(), e );
        }

        try ( InputStream inputStream = url.openStream()) {
            return process( inputStream );
        }
        catch ( IOException e ) {
            throw new RepositorySearchException( e.getMessage(), e );
        }
    }

    private enum Schema {
        actor,
        amazonASIN,
        cast,
        coverArtSmall,
        directors,
        genre,
        imdbID,
        info,
        items,
        iTunesURL,
        longDescription,
        movie,
        movieChapters,
        movieTags,
        movieTitle,
        netflixID,
        producers,
        rating,
        releaseDate,
        screenWriters,
        shortDescription,
        tagChimpID,
        totalChapters;
    }
}
