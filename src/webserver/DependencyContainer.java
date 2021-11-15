package webserver;

import exceptions.DependencyInjectionException;

import java.util.HashMap;

public class DependencyContainer {
    private HashMap<String, Class> map;

    public DependencyContainer() {
        this.map = new HashMap<>();
    }

    public void register(String name, Class cls) throws DependencyInjectionException {
        if (this.map.containsKey(name))
            throw new DependencyInjectionException("Multiple definitions of " + name + '.');
        this.map.put(name, cls);
    }

    public Class get(String name) throws DependencyInjectionException {
        Class ret = this.map.get(name);
        if (ret == null)
            throw new DependencyInjectionException(name + " was never defined.");
        return ret;
    }
}
