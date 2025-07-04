package org.rifaii.tuum.audit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataChangeNotifier {

    private final RabbitTemplate rabbitTemplate;
    private static final String ROUTING_KEY_FMT = "data.change.%s";

    public DataChangeNotifier(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notify(DataChangeLog dataChangeLog) {
        String routingKey = ROUTING_KEY_FMT.formatted(dataChangeLog.operationType());
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, routingKey, dataChangeLog);
    }


}
