package com.yushkova.banktest.tests;

import com.yushkova.banktest.ApplicationManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class TestBase {

  protected final ApplicationManager app = new ApplicationManager();

  String[] namesOfRegisterParameters = {"orderId", "formUrl"};
  String[] namesOfOrderStatusParameters = {"errorCode", "errorMessage", "orderStatus", "amount", "paymentAmountInfo"};
  String[] namesOfPaymentAmountInfo = {"paymentState", "approvedAmount", "depositedAmount", "refundedAmount"};
  String[] namesOfReverseAndRefundParameters = {"errorCode", "errorMessage"};

  @BeforeMethod
  public void setUp() throws Exception {
    app.init();
  }

  @AfterMethod
  public void tearDown() {
    app.stop();
  }

  public ApplicationManager getApp() {
    return app;
  }
}
