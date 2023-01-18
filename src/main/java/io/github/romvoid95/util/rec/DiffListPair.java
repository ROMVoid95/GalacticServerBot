package io.github.romvoid95.util.rec;

import java.util.List;

public record DiffListPair<T, K> (List<T> left, List<K> right)
{
    public static <T, K> DiffListPair<T, K> of(List<T> left, List<K> right) {
        return new DiffListPair<>(left, right);
    }
}
