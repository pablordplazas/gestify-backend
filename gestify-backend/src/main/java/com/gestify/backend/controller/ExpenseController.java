package com.gestify.backend.controller;

import com.gestify.backend.model.Expense;
import com.gestify.backend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam(required = false) Long idNegocio) {
        if (idNegocio != null) {
            return ResponseEntity.ok(expenseRepository.findByIdNegocio(idNegocio));
        }
        return ResponseEntity.ok(expenseRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(savedExpense);
    }
}