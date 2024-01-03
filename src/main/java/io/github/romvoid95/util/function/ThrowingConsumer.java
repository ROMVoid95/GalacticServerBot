package io.github.romvoid95.util.function;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

    void accept(T value) throws E;

}