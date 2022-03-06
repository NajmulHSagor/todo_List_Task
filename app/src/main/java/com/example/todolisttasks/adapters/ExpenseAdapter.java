package com.example.todolisttasks.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolisttasks.databinding.ExpenseRowBinding;
import com.example.todolisttasks.models.ExpenseModel;

import java.util.List;

public class ExpenseAdapter extends ListAdapter<ExpenseModel,ExpenseAdapter.ExpenseViewHolder> {
    public ExpenseAdapter(){
        super(new ExpenseDiffCallback());
    }
    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ExpenseRowBinding binding = ExpenseRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new ExpenseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        final ExpenseModel expenseModel = getItem(position);
        holder.bind(expenseModel);

    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        private final ExpenseRowBinding  binding;

        public ExpenseViewHolder(ExpenseRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(ExpenseModel expenseModel){
            binding.setExpense(expenseModel);
        }
    }
    static class ExpenseDiffCallback extends DiffUtil.ItemCallback<ExpenseModel>{

        @Override
        public boolean areItemsTheSame(@NonNull ExpenseModel oldItem, @NonNull ExpenseModel newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ExpenseModel oldItem, @NonNull ExpenseModel newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }
}
