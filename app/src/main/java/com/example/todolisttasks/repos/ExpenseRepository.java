package com.example.todolisttasks.repos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todolisttasks.models.ExpenseModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {
    private final String COLLECTION_EXPENSE = "TodoExpenses";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<Void> insertNewExpense(ExpenseModel expenseModel) {
        final DocumentReference ref = db.collection(COLLECTION_EXPENSE)
                .document();
        expenseModel.setId(ref.getId());
        return ref.set(expenseModel);

    }

    public LiveData<List<ExpenseModel>> getAllExpenses(String userId) {
        final MutableLiveData<List<ExpenseModel>> expensesLiveData =
                new MutableLiveData<>();
        db.collection(COLLECTION_EXPENSE)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(((value, error) -> {
                    if (error != null) return;
                    final List<ExpenseModel> tempList = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        tempList.add(doc.toObject(ExpenseModel.class));
                    }
                    expensesLiveData.postValue(tempList);
                }));
        return expensesLiveData;
    }

}
