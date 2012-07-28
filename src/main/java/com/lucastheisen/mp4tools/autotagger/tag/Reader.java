package com.lucastheisen.mp4tools.autotagger.tag;


import java.io.File;


public interface Reader {
    /**
     * Reads an mp4 files tags and returns a TagInfo containing that
     * information.
     * 
     * @param file
     *            The mp4 file
     * @return A TagInfo
     * @throws AutotaggerIOException
     *             if unable to read the file
     */
    public TagInfo read( File file );
}
