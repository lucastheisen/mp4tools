package com.lucastheisen.mp4tools.autotagger.tag.isoparser;


import com.coremedia.iso.boxes.Box;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo;


public interface BoxParser<T extends Box> {
    /**
     * Parses the provided box loading the fields in <code>tagInfo</code> that
     * it contains information for.
     * 
     * @param box A Box containing information
     * @param tagInfo The TagInfo to populate with data
     */
    public void parse( T box, TagInfo tagInfo );
}
