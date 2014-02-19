package org.motechproject.mds.jdo;

import org.motechproject.mds.builder.MDSClassLoader;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.transaction.TransactionDefinition;

public class MdsTransactionManager extends JdoTransactionManager {

    private static final long serialVersionUID = 3817917722565508554L;

    private ClassLoader webAppClassLoader = Thread.currentThread().getContextClassLoader();

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        Thread.currentThread().setContextClassLoader(MDSClassLoader.getInstance());
        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        try {
            super.doCleanupAfterCompletion(transaction);
        } finally {
            Thread.currentThread().setContextClassLoader(webAppClassLoader);
        }
    }
}
