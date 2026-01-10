package com.jiajin.simplerestserver.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ljj
 * @create 2025 12 16 17:18
 */
@Component
public class TransactionExecutor {

    private final PlatformTransactionManager txManager;

    public TransactionExecutor(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    /**
     * 可抛出异常的有返回值函数接口
     */
    @FunctionalInterface
    public interface TransactionCallable<T> {
        T call() throws Exception;
    }

    /**
     * 可抛出异常无返回值函数接口
     */
    @FunctionalInterface
    public interface TransactionRunnable {
        void run() throws Exception;
    }

    /**
     * 使用默认传播行为 REQUIRED 执行，有返回值
     */
    public <T> T execute(TransactionCallable<T> callable) throws Exception {
        return execute(Propagation.REQUIRED, callable);
    }

    /**
     * 使用指定传播行为执行，有返回值
     */
    public <T> T execute(Propagation propagation, TransactionCallable<T> callable) throws Exception {
        TransactionTemplate template = buildTemplate(propagation);
        AtomicReference<Exception> exRef = new AtomicReference<>();
        T result = template.execute(status -> {
            try {
                return callable.call();
            } catch (Exception e) {
                status.setRollbackOnly();
                exRef.set(e);
                return null;
            }
        });
        if (exRef.get() != null) {
            throw exRef.get();
        }
        return result;
    }

    /**
     * 使用默认传播行为 REQUIRED 执行，无返回值
     */
    public void execute(TransactionRunnable runnable) throws Exception {
        execute(Propagation.REQUIRED, runnable);
    }

    /**
     * 使用 REQUIRED_NEW 执行，无返回值
     */
    public void executeRequireNew(TransactionRunnable runnable) throws Exception {
        execute(Propagation.REQUIRES_NEW, runnable);
    }

    /**
     * 使用指定传播行为执行，无返回值
     */
    public void execute(Propagation propagation, TransactionRunnable runnable) throws Exception {
        TransactionTemplate template = buildTemplate(propagation);
        AtomicReference<Exception> exRef = new AtomicReference<>();
        template.execute(status -> {
            try {
                runnable.run();
            } catch (Exception e) {
                status.setRollbackOnly();
                exRef.set(e);
            }
            return null;
        });
        if (exRef.get() != null) {
            throw exRef.get();
        }
    }

    private TransactionTemplate buildTemplate(Propagation propagation) {
        TransactionTemplate template = new TransactionTemplate(txManager);
        template.setPropagationBehavior(mapPropagation(propagation));
        return template;
    }

    private int mapPropagation(Propagation propagation) {
        if (propagation == null) return TransactionDefinition.PROPAGATION_REQUIRED;
        switch (propagation) {
            case REQUIRED:
                return TransactionDefinition.PROPAGATION_REQUIRED;
            case REQUIRES_NEW:
                return TransactionDefinition.PROPAGATION_REQUIRES_NEW;
            case SUPPORTS:
                return TransactionDefinition.PROPAGATION_SUPPORTS;
            case NOT_SUPPORTED:
                return TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
            case MANDATORY:
                return TransactionDefinition.PROPAGATION_MANDATORY;
            case NEVER:
                return TransactionDefinition.PROPAGATION_NEVER;
            case NESTED:
                return TransactionDefinition.PROPAGATION_NESTED;
            default:
                return TransactionDefinition.PROPAGATION_REQUIRED;
        }
    }
}