package io.github.romvoid95.util.function;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable>
{
    R apply(T t) throws E;

    static <T, S extends Exception> ThrowingFunction<T, T, S> identity() {
        return t -> t;
    }

    /**
     * Map throwing function in default stream api with exception consume support
     *
     * @param function the function to apply
     * @param exceptionHandler the exception consumer
     * @param <T> type of function parameter
     * @param <R> type of function result
     * @param <E> type of exception
     * @return a new function
     */
    @SuppressWarnings("unchecked")
    static <T, R, E extends Throwable> Function<T, R> asFunction(ThrowingFunction<T, R, E> function, Function<E, R> exceptionHandler) {
        return value -> {
            try {
                return function.apply(value);
            } catch (Throwable exception) {
                return exceptionHandler.apply((E) exception);
            }
        };
    }
}
