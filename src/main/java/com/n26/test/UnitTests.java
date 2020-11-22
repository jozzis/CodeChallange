package com.n26.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.model.Statistic;
import com.n26.model.Transaction;
import com.n26.service.StatisticService;
import com.n26.service.TransactionService;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;

@SpringBootTest(classes = { TransactionService.class, StatisticService.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
@ActiveProfiles("test")
public class UnitTests {
    @Autowired
    private TransactionService transactionService = new TransactionService();
    @Autowired
    private StatisticService statisticService = new StatisticService();

    @Before
    public void setUp() {
        transactionService.createTransactionList();
    }

    @org.junit.jupiter.api.Test
    public void shouldReturnNotNullTransactionService() {
        assertNotNull(transactionService);
    }

    @Test
    public void shouldReturnNotNullStatisticService() throws Exception {
        assertNotNull(statisticService);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnTransactionCreatedWithSuccess() throws Exception {

        String now = ZonedDateTime.now().toString();

        JSONObject json = new JSONObject();
        json.put("amount", "22.88");
        json.put("timestamp", now);

        Transaction transaction = transactionService.createTransaction(json);

        assertNotNull(transaction);
        assertEquals(transaction.getAmount().toString(), json.get("amount"));
        assertEquals(transaction.getTimestamp().toString(), json.get("timestamp"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnTransactionCreatedInFuture() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        JSONObject json = new JSONObject();
        json.put("amount", "10");
        json.put("timestamp", "2021-09-11T09:59:51.312Z");

        boolean transactionInFuture = transactionService.
                isTransactionInFuture(transactionService.createTransaction(json));

        assertTrue(transactionInFuture);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnTransactionStatisticsCalculated() throws Exception {

        transactionService.clearTransactions();

        String now = ZonedDateTime.now().toString();

        JSONObject json1 = new JSONObject();
        json1.put("amount", "100");
        json1.put("timestamp", now);
        transactionService.addTransaction(transactionService.
                                            createTransaction(json1));

        JSONObject json2 = new JSONObject();
        json2.put("amount", "200.0");
        json2.put("timestamp", now);
        transactionService.addTransaction(transactionService.
                                            createTransaction(json2));

        Statistic statistic = statisticService.create(transactionService.getInDateTransactions());

        assertNotNull(statistic);
        assertEquals("300.00", statistic.getSum().toString());
        assertEquals("150.00", statistic.getAvg().toString());
        assertEquals("100.00", statistic.getMin().toString());
        assertEquals("200.00", statistic.getMax().toString());
        assertEquals(2, statistic.getCount());
    }

    @After
    public void tearDown() {
        transactionService.clearObjects();
    }
}
