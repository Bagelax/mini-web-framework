package webserver;

import annotations.*;
import exceptions.DependencyInjectionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DIEngine {
    private DependencyContainer dc;
    private HashMap<Class, Object> singletons;
    private Set<Integer> hashes;

    public DIEngine() {
        this.dc = new DependencyContainer();
        this.singletons = new HashMap<>();
        this.hashes = new HashSet<>();
    }

    public void initializeFields(Object o)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, DependencyInjectionException {
        for (Field field : o.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Autowired.class)) continue;

            Autowired conf = field.getAnnotation(Autowired.class);
            field.setAccessible(true);
            Class c;
            if (field.getType().isInterface()) {
                if (!field.isAnnotationPresent(Qualifier.class))
                    throw new DependencyInjectionException(
                            String.format("field %s is an interface instance with Autowired but without Qualifier annotation.", field.getName()));
                Qualifier q = field.getAnnotation(Qualifier.class);
                c = this.dc.get(q.value());
            }
            else {
                c = field.getType();
            }
            Object newVal;
            String beanValue = "";

            if (c.isAnnotationPresent(Bean.class))
                beanValue = ((Bean) c.getAnnotation(Bean.class)).scope();

            if (beanValue.equals("singleton") || c.isAnnotationPresent(Service.class))
                newVal = this.getSingleton(c);
            else if (beanValue.equals("prototype") || c.isAnnotationPresent(Component.class))
                newVal = c.getDeclaredConstructor().newInstance();
            else
                throw new DependencyInjectionException("Invalid Bean scope for class " + c.getName() + ".");

            field.set(o, newVal);
            if (conf.verbose())
                System.out.println(String.format("Initialized %s %s in %s on %s with %s",
                        field.getType(),
                        field.getName(),
                        o.getClass().getName(),
                        LocalDateTime.now(),
                        newVal.hashCode()));
            if (!this.initialized(newVal))
                initializeFields(newVal);
        }
        this.hashes.add(o.hashCode());
    }

    public void register(String name, Class c) throws DependencyInjectionException {
        this.dc.register(name, c);
    }

    private Object getSingleton(Class c) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!this.singletons.containsKey(c))
            this.singletons.put(c, c.getDeclaredConstructor().newInstance());
        return this.singletons.get(c);
    }

    private boolean initialized(Object o) {
        return this.hashes.contains(o.hashCode());
    }
}
