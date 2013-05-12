package org.kootox.episodesmanager.services;

import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.kootox.episodesmanager.EpisodesManagerConfig;
import org.kootox.episodesmanager.EpisodesManagerTopiaRootContextSupplierFactory;
import org.mockito.Mockito;
import org.nuiton.topia.TopiaContext;
import org.nuiton.topia.TopiaException;
import org.nuiton.topia.TopiaNotFoundException;
import org.nuiton.topia.TopiaRuntimeException;
import org.nuiton.util.DateUtil;

/**
 * @author jcouteau <couteau@codelutin.com>
 */
public class FakeServiceContext implements MethodRule, ServiceContext {

    protected Supplier<TopiaContext> rootContextSupplier;

    protected static class MyStatement extends Statement {

        protected FakeServiceContext fakeServiceContext;

        protected Statement base;

        public MyStatement(FakeServiceContext fakeServiceContext, Statement base) {
            this.fakeServiceContext = fakeServiceContext;
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            fakeServiceContext.before();
            try {
                base.evaluate();
            } finally {
                fakeServiceContext.after();
            }
        }
    }

    private static final Log log = LogFactory.getLog(FakeServiceContext.class);

    protected ServiceFactory serviceFactory;

    protected TopiaContext rootContext;

    protected TopiaContext transaction;

    /**
     * A time-stamp, allow to make multiple build and keep the tests data.
     */
    protected String time = String.valueOf(System.nanoTime());

    /**
     * The name of the test (the class name).
     */
    protected String className;

    /**
     * The name of the test (the method name).
     */
    protected String methodName;

    protected Date fakeCurrentTime;

    protected File storage;

    @Override
    public Statement apply(final Statement base,
                           FrameworkMethod method, Object target) {
        methodName = method.getName();
        return new MyStatement(this, base);
    }

    /**
     * Main constructor to be used inline, when declaring the field annoted
     * with @Rule in the test needing a database.
     *
     * @param testClass returned by a getClass() call in the test you're writing
     */
    public FakeServiceContext(Class testClass) {
        className = testClass.getName();
    }

    protected File getTestSpecificDirectory() {
        // Trying to look for the temporary folder to store data for the test
        String tempDirPath = System.getProperty("java.io.tmpdir");
        if (tempDirPath == null) {
            // can this really occur ?
            tempDirPath = "";
            if (log.isWarnEnabled()) {
                log.warn("'\"java.io.tmpdir\" not defined");
            }
        }
        File tempDirFile = new File(tempDirPath);

        // create the directory to store database data
        String dataBasePath = className
                + File.separator // a directory with the test class name
                + methodName // a sub-directory with the method name
                + '_'
                + time; // and a timestamp
        File databaseFile = new File(tempDirFile, dataBasePath);
        return databaseFile;
    }

    /**
     * Will be called by JUnit before the test.
     * <p/>
     * Create the temp directory and configure Topia to store data in H2.
     */
    public void before() throws IOException, TopiaNotFoundException {
        EpisodesManagerTopiaRootContextSupplierFactory factory =
                new EpisodesManagerTopiaRootContextSupplierFactory();
        rootContextSupplier = factory.newEmbeddedDatabase(getTestSpecificDirectory());
        rootContext = rootContextSupplier.get();
    }

    public Supplier<TopiaContext> getRootContextSupplier() {
        return rootContextSupplier;
    }

    /**
     * Called by JUnit after the test: we close the context.
     */
    public void after() {
        if (!rootContext.isClosed()) {
            try {
                rootContext.closeContext();
            } catch (TopiaException e) {
                throw new TopiaRuntimeException(e);
            }
        }
    }

    /**
     * May be used in test to get a fresh transaction.
     */
    @Override
    public TopiaContext getTransaction() {
        TopiaContext transaction;

        try {
            transaction = rootContext.beginTransaction();
        } catch (TopiaException e) {
            throw new TopiaRuntimeException(e);
        }

        return transaction;
    }

    public void setCurrentTime(Date fakeCurrentTime) {
        this.fakeCurrentTime = fakeCurrentTime;
    }

    public void setCurrentTime(int day, int month, int year) {
        Date date = DateUtil.createDate(day, month, year);
        setCurrentTime(date);
    }

    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public <E extends EpisodesManagerService> E newService(Class<E> clazz) {
        return serviceFactory.newService(clazz, this);
    }

    @Override
    public void closeTransaction(TopiaContext transaction) {
        try {
            if (transaction != null) {
                transaction.closeContext();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot close topia context, not opened");
                }
            }
        } catch (TopiaException te) {
            log.fatal("Could not close context", te);
        }
    }

    @Override
    public EpisodesManagerConfig getEpisodesManagerConfig() {

        EpisodesManagerConfig mock = Mockito.mock(EpisodesManagerConfig.class);
        Mockito.when(mock.getTheTvDbLastUpdated()).thenReturn(1L);
		Mockito.when(mock.getTheTvDbApiKey()).thenReturn("605B6925B7B887D7");
        return mock;
    }

}
