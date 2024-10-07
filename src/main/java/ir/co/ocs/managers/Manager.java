package ir.co.ocs.managers;

import ir.co.ocs.envoriments.Server;

import java.util.List;

public interface Manager<T> {

    T getInstance();

    void add(T... environments);

    void remove(String... identifiers);

    T get(String identifier);

    void shutdown();


}
