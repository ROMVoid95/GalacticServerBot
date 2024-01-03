package io.github.romvoid95.util.function;

public final class Nothing
{
    public static final Nothing NADA = new Nothing();

    public Nothing() {}

    public Void toVoid() {
        return voidness();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Nothing;
    }

    public static Void voidness() {
        return null;
    }    
}
