package com.readonlydev.logback;

import java.io.File;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;

@NoAutoStart
public class SessionTriggeringPolicy<E> extends SizeAndTimeBasedFNATP<E>
{
    private boolean started = false;

    @Override
    public boolean isTriggeringEvent( File activeFile, E event ) {
        if ( !started ) {
            nextCheck = 0L;
            return started = true;
        }

        return super.isTriggeringEvent( activeFile, event );
    };
}
