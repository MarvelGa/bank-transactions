package org.banktransaction;

import org.banktransaction.entity.Transaction;
import org.banktransaction.exception.FileCanNotBeParsedException;
import org.banktransaction.exception.FileCanNotBeReadException;
import org.banktransaction.repository.TransactionRepository;
import org.banktransaction.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class TransactionServiceTest {
    @MockBean
    private TransactionRepository repository;
    private TransactionService service;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private Transaction transaction4;

    private Transaction transaction5;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        service = new TransactionService(repository);
        transaction1 = Transaction.builder()
             /*   .id(1L)*/
                .amount(new BigDecimal("845.03"))
                .vendor("Vendor1")
                .category("Groceries")
                .type(Transaction.TransactionType.INTERNET)
                .date(LocalDate.parse("2021-12-06"))
                .build();

        transaction2 = Transaction.builder()
            /*    .id(2L)*/
                .amount(new BigDecimal("75.03"))
                .vendor("Vendor2")
                .category("MyMonthlyDD")
                .type(Transaction.TransactionType.CARD)
                .date(LocalDate.parse("2020-11-18"))
                .build();

        transaction3 = Transaction.builder()
     /*           .id(3L)*/
                .amount(new BigDecimal("775.03"))
                .vendor("Vendor3")
                .category("MyMonthlyDD")
                .type(Transaction.TransactionType.DIRECT_DEBIT)
                .date(LocalDate.parse("2021-01-17"))
                .build();

        transaction4 = Transaction.builder()
        /*        .id(4L)*/
                .amount(new BigDecimal("1475.03"))
                .vendor("Vendor4")
                .category("MyMonthlyDD")
                .type(Transaction.TransactionType.INTERNET)
                .date(LocalDate.parse("2020-03-05"))
                .build();

        transaction5 = Transaction.builder()
       /*         .id(5L)*/
                .amount(new BigDecimal("877.03"))
                .vendor("Vendor5")
                .category("")
                .type(Transaction.TransactionType.CARD)
                .date(LocalDate.parse("2020-03-05"))
                .build();

        transactions = List.of(transaction1, transaction2, transaction3, transaction4, transaction5);
    }

    @Test
    void shouldParseFileCorrect(){
        String fileDir = "src/test/resources/test-data.json";
        List<Transaction> expected =  transactions;
        var actual = service.readFileAndSaveInRepository(fileDir);
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowsFileCanNotBeReadExceptionIfFilesPathNotValid() {
        String fileDir = "wrong";
        Throwable exception = Assertions.assertThrows(
                FileCanNotBeReadException.class, () -> service.readFileAndSaveInRepository(fileDir));
        assertEquals("Can't find the file. There is a problem with a file's path, it can not be read!", exception.getMessage());
        assertEquals(FileCanNotBeReadException.class, exception.getClass());
    }

    @Test
    void shouldThrowsFileCanNotBeParsedExceptionIfJsonFileDataIsNotValid() {
        String fileDir = "src/test/resources/incorrect-test-data.json";
        Throwable exception = Assertions.assertThrows(
                FileCanNotBeParsedException.class, () -> service.readFileAndSaveInRepository(fileDir));
        assertEquals("There is a problem with parsing. Json data is not valid", exception.getMessage());
        assertEquals(FileCanNotBeParsedException.class, exception.getClass());
    }


    @Test
    void shouldGetHighestSpendTransactionByCategoryAndYear() {
        int year = 2020;
        String category="MyMonthlyDD";
        Transaction expected = transaction4;
        Transaction actual = service.getHighestSpendByCategoryAndYear(year, category, transactions);
        assertEquals(expected, actual);
    }

    @Test
    void ifHighestSpendTransactionByCategoryAndYearWasNotFoundByNotExistYearThenReturnEmptyObject() {
        int year = 2000;
        String category="MyMonthlyDD";
        Transaction expected = new Transaction();
        Transaction actual = service.getHighestSpendByCategoryAndYear(year, category, transactions);
        assertEquals(expected, actual);
    }

    @Test
    void ifHighestSpendTransactionByCategoryAndYearWasNotFoundByNotExistCategoryThenReturnEmptyObject() {
        int year = 2020;
        String category="Not exist";
        Transaction expected = new Transaction();
        Transaction actual = service.getHighestSpendByCategoryAndYear(year, category, transactions);
        assertEquals(expected, actual);
    }

    @Test
    void shouldGetHighestSpendTransactionByEmptyCategoryAndYear() {
        int year = 2020;
        String category="";
        Transaction expected = transaction5;
        Transaction actual = service.getHighestSpendByCategoryAndYear(year, category, transactions);
        assertEquals(expected, actual);
    }

    @Test
    void ifLowestSpendTransactionByCategoryAndYearWasNotFoundThenReturnEmptyObject() {
        int year = 2020;
        String category="MyMonthlyDD";
        Transaction expected = transaction2;
        Transaction actual = service.getLowestSpendByCategoryAndYear(year, category, transactions);
        assertEquals(expected, actual);
    }

    @Test
    void ifLowestSpendTransactionByCategoryAndYearWasNotFoundByNotExistYearThenReturnEmptyObject() {
        int year = 1900;
        String category="MyMonthlyDD";
        Transaction expected = new Transaction();
        Transaction actual = service.getLowestSpendByCategoryAndYear(year, category, transactions);
        assertEquals(expected, actual);
    }

    @Test
    void ifLowestSpendTransactionByCategoryAndYearWasNotFoundByNotExistCategoryThenReturnEmptyObject() {
        int year = 2020;
        String category="Not exist";
        Transaction expected = new Transaction();
        Transaction actual = service.getLowestSpendByCategoryAndYear(year, category, transactions);
        assertEquals(expected, actual);
    }

    @Test
    void shouldGetLowestSpendTransactionByEmptyCategoryAndYear() {
        int year = 2020;
        String category="";
        Transaction expected = transaction5;
        Transaction actual = service.getLowestSpendByCategoryAndYear(year, category, transactions);
        assertEquals(expected, actual);
    }





}
