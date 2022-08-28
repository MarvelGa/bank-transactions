package org.banktransaction;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.banktransaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = Application.class)
class SprintBootContextTest {
    @Autowired
    private ApplicationContext context;

    @Test
    void checkContext() {
        assertNotNull(context.getBean(TransactionService.class));
    }
}
