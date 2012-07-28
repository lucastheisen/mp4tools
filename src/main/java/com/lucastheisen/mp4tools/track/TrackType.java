package com.lucastheisen.mp4tools.track;

import java.io.File;
import java.util.regex.Pattern;


import com.lucastheisen.mp4tools.edit.IsoParserMuxer.FormatNotSupportedException;

public enum TrackType {
    h264("^.*\\.h264$"),
    aac("^.*\\.aac$");

    private Pattern pattern;

    private TrackType( String regex ) {
        if ( regex != null ) {
            pattern = Pattern.compile( regex );
        }
    }

    public static TrackType forFile( File file ) {
        String filename = file.getName();
        for ( TrackType supportedFormat : values() ) {
            if ( supportedFormat.pattern.matcher( filename ).matches() ) {
                return supportedFormat;
            }
        }
        throw new FormatNotSupportedException( "unable to determine format of "
                + filename );
    }
}