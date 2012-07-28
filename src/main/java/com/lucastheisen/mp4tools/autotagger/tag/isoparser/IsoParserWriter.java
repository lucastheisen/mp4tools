package com.lucastheisen.mp4tools.autotagger.tag.isoparser;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;


import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.MetaBox;
import com.googlecode.mp4parser.util.Path;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo;
import com.lucastheisen.mp4tools.autotagger.tag.Writer;


public class IsoParserWriter implements Writer {
    public BoxWriter<MetaBox> boxWriter;

    public IsoParserWriter( BoxWriter<MetaBox> boxWriter ) {
        this.boxWriter = boxWriter;
    }

    @Override
    public void write( File file, TagInfo tagInfo ) {
        try (
                FileChannel rChannel = new RandomAccessFile( file, "r" ).getChannel() ;
                FileChannel wChannel = new RandomAccessFile( file, "rw" ).getChannel() ;) {
            IsoFile isoFile = new IsoFile( rChannel );

            MetaBox metaBox = (MetaBox) Path.getPath( isoFile, "/moov[0]/udta[0]/meta[0]" );

            boxWriter.write( metaBox, tagInfo );

            isoFile.getBox( wChannel );
        }
        catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
