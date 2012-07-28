package com.lucastheisen.mp4tools.edit;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;


public class IsoParserJoiner implements Joiner {
    private static Logger logger = LoggerFactory.getLogger( IsoParserJoiner.class );

    public void join( File output, File... parts ) throws IOException {
        if ( logger.isDebugEnabled() ) {
            StringBuilder builder = new StringBuilder();
            boolean first = false;
            for ( File part : parts ) {
                if ( first ) {
                    first = false;
                }
                else {
                    builder.append( ", " );
                }
                builder.append( part.getAbsolutePath() );
            }
            logger.debug( "combining parts: ", builder );
        }

        InputStream[] inputStreams = new InputStream[parts.length];
        RandomAccessFile outputFile = null;
        try {
            List<Track> videoTracks = new LinkedList<Track>();
            List<Track> audioTracks = new LinkedList<Track>();
            Movie[] movieParts = new Movie[parts.length];

            int index = 0;
            for ( File part : parts ) {
                InputStream inputStream = new FileInputStream( part );
                inputStreams[index] = inputStream;
                movieParts[index] = MovieCreator.build( Channels.newChannel( inputStream ) );
                index++;
            }

            for ( Movie movie : movieParts ) {
                for ( Track track : movie.getTracks() ) {
                    if ( track.getHandler().equals( "soun" ) ) {
                        audioTracks.add( track );
                    }
                    if ( track.getHandler().equals( "vide" ) ) {
                        videoTracks.add( track );
                    }
                }
            }

            Movie result = new Movie();

            if ( audioTracks.size() > 0 ) {
                result.addTrack( new AppendTrack( audioTracks.toArray( new Track[audioTracks.size()] ) ) );
            }
            if ( videoTracks.size() > 0 ) {
                result.addTrack( new AppendTrack( videoTracks.toArray( new Track[videoTracks.size()] ) ) );
            }

            IsoFile out = new DefaultMp4Builder().build( result );
            outputFile = new RandomAccessFile( output, "rw" );
            FileChannel channel = outputFile.getChannel();
            channel.position( 0 );
            out.getBox( channel );
            channel.close();
        }
        finally {
            for ( InputStream inputStream : inputStreams ) {
                if ( inputStream != null ) {
                    inputStream.close();
                }
            }
            if ( outputFile != null ) {
                outputFile.close();
            }
        }
    }

    public static void main( String[] args ) throws IOException {
        if ( args.length < 3 ) {
            logger.error( "at least 3 arguments are required" );
            System.exit( 1 );
        }

        List<File> parts = new ArrayList<File>();
        File output = new File( args[0] );
        for ( int i = 1; i < args.length; i++ ) {
            parts.add( new File( args[i] ) );
        }

        logger.info( "joining parts" );
        new IsoParserJoiner().join( output, parts.toArray( new File[parts.size()] ) );
        logger.info( "done" );
    }
}
