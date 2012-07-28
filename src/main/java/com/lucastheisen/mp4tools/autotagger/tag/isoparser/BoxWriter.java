package com.lucastheisen.mp4tools.autotagger.tag.isoparser;

import com.coremedia.iso.boxes.Box;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo;

public interface BoxWriter<T extends Box> {
    public void write( T parentBox, TagInfo tagInfo );
}
