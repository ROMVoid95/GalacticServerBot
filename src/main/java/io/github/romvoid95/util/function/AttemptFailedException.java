package io.github.romvoid95.util.function;

public final class AttemptFailedException extends RuntimeException
{
    public AttemptFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttemptFailedException(Throwable throwable) {
        super(throwable);
    }    
}
