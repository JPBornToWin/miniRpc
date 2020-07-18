package cn.jp.server;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ServerCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Boolean flag =  conditionContext.getEnvironment().getProperty("mini-rpc.serverService", Boolean.class);
        if (flag == null) {
            return false;
        }

        return flag;
    }
}
