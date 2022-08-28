package org.banktransaction.service;

import lombok.RequiredArgsConstructor;
import org.banktransaction.entity.Transaction;
import org.banktransaction.exception.FileCanNotBeParsedException;
import org.banktransaction.exception.FileCanNotBeReadException;
import org.banktransaction.repository.TransactionRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    static String fileDir = "src/main/resources/data.json";

    public List<Transaction> getAllTransactionFromRepository() {
        return (List<Transaction>) repository.findAll();
    }

    public void readFileAndSaveInRepository() {
        List<Transaction> transactions;
        try {
            transactions = parseFile();
        } catch (IOException e) {
            throw new FileCanNotBeReadException("There is a problem with a file, it can not be read!");
        }
        if (transactions != null) {
            repository.saveAll(transactions);
        }
    }

    public static List<Transaction> parseFile() throws IOException {
        JSONParser jsonParser = new JSONParser();
        List<Transaction> transactions = null;
        try {
            JSONArray parsedArray = (JSONArray) jsonParser.parse(new FileReader(new File(fileDir)));
            if (parsedArray != null) {
                transactions = new ArrayList<>();
                mapJsonToTransaction(transactions, parsedArray);
            }
        } catch (ParseException e) {
            throw new FileCanNotBeParsedException("There is a problem with parsing");
        }
        return transactions;
    }

    private static void mapJsonToTransaction(List<Transaction> transactions, JSONArray parsedArray) {
        for (Object elem : parsedArray) {
            JSONObject jsonElem = (JSONObject) elem;
            Transaction transaction = Transaction.builder()
                    .date(LocalDate.parse((CharSequence) jsonElem.get("date"), DateTimeFormatter.ofPattern("dd/LLL/yyyy")))
                    .vendor((String) jsonElem.get("vendor"))
                    .type(Transaction.TransactionType.valueOf((String) jsonElem.get("type")))
                    .amount(new BigDecimal((String) jsonElem.get("amount")).setScale(2, RoundingMode.DOWN))
                    .category((String) jsonElem.get("category"))
                    .build();
            transactions.add(transaction);
        }
    }

    public static List<Transaction> sortAsLatestFirst(List<Transaction> transactions) {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public static Map<String, BigDecimal> getCategoryToOutgoing(List<Transaction> transactions) {
        Map<String, BigDecimal> categoryToOutgoing = new HashMap<>();
        for (Transaction elem : transactions) {
            if (categoryToOutgoing.containsKey(elem.getCategory())) {
                BigDecimal previousAmount = categoryToOutgoing.get(elem.getCategory());
                categoryToOutgoing.put(elem.getCategory(), previousAmount.add(elem.getAmount()));
            } else {
                categoryToOutgoing.put(elem.getCategory(), elem.getAmount());
            }
        }
        return categoryToOutgoing;
    }

    public static List<Transaction> getTransactionsByCategory(List<Transaction> transactions, String category) {
        return transactions.stream()
                .filter(el -> el.getCategory() != null)
                .filter(el -> el.getCategory().toLowerCase().trim().equals(category.toLowerCase().trim()))
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public static Map<String, BigDecimal> getMonthlyAverageSpendToCategory(List<Transaction> transactions, String category) {
        List<Transaction> transactionsByCategory = getTransactionsByCategory(transactions, category);
        Map<String, BigDecimal> categoryToMonthlySpend = new HashMap<>();
        Map<String, BigDecimal> categoryToMonthlyAvgSpend = new HashMap<>();
        BigDecimal totalAmount = new BigDecimal("0.00");
        for (Transaction elem : transactionsByCategory) {
            String mountAndYear = elem.getDate().getMonth() + "-" + elem.getDate().getYear();
            if (categoryToMonthlySpend.containsKey(mountAndYear)) {
                BigDecimal previousAmount = categoryToMonthlySpend.get(mountAndYear);
                categoryToMonthlySpend.put(mountAndYear, previousAmount.add(elem.getAmount()));
            } else {
                categoryToMonthlySpend.put(mountAndYear, elem.getAmount());
            }
        }

        getCategoryToMonthlyAvgSpend(category, categoryToMonthlySpend, categoryToMonthlyAvgSpend, totalAmount);
        return categoryToMonthlyAvgSpend;
    }

    public Transaction getHighestSpendByCategoryAndYear(int year, String category, List<Transaction> transactions) {
        List<Transaction> sortedByAmount = getSortedByYearAndCategory(year, category, transactions);
        return sortedByAmount.isEmpty() ? new Transaction() : sortedByAmount.get(sortedByAmount.size() - 1);
    }

    public Transaction getLowestSpendByCategoryAndYear(int year, String category, List<Transaction> transactions) {
        List<Transaction> sortedByAmount = getSortedByYearAndCategory(year, category, transactions);
        return sortedByAmount.isEmpty() ? new Transaction() : sortedByAmount.get(0);
    }

    private List<Transaction> getSortedByYearAndCategory(int year, String category, List<Transaction> transactions) {
        List<Transaction> transactionsByCategory = getTransactionsByCategory(transactions, category);
        List<Transaction> sortedByAmount = new ArrayList<>();
        if (!transactions.isEmpty()) {
            sortedByAmount = transactionsByCategory.stream()
                    .filter(elem -> elem.getDate().getYear() == year)
                    .sorted(Comparator.comparing(Transaction::getAmount))
                    .collect(Collectors.toList());
        }
        return sortedByAmount;
    }

    private static void getCategoryToMonthlyAvgSpend(String category, Map<String, BigDecimal> categoryToMonthlySpend, Map<String, BigDecimal> categoryToMonthlyAvgSpend, BigDecimal totalAmount) {
        for (BigDecimal elem : categoryToMonthlySpend.values()) {
            totalAmount = totalAmount.add(elem);
        }
        if (categoryToMonthlySpend.size() > 0) {
            BigDecimal mountsQuantity = new BigDecimal(categoryToMonthlySpend.size());
            BigDecimal totalAmountAvg = totalAmount.divide(mountsQuantity, 2, RoundingMode.DOWN);
            categoryToMonthlyAvgSpend.put(category, totalAmountAvg);
        } else {
            categoryToMonthlyAvgSpend.put(category, totalAmount);
        }
    }
}
