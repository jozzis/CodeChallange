package com.n26.test.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.model.Statistic;
import com.n26.model.Transaction;
import com.n26.service.StatisticService;
import com.n26.service.TransactionService;
import org.hibernate.validator.constraints.br.TituloEleitoral;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

@SpringBootTest(classes = { TransactionService.class, StatisticService.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ActiveProfiles("test")
public class UnitTests {

    private TransactionService transactionService = new TransactionService();
    private StatisticService statisticService = new StatisticService();
    private ObjectMapper mapper = new ObjectMapper();

    /*
     * Test 1 : Check if transactionService object is created with success
     * Expected : NotNull
     **/
    @Test
    @Order(1)
    public void shouldReturnTransactionServiceNotNull() {
        assertNotNull(transactionService);
    }

    /*
     * Test 2 : Check if statisticService object is created with success
     * Expected : NotNull
     **/
    @Test
    @Order(2)
    public void shouldReturnStatisticServiceNotNull(){
        assertNotNull(statisticService);
    }

    /*
     * Test 3 : Call checkJSON method with a valid JSON transaction
     * Expected : testHashMap = {hasError   = false,
     *                           message    = successMessage,
     *                           httpStatus = 201}
     **/
    @Test
    @Order(3)
    public void shouldReturnJSONHandledWithSuccess() throws Exception {
        String now = ZonedDateTime.now().toString();
        String successMessage = "Transaction Created with Success.";

        JSONObject json = new JSONObject();
        json.put("amount", "10.00");
        json.put("timestamp", now);
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHashMap = transactionService.checkJSON(transaction);

        assertNotNull(testHashMap);
        assertEquals(false, testHashMap.get("hasError"));
        assertEquals(successMessage, testHashMap.get("message"));
        assertEquals(201, testHashMap.get("httpStatus"));
    }

    /*
     * Test 4 : Call checkJSON method with a null JSON.
     * Expected : testHashMap = {hasError   = true,
     *                           message    = failedMessage,
     *                           httpStatus = 400}
     **/
    @Test
    @Order(4)
    public void shouldReturnJSONHandledIsNotValid() throws Exception {
        String failedMessage = "The JSON is not valid.";

        JSONObject json = new JSONObject();
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHashMap = transactionService.checkJSON(transaction);

        assertNotNull(testHashMap);
        assertEquals(true, testHashMap.get("hasError"));
        assertEquals(failedMessage, testHashMap.get("message"));
        assertEquals(400, testHashMap.get("httpStatus"));
    }

    /*
     * Test 5 : Call checkJSON method with a transaction date in the future.
     * Expected : testHashMap = {hasError   = true,
     *                           message    = failedMessage,
     *                           httpStatus = 422}
     **/
    @Test
    @Order(5)
    public void shouldReturnJSONHandledInTheFuture() throws Exception {
        String tomorrow = ZonedDateTime.now().plusDays(1).toString();
        String failedMessage = "The date introduced is in the future.";

        JSONObject json = new JSONObject();
        json.put("amount", "10.00");
        json.put("timestamp", tomorrow);
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHashMap = transactionService.checkJSON(transaction);

        assertNotNull(testHashMap);
        assertEquals(true, testHashMap.get("hasError"));
        assertEquals(failedMessage, testHashMap.get("message"));
        assertEquals(422, testHashMap.get("httpStatus"));
    }

    /*
     * Test 6 : Call checkJSON method with a transaction date before past 60sec.
     * Expected : testHashMap = {hasError   = true,
     *                           message    = warningMessage,
     *                           httpStatus = 204}
     **/
    @Test
    @Order(6)
    public void shouldReturnJSONHandledIsOld() throws Exception {
        String pastMinute = ZonedDateTime.now().minusSeconds(61).toString();
        String warningMessage = "This transaction is older than 60sec. It will no longer be considered for statistics.";

        JSONObject json = new JSONObject();
        json.put("amount", "10.00");
        json.put("timestamp", pastMinute);
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHashMap = transactionService.checkJSON(transaction);

        assertNotNull(testHashMap);
        assertEquals(true, testHashMap.get("hasError"));
        assertEquals(warningMessage, testHashMap.get("message"));
        assertEquals(204, testHashMap.get("httpStatus"));
    }

    /*
     * Test 7 : Call checkJSON method with JSON field not parsable.
     * Expected : testHashMap = {hasError   = true,
     *                           message    = failedMessage,
     *                           httpStatus = 204}
     **/
    @Test
    @Order(7)
    public void shouldReturnJSONNotParsable() throws Exception {
        String now = ZonedDateTime.now().toString();
        String failedMessage = "The fields should be parsable to following values:\n" +
                                        " amount: BigDecimal\n" +
                                        " timeStamp format: YYYY-MM-DD'T'hh:mm:ss.sssZ";

        JSONObject json = new JSONObject();
        json.put("amount", "abc");
        json.put("timestamp", now);
        Transaction transaction = mapper.readValue(json.toString(), Transaction.class);

        HashMap testHashMap = transactionService.checkJSON(transaction);

        assertNotNull(testHashMap);
        assertEquals(true, testHashMap.get("hasError"));
        assertEquals(failedMessage, testHashMap.get("message"));
        assertEquals(422, testHashMap.get("httpStatus"));
    }

    /*
     * Test 8 : Call createStatistic method with 2 valid transactions.
     * Expected : Statistic = {sum    = "300.00",
     *                         avg    = "150.00",
     *                         max    = "200.00",
     *                         min    = "100.00",
     *                         count  = 2 }
     **/
    @Test
    @Order(8)
    @SuppressWarnings("unchecked")
    public void shouldReturnStatisticsCreated() throws Exception {

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
        Statistic statistic = statisticService.createStatistic(transactionService.getInDateTransactions());

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
