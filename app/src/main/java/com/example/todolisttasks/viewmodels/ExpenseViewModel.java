package com.example.todolisttasks.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.todolisttasks.models.ExpenseModel;
import com.example.todolisttasks.repos.ExpenseRepository;

import java.util.List;

public class ExpenseViewModel extends ViewModel {
    private final ExpenseRepository repository = new ExpenseRepository();
    public void addExpense(ExpenseModel expenseModel){
        repository.insertNewExpense(expenseModel)
                .addOnSuccessListener(unused -> {

                }).addOnFailureListener(unused->{

        });
    }
    public LiveData<List<ExpenseModel>> getExpenseByUserId(String userId){
        return repository.getAllExpenses(userId);
    }


}
