package io.github.romvoid95.util.function;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {

    void run() throws E;

}