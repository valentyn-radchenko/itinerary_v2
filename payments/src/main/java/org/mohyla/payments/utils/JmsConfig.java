package org.mohyla.payments.utils;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JmsConfig {

    @Bean
    public JmsTemplate queueJmsTemplate(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setPubSubDomain(false);
        return template;
    }

    @Bean
    public JmsTemplate topicJmsTemplate(@Qualifier("jmsConnectionFactory")ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setPubSubDomain(true);
        return template;
    }
    @Bean
    public JmsListenerContainerFactory<?> queueListenerFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }
}