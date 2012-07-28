package com.lucastheisen.mp4tools.console;

class BlockText {
    private String text;
    private String leftPad;
    private int width;
    private String asString;

    BlockText( String text ) {
        this( text, "" );
    }

    BlockText( String text, String leftPad ) {
        this( text, leftPad, 80 );
    }

    BlockText( String text, String leftPad, int width ) {
        this.text = text;
        this.leftPad = leftPad;
        this.width = width - leftPad.length();
    }

    @Override
    public String toString() {
        if ( text == null ) return null;
        if ( asString == null ) {
            StringBuilder builder = new StringBuilder( leftPad );

            char[] wordBuffer = new char[width];

            int wordIndex = 0;
            int lineIndex = 0;
            char c;
            for ( int i = 0, count = text.length(); i < count; i++ ) {
                if ( wordIndex == width ) {
                    builder.append( wordBuffer );
                    wordIndex = 0;
                    lineIndex = 0;
                }

                c = text.charAt( i );
                switch ( c ) {
                case '\t':
                case ' ':
                    if ( lineIndex < width ) {
                        builder.append( wordBuffer, 0, wordIndex );
                    }
                    else {
                        builder.append( '\n' ).append( leftPad ).append( wordBuffer, 0, wordIndex );
                        lineIndex = wordIndex;
                    }

                    if ( ++lineIndex < width ) {
                        builder.append( ' ' );
                    }
                    else {
                        builder.append( '\n' ).append( leftPad ).append( wordBuffer, 0, wordIndex );
                        lineIndex = 0;
                    }
                    wordIndex = 0;
                    break;
                case '\r':
                    int nextIndex = i + 1;
                    if ( nextIndex < count
                            && text.charAt( nextIndex ) == '\n' ) {
                        i++;
                    }
                    // fall through on purpose...
                case '\n':
                    if ( lineIndex >= width ) {
                        builder.append( '\n' );
                    }
                    builder.append( wordBuffer, 0, wordIndex ).append( '\n' );
                    lineIndex = 0;
                    wordIndex = 0;
                    break;
                default:
                    wordBuffer[wordIndex++] = c;
                    lineIndex++;
                }
            }

            // last word...
            if ( lineIndex < width ) {
                builder.append( wordBuffer, 0, wordIndex );
            }
            else {
                builder.append( '\n' ).append( leftPad ).append( wordBuffer, 0, wordIndex );
            }

            asString = builder.toString();
        }
        return asString;
    }
}