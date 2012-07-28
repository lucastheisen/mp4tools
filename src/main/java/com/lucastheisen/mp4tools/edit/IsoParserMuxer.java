package com.lucastheisen.mp4tools.edit;


import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.builder.FragmentedMp4Builder;
import com.googlecode.mp4parser.authoring.builder.SyncSampleIntersectFinderImpl;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.H264TrackImpl;


public class IsoParserMuxer implements Muxer {
    private static Logger logger = LoggerFactory.getLogger( IsoParserMuxer.class );

    public void mux( File output, File... parts ) throws IOException {
        mux( output, false, parts );
    }

    @Override
    public void mux( File output, boolean fragmented, File... parts )
            throws IOException {
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

        List<Closeable> closeables = new ArrayList<Closeable>();

        try {
            Movie movie = new Movie();
            for ( File part : parts ) {
                SupportedFormat format = SupportedFormat.forFile( part );
                // InputStream inputStream = new BufferedInputStream( new FileInputStream( part ) );
                InputStream inputStream = new FileInputStream( part );
                closeables.add( inputStream );
                switch ( format ) {
                case h264:
                    movie.addTrack( new H264TrackImpl( inputStream ) );
                    break;
                case aac:
                    movie.addTrack( new AACTrackImpl( inputStream ) );
                    break;
                }
            }

            if ( fragmented ) {
                FragmentedMp4Builder fragmentedMp4Builder = new FragmentedMp4Builder();
                fragmentedMp4Builder.setIntersectionFinder( new SyncSampleIntersectFinderImpl() );
                IsoFile out = fragmentedMp4Builder.build( movie );
                try ( FileOutputStream fileOutputStream = new FileOutputStream( output )) {
                    out.getBox( fileOutputStream.getChannel() );
                }
            }
            else {
                IsoFile out = new DefaultMp4Builder().build( movie );
                try ( FileOutputStream fileOutputStream = new FileOutputStream( output )) {
                    out.getBox( fileOutputStream.getChannel() );
                }
            }
        }
        finally {
            for ( Closeable closeable : closeables ) {
                closeable.close();
            }
        }
    }

    public static enum SupportedFormat {
        h264("^.*\\.h264$"),
        aac("^.*\\.aac$");

        private Pattern pattern;

        private SupportedFormat( String regex ) {
            if ( regex != null ) {
                pattern = Pattern.compile( regex );
            }
        }

        public static SupportedFormat forFile( File file ) {
            String filename = file.getName();
            for ( SupportedFormat supportedFormat : values() ) {
                if ( supportedFormat.pattern.matcher( filename ).matches() ) {
                    return supportedFormat;
                }
            }
            throw new FormatNotSupportedException( "unable to determine format of "
                    + filename );
        }
    }

    public static class FormatNotSupportedException extends RuntimeException {
        private static final long serialVersionUID = -5142075710302473606L;

        public FormatNotSupportedException( String message ) {
            super( message );
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

        logger.info( "muxing parts" );
        new IsoParserMuxer().mux( output, parts.toArray( new File[parts.size()] ) );
        logger.info( "done" );
    }
}
