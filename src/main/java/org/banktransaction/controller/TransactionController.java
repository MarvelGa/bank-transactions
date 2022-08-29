package org.banktransaction.controller;

import org.banktransaction.dto.TransferData;
import org.banktransaction.entity.Transaction;
import org.banktransaction.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class TransactionController {
    TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
        service.readFileAndSaveInRepository(service.fileDir);
    }

    @GetMapping("/")
    public String getTransactionsByCategory(HttpSession session, Model model) {
        List<Transaction> transactions = service.getAllTransactionFromRepository();
        if (session.getAttribute("category") != null) {
            String category = (String) session.getAttribute("category");
            List<Transaction> transactionsByCategory = service.getTransactionsByCategory(transactions, category);
            List<Transaction> sortedTransactions = service.sortAsLatestFirst(transactionsByCategory);
            model.addAttribute("transactions", sortedTransactions);
        } else {
            model.addAttribute("transactions", transactions);
        }
        session.removeAttribute("category");
        model.addAttribute("categoryName", new String());
        return "transactions-by-category-page";
    }

    @PostMapping("/")
    public String getTransactionsCategory(@ModelAttribute("categoryName") String category, HttpSession session) {
        if (category != null) {
            session.setAttribute("category", category);
        }
        return "redirect:/";
    }

    @GetMapping("/total-outgoing")
    public String getTotalOutgoingByCategory(HttpSession session, Model model) {
        List<Transaction> transactions = service.getAllTransactionFromRepository();
        Map<String, BigDecimal> categoryToAmount = service.getCategoryToOutgoing(transactions);
        model.addAttribute("categoryToAmount", categoryToAmount);
        return "total-outgoing-per-category-page";
    }

    @GetMapping("/monthly-average")
    public String getMonthlyAverageSpendByCategory(HttpSession session, Model model) {
        List<Transaction> transactions = service.getAllTransactionFromRepository();
        if (session.getAttribute("category") != null) {
            String category = (String) session.getAttribute("category");
            Map<String, BigDecimal> categoryToMonthlyAvgSpend = service.getMonthlyAverageSpendToCategory(transactions, category);
            model.addAttribute("categoryToMonthlyAvgSpend", categoryToMonthlyAvgSpend);
        }
        session.removeAttribute("category");
        model.addAttribute("categoryName", new String());
        return "monthly-average-spend-by-category-page";
    }

    @PostMapping("/monthly-average")
    public String getCategory(@ModelAttribute("categoryName") String category, HttpSession session) {
        if (category != null) {
            session.setAttribute("category", category);
        }
        return "redirect:/monthly-average";
    }

    @GetMapping("/highest-spend")
    public String getHighestSpendByCategoryAndYear(HttpSession session, Model model) {
        model.addAttribute("isDataFound", true);
        List<Transaction> transactions = service.getAllTransactionFromRepository();
        if (session.getAttribute("category") != null && session.getAttribute("year") != null) {
            String category = (String) session.getAttribute("category");
            Integer year = (Integer) session.getAttribute("year");
            Transaction highestSpend = service.getHighestSpendByCategoryAndYear(year, category, transactions);
            if (highestSpend.getId() != null) {
                model.addAttribute("transactions", List.of(highestSpend));
            } else {
                model.addAttribute("isDataFound", false);
            }
        } else {
            model.addAttribute("transactions", transactions);
        }
        session.removeAttribute("category");
        session.removeAttribute("year");
        model.addAttribute("transferData", new TransferData());
        return "highest-spend-by-category-and-year-page";
    }

    @PostMapping("/highest-spend")
    public String getCategoryAndYear(@ModelAttribute("categoryName") String category, @ModelAttribute("year") Integer year, HttpSession session) {
        if (category != null) {
            session.setAttribute("category", category);
        }
        if (year != null) {
            session.setAttribute("year", year);
        }
        return "redirect:/highest-spend";
    }

    @GetMapping("/lowest-spend")
    public String getLowestSpendByCategoryAndYear(HttpSession session, Model model) {
        model.addAttribute("isDataFound", true);
        List<Transaction> transactions = service.getAllTransactionFromRepository();
        if (session.getAttribute("category") != null && session.getAttribute("year") != null) {
            String category = (String) session.getAttribute("category");
            Integer year = (Integer) session.getAttribute("year");
            Transaction highestSpend = service.getLowestSpendByCategoryAndYear(year, category, transactions);
            if (highestSpend.getId() != null) {
                model.addAttribute("transactions", List.of(highestSpend));
            } else {
                model.addAttribute("isDataFound", false);
            }
        } else {
            model.addAttribute("transactions", transactions);
        }
        session.removeAttribute("category");
        session.removeAttribute("year");
        model.addAttribute("transferData", new TransferData());
        return "lowest-spend-by-category-and-year-page";
    }

    @PostMapping("/lowest-spend")
    public String getCategoryAndYearForLowestTransaction(@ModelAttribute("categoryName") String category, @ModelAttribute("year") Integer year, HttpSession session) {
        if (category != null) {
            session.setAttribute("category", category);
        }
        if (year != null) {
            session.setAttribute("year", year);
        }
        return "redirect:/lowest-spend";
    }

}
