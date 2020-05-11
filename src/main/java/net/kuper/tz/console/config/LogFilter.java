package net.kuper.tz.console.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import net.kuper.tz.console.entity.LogMessageEntity;

import java.text.DateFormat;
import java.util.Date;

/**
 * 定义Logfilter拦截输出日志
 * @author jie
 * @reference https://cloud.tencent.com/developer/article/1096792
 * @date 2018-12-24
 */
public class LogFilter extends Filter<ILoggingEvent>{

    @Override
    public FilterReply decide(ILoggingEvent event) {
        LogMessageEntity loggerMessage = new LogMessageEntity(
                event.getFormattedMessage(),
                DateFormat.getDateTimeInstance().format(new Date(event.getTimeStamp())),
                event.getThreadName(),
                event.getLoggerName(),
                event.getLevel().levelStr
        );
        LoggerQueue.getInstance().push(loggerMessage);
        return FilterReply.ACCEPT;
    }
}