package net.kuper.tz.console.service;

import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import net.kuper.tz.console.config.LoggerQueue;
import net.kuper.tz.console.entity.LogMessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class LogbackService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * 推送日志到/topic/pullLogger
     */
//    @PostConstruct
    public void pushLogger() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info("starting push log");
                while (true) {
                    try {
                        LogMessageEntity log = LoggerQueue.getInstance().poll();
                        if (log != null) {
                            // 格式化异常堆栈信息
                            if ("ERROR".equals(log.getLevel())) {
                                log.setBody("<pre>" + log.getBody() + "</pre>");
                            }
                            if (log.getClassName().equals("jdbc.resultsettable")) {
                                log.setBody("<br><pre>" + log.getBody() + "</pre>");
                            }
                            if (messagingTemplate != null) {
                                messagingTemplate.convertAndSend("/topic/logMsg", log);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        executorService.submit(runnable);
    }
}
