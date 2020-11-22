package com.n26.test.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.model.Statistic;
import com.n26.model.Transaction;
import com.n26.service.StatisticService;
import com.n26.service.TransactionService;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.ZonedDateTime;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(classes = { TransactionService.class, StatisticService.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ActiveProfiles("test")
public class UnitTests {

    private TransactionService transactionService = new TransactionService();
    private StatisticService statisticService = new StatisticService();
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        transactionService.createTransactionList();
    }

    @Test
    public void shouldReturnNotNullTransactionService() {
        assertNotNull(transactionService);
    }

    @Test
    public void shouldReturnNotNullStatisticService(){
        assertNotNull(statisticService);
    }

    @Test
    public void shouldReturnJSONHandledWithSuccess() throws Exception {
        String now = ZonedDateTime.now().toString();
        String successMessage = "Transaction Created with Success.";
        JSONObject json = new JSONObject();
        json.put("amount", "10.00");
        json.put("timestamp", now);
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHash = transactionService.handleJSON(transaction);

        assertNotNull(testHash);
        assertEquals(false, testHash.get("hasError"));
        assertEquals(successMessage, testHash.get("message"));
        assertEquals(201, testHash.get("httpStatus"));
    }

    @Test
    public void shouldReturnJSONHandledInTheFuture() throws Exception {
        String tomorrow = ZonedDateTime.now().plusDays(1).toString();
        String successMessage = "The date introduced is in the future.";

        JSONObject json = new JSONObject();
        json.put("amount", "10.00");
        json.put("timestamp", tomorrow);
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHash = transactionService.handleJSON(transaction);

        assertNotNull(testHash);
        assertEquals(true, testHash.get("hasError"));
        assertEquals(successMessage, testHash.get("message"));
        assertEquals(422, testHash.get("httpStatus"));
    }

    @Test
    public void shouldReturnJSONHandledIsOlder() throws Exception {
        String pastMinute = ZonedDateTime.now().minusSeconds(61).toString();
        String successMessage = "This transaction is older than 60sec. It will no longer be considered for statistics.";

        JSONObject json = new JSONObject();
        json.put("amount", "10.00");
        json.put("timestamp", pastMinute);
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHash = transactionService.handleJSON(transaction);

        assertNotNull(testHash);
        assertEquals(true, testHash.get("hasError"));
        assertEquals(successMessage, testHash.get("message"));
        assertEquals(204, testHash.get("httpStatus"));
    }

    @Test
    public void shouldReturnJSONHandledNotParsable() throws Exception {
        String now = ZonedDateTime.now().toString();
        String successMessage = "The fields should be parsable to following values:\n" +
                                        " amount: BigDecimal\n" +
                                        " timeStamp format: YYYY-MM-DD'T'hh:mm:ss.sssZ";

        JSONObject json = new JSONObject();
        json.put("amount", "abc");
        json.put("timestamp", now);
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHash = transactionService.handleJSON(transaction);

        assertNotNull(testHash);
        assertEquals(true, testHash.get("hasError"));
        assertEquals(successMessage, testHash.get("message"));
        assertEquals(422, testHash.get("httpStatus"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnTransactionCreatedWithSuccess() throws Exception {

        String now = ZonedDateTime.now().toString();

        JSONObject json = new JSONObject();
        json.put("amount", "10.00");
        json.put("timestamp", now);

        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        assertNotNull(transaction);
        assertEquals(transaction.getAmount(), json.get("amount"));
        assertEquals(transaction.getTimestamp(), json.get("timestamp"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnTransactionStatisticsCalculated() throws Exception {

        transactionService.clearTransactions();

        String now = ZonedDateTime.now().toString();

        JSONObject json1 = new JSONObject();
        json1.put("amount", "100");
        json1.put("timestamp", now);
        Transaction transaction1 = mapper.readValue(json1.toString(), Transaction.class);

        JSONObject json2 = new JSONObject();
        json2.put("amount", "200.0");
        json2.put("timestamp", now);
        Transaction transaction2 = mapper.readValue(json2.toString(), Transaction.class);

        transactionService.addTransaction(transaction1);
        transactionService.addTransaction(transaction2);
        Statistic statistic = statisticService.create(transactionService.getInDateTransactions());

        assertNotNull(statistic);
        assertEquals("300.00", statistic.getSum());
        assertEquals("150.00", statistic.getAvg());
        assertEquals("100.00", statistic.getMin());
        assertEquals("200.00", statistic.getMax());
        assertEquals(2, statistic.getCount());
    }

    @After
    public void tearDown() {
        transactionService.clearObjects();
    }
}
