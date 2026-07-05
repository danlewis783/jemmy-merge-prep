package org.netbeans.jemmy;

public interface TimeoutOverride extends AutoCloseable {

    /**
     * TimeoutOverride is now {@link AutoCloseable} so it can be used in Java 8 try-with-resources
     * @see AutoCloseable#close()
     */
    void cancel();

    long get();
    TimeoutKey key();

    // overriding (but not implementing) the abstract method here to remove the 
    // "throws Exception" part, for more compact typical usage
    @Override
    void close();
}
