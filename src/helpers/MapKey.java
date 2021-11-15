package helpers;

import java.util.Objects;

public class MapKey {
    public MapKey(String path, String method) {
        this.path = path;
        this.method = method;
    }
    private String path, method;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapKey mapKey = (MapKey) o;
        return path.equals(mapKey.path) && method.equals(mapKey.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method);
    }

    @Override
    public String toString() {
        return this.method + " " + this.path;
    }
}
