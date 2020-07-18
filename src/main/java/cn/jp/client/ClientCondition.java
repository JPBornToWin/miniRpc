package cn.jp.client;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

public class ClientCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Boolean flag =  conditionContext.getEnvironment().getProperty("mini-rpc.clientService", Boolean.class);

        if (flag == null) {
            return false;
        }

        return flag;
    }
}
