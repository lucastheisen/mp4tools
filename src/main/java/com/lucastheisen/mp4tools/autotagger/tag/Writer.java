package com.lucastheisen.mp4tools.autotagger.tag;


import java.io.File;


public interface Writer {
    public void write( File file, TagInfo tagInfo );
}
