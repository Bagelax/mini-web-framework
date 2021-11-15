package helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MapVal {
    Object o;
    Method m;
    public MapVal(Object o, Method m) {
        this.o = o;
        this.m = m;
    }

    public Object invoke(Object ...objects) throws InvocationTargetException, IllegalAccessException {
        return m.invoke(o, objects);
    }
}

