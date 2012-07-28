package com.lucastheisen.mp4tools.autotagger.tag.isoparser;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.coremedia.iso.boxes.Box;


public class TypeSafeParserMap {
    private Map<Class<?>, BoxParser<?>> unsafeMap;

    public TypeSafeParserMap() {
        unsafeMap = new HashMap<>();
    }

    public <T extends Box> void add( Class<T> clazz, BoxParser<T> parser ) {
        unsafeMap.put( clazz, parser );
    }

    public <T extends Box> void addAll( List<ParserMapEntry<T>> list ) {
        for ( ParserMapEntry<T> entry : list ) {
            unsafeMap.put( entry.clazz, entry.parser );
        }
    }

    public <T extends Box> boolean containsKey( Class<T> clazz ) {
        return unsafeMap.containsKey( clazz );
    }

    @SuppressWarnings( "unchecked" )
    public <T extends Box> BoxParser<T> get( Class<T> clazz ) {
        return (BoxParser<T>) unsafeMap.get( clazz );
    }

    /**
     * Bean method used for dependency injection. Simply calls
     * {@link #addAll(List)}.
     * 
     * @param entries
     *            The entries of the map.
     */
    public <T extends Box> void setMap( List<ParserMapEntry<T>> entries ) {
        addAll( entries );
    }

    public class ParserMapEntry<T extends Box> {
        private Class<T> clazz;
        private BoxParser<T> parser;

        public ParserMapEntry( Class<T> clazz, BoxParser<T> parser ) {
            this.clazz = clazz;
            this.parser = parser;
        }
    }
}