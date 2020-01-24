package org.killbill.billing.plugin.payment;

import org.joda.time.DateTime;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.payment.plugin.api.PaymentTransactionInfoPlugin;
import org.killbill.billing.plugin.api.PluginCallContext;
import org.killbill.billing.plugin.payment.dao.PaymentTestDao;
import org.killbill.clock.Clock;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.killbill.billing.plugin.payment.PaymentTestActivator.PLUGIN_NAME;

public class PaymentTestPluginApiDBTest {

    @Mock
    OSGIKillbillAPI             killbillAPI;
    @Mock
    OSGIConfigPropertiesService configProperties;
    @Mock
    OSGIKillbillLogService      logService;
    @Mock
    Clock                       clock;
    PaymentTestDao paymentTestDao;


    TestingStates        testingStates;
    PaymentTestPluginApi paymentTestPugin;
    PluginCallContext    pluginCallContext;
    UUID                 accountId;
    UUID                 tenantId;


    @BeforeClass(groups = {"slow", "integration"})
    private void startDB() throws Exception {
        EmbeddedDbHelper.instance().startDb();
    }

    @AfterClass(groups = {"slow", "integration"})
    private void stopDB() throws Exception {
        EmbeddedDbHelper.instance().stopDB();
    }

    @AfterMethod(groups = {"slow", "integration"})
    public void tearDown() throws Exception {
    }

    @BeforeMethod(groups = {"slow", "integration"})
    void beforeMethod() throws Exception {
        EmbeddedDbHelper.instance().resetDB();
        this.paymentTestDao = EmbeddedDbHelper.instance().getPaymentTestDao();
        this.testingStates = new TestingStates();

        this.tenantId = UUID.randomUUID();
        this.accountId = UUID.randomUUID();
        this.pluginCallContext = new PluginCallContext(PLUGIN_NAME, DateTime.now(), this.accountId, this.tenantId);
        this.testingStates = new TestingStates();
        this.paymentTestPugin = new PaymentTestPluginApi(this.killbillAPI,
                                                         this.configProperties,
                                                         this.logService,
                                                         this.clock,
                                                         this.paymentTestDao,
                                                         this.testingStates);
    }

    @Test(groups = {"slow", "integration"})
    public void testReadWritePaymentResponses() throws PaymentPluginApiException, SQLException {
        final UUID kbPaymentId = UUID.randomUUID();
        //        final UUID kbTransactionId = UUID.randomUUID();
        final UUID kbPaymentMethodId = UUID.randomUUID();

        final PaymentTransactionInfoPlugin ret = this.paymentTestPugin.authorizePayment(
                this.accountId,
                kbPaymentId,
                UUID.randomUUID(),
                kbPaymentMethodId,
                BigDecimal.TEN,
                Currency.EUR,
                null,
                this.pluginCallContext);

        Assert.assertEquals(ret.getStatus(), PaymentPluginStatus.PROCESSED);

        final List<PaymentTransactionInfoPlugin> responses =
                this.paymentTestDao.getPaymentResponses(this.accountId,
                                                        this.tenantId,
                                                        kbPaymentId);
        Assert.assertEquals(responses.size(), 1);
        Assert.assertTrue(responses.get(0).getKbPaymentId().compareTo(kbPaymentId) == 0);
        Assert.assertTrue(responses.get(0).getKbTransactionPaymentId().compareTo(kbPaymentMethodId) == 0);
        Assert.assertTrue(responses.get(0).getAmount().compareTo(BigDecimal.TEN) == 0);
        Assert.assertEquals(responses.get(0).getCurrency(), Currency.EUR);
    }
}

