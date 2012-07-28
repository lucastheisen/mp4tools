package com.lucastheisen.perl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PerlHash implements Iterable<PerlHash> {
    private Map<String, PerlHash> map;
    private List<PerlHash> list;
    private Object value;
    private Mode mode = Mode.Undefined;

    public PerlHash() {
    }

    private PerlHash( Object value ) {
        mode = Mode.Scalar;
        this.value = value;
    }

    public void add( Object value ) {
        if ( mode != Mode.List ) {
            mode = Mode.List;
            list = new ArrayList<>();
        }
        list.add( toPerlHash( value ) );
    }

    public PerlHash get( int index ) {
        if ( mode == Mode.Map ) {
            return map.get( index );
        }
        else if ( mode == Mode.List ) {
            return list.get( index );
        }
        throw new PerlHashTypeException( "Not a list or map" );
    }

    public PerlHash get( String key ) {
        if ( mode == Mode.Map ) {
            return map.get( key );
        }
        if ( mode == Mode.List ) {
            return list.get( Integer.parseInt( key ) );
        }
        throw new PerlHashTypeException( "Not a list or map" );
    }

    @Override
    public Iterator<PerlHash> iterator() {
        if ( mode == Mode.List ) {
            return list.iterator();
        }
        throw new PerlHashTypeException( "Not a list" );
    }

    public Set<String> keySet() {
        if ( mode == Mode.Map ) {
            return map.keySet();
        }
        throw new PerlHashTypeException( "Not a map" );
    }

    public void put( String key, Object value ) {
        if ( mode != Mode.Map ) {
            mode = Mode.Map;
            map = new HashMap<>();
        }
        map.put( key, toPerlHash( value ) );
    }

    public Object value() {
        switch ( mode ) {
        case List:
            return list;
        case Map:
            return map;
        case Scalar:
            return value;
        case Undefined:
        default:
            return null;
        }
    }

    public List<PerlHash> valueList() {
        if ( mode == Mode.List ) {
            return list;
        }
        throw new PerlHashTypeException( "Not a list" );
    }

    public Map<String, PerlHash> valueMap() {
        if ( mode == Mode.Map ) {
            return map;
        }
        throw new PerlHashTypeException( "Not a map" );
    }

    private PerlHash toPerlHash( Object value ) {
        return value instanceof PerlHash ? (PerlHash) value
                : new PerlHash( value );
    }

    private enum Mode {
        List, Map, Scalar, Undefined
    }
}
