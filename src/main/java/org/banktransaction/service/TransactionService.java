package org.banktransaction.service;

import lombok.RequiredArgsConstructor;
import org.banktransaction.entity.Transaction;
import org.banktransaction.exception.FileCanNotBeParsedException;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
private final TransactionRepository repository;
private static int id = 1;
static String fileDir = "src/main/resources/data.json";

    public static void readFileAndSaveInRepository() throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        transactions = parseFile();
    }

    public static List<Transaction> parseFile() throws IOException {
        JSONParser jsonParser = new JSONParser();
        List<Transaction> transactions = null;
        try {
            JSONArray parsedArray = (JSONArray) jsonParser.parse(new FileReader(new File(fileDir)));
            if (parsedArray!=null){
                transactions = new ArrayList<>();
                mapJsonToTransaction(transactions, parsedArray);
            }
        } catch (ParseException e) {
            throw new FileCanNotBeParsedException("There is a problem with parsing");
        }
        return transactions;
}

    private static void mapJsonToTransaction(List<Transaction> transactions, JSONArray parsedArray) {
        for (Object elem: parsedArray){
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
}
