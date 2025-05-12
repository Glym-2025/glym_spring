package glym.glym_spring.global.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContextProvider.class);
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        log.info("ApplicationContext initialized");
    }

    public static MessageSource getMessageSource() {
        if (context == null) {
            log.error("ApplicationContext is not initialized");
            throw new IllegalStateException("ApplicationContext is not initialized");
        }
        return context.getBean(MessageSource.class);
    }
}