package com.ftvalue.aggregation.api;

import com.ftvalue.aggregation.api.model.Payment;
import com.ftvalue.aggregation.api.model.PaymentResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockRestServiceServer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
public class PaymentConsumerTests {

    @Value("${payment.service.baseUrl}")
    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentConsumer consumer;

    private MockRestServiceServer server;

    @Before
    public void before() {
        server = WireMockRestServiceServer.with(this.restTemplate)
                .baseUrl(this.baseUrl)
                .stubs("classpath:mappings/*.json").build();
    }

    @After
    public void after() {
        server.verify();
    }

    @Test
    public void shouldMakeAPaymentSuccessfullyGivenCorrectOrderInfo() throws Exception {
        // given:
        Payment payment = new Payment("95ff8e3b2ff06eb4f894e46fb028ccedc8d2294e068632e810c10bg6adgegg05")
                .set("order_no", "20170413232809").set("charset", "GBK").set("service", "online_pay")
                .set("seller_email", "game211@126.com").set("merchant_ID", "100000000001986").set("isApp", "web")
                .set("paymethod", "bankPay").set("notify_url", "https://test.payworth.net/notify_url.jsp")
                .set("title", "1").set("body", "1").set("payment_type", 1).set("total_fee", 0.11F)
                .set("return_url","https://test.payworth.net/return_url.jsp");

        // when:
        PaymentResult paymentResult = consumer.pay(payment);

        // then:
        assertThat(paymentResult.getBody()).isEqualTo("{\"status\":\"SUCCESS\"}");
    }

}
