package oose.euphoria.backend.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    @Setter
    @Getter
    private static ApplicationContext context;

    /**
     * Returns the bean based on the incoming class
     *
     * @param beanClass
     * @param <T>
     * @return BEANS
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    /**
     * Sets the current context for the static getter and setter
     *
     * @param context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        setContext(context);
    }
}
