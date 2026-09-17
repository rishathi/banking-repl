package repl.banking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import repl.banking.domain.*;
import repl.banking.persistence.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class BankingServiceImplTest {

    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private BankingService service;

    @BeforeEach
    void setUp() {
        accountDAO = mock(AccountDAO.class);
        transactionDAO = mock(TransactionDAO.class);
        service = new BankingServiceImpl(accountDAO, transactionDAO);
    }

    @Test 
    void loginSucceedsWithCorrectCreds() {
        Account account = new Account("1234", 0.0);
        account.setAccountId(1);

        when(accountDAO.findById(1)).thenReturn(account);

        Account result = service.login(1, "1234");

        assertNotNull(result);
        assertEquals(1, result.getAccountId());
        assertEquals("1234", result.getPin());

        verify(accountDAO).findById(1);
    }

    @Test
    void loginFailsWithIncorrectPin() {
        Account account = new Account("1234", 0.0);
        account.setAccountId(1);

        when(accountDAO.findById(1)).thenReturn(account);

        Account result = service.login(1, "0000");

        assertNull(result);

        verify(accountDAO).findById(1);
    }

}