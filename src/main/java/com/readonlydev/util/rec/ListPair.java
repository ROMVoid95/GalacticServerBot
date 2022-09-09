package com.readonlydev.util.rec;

import java.util.List;

public record ListPair<T> (List<T> left, List<T> right)
{
    public ListPair(List<T> left, List<T> right) {
        this.left = left;
        this.right = right;
    }

    public static <T> ListPair<T> of(List<T> left, List<T> right) {
        return new ListPair<>(left, right);
    }
}
