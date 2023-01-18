package io.github.romvoid95.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import io.github.romvoid95.Conf;

public class LogFilter extends Filter<ILoggingEvent> {
    public static final Level LEVEL = Conf.Bot().getDebug() ? Level.DEBUG : Level.INFO;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (event.getLevel().isGreaterOrEqual(Level.DEBUG)) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }
}
