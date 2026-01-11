package com.jiajin.simplerestserver.infrastructure.tx;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransactionExecutor {

    private final PlatformTransactionManager txManager;

    public TransactionExecutor(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    /* ====================== Functional Interfaces ====================== */

    @FunctionalInterface
    public interface TxCallable<T> {
        T call() throws Exception;
    }

    @FunctionalInterface
    public interface TxRunnable {
        void run() throws Exception;
    }

    /* ====================== Public API ====================== */

    /** 默认 REQUIRED，有返回值 */
    public <T> T required(TxCallable<T> callable) throws Exception {
        return execute(Propagation.REQUIRED, callable);
    }

    /** REQUIRED_NEW，有返回值 */
    public <T> T requiresNew(TxCallable<T> callable) throws Exception {
        return execute(Propagation.REQUIRES_NEW, callable);
    }

    /** 指定传播行为，有返回值 */
    public <T> T execute(Propagation propagation, TxCallable<T> callable) throws Exception {
        TransactionTemplate template = buildTemplate(propagation);
        try {
            return template.execute(status -> {
                try {
                    return callable.call();
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new TransactionExecutionException(e);
                }
            });
        } catch (TransactionExecutionException e) {
            throw unwrap(e);
        }
    }

    /** 默认 REQUIRED，无返回值 */
    public void required(TxRunnable runnable) throws Exception {
        execute(Propagation.REQUIRED, runnable);
    }

    /** REQUIRED_NEW，无返回值 */
    public void requiresNew(TxRunnable runnable) throws Exception {
        execute(Propagation.REQUIRES_NEW, runnable);
    }

    /** 指定传播行为，无返回值 */
    public void execute(Propagation propagation, TxRunnable runnable) throws Exception {
        TransactionTemplate template = buildTemplate(propagation);
        try {
            template.execute(status -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new TransactionExecutionException(e);
                }
                return null;
            });
        } catch (TransactionExecutionException e) {
            throw unwrap(e);
        }
    }

    /* ====================== Internal ====================== */

    private TransactionTemplate buildTemplate(Propagation propagation) {
        TransactionTemplate template = new TransactionTemplate(txManager);
        template.setPropagationBehavior(mapPropagation(propagation));
        return template;
    }

    private int mapPropagation(Propagation propagation) {
        return propagation == null
                ? TransactionDefinition.PROPAGATION_REQUIRED
                : propagation.value();
    }

    private Exception unwrap(TransactionExecutionException e) throws Exception {
        Throwable cause = e.getCause();
        if (cause instanceof Exception) {
            return (Exception) cause;
        }
        throw new RuntimeException(cause);
    }
}
