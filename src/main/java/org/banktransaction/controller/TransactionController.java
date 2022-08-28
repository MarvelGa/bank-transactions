package org.banktransaction.controller;

import org.banktransaction.entity.Transaction;
import org.banktransaction.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/*@RequiredArgsConstructor*/
@Controller
public class TransactionController {
    /*private final*/ TransactionService service;

    public TransactionController(TransactionService service) {
   /*     this.service = service;
        service.readFileAndSaveInRepository();*/
    }

    @GetMapping("/")
    public String getAllTransaction(Model model) {
        List<Transaction> transactions = service.getAllTransactionFromRepository();
        model.addAttribute("transactions", transactions);
        return "main-page";
    }
}
