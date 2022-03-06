package com.example.todolisttasks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.todolisttasks.adapters.ExpenseAdapter;
import com.example.todolisttasks.databinding.FragmentExpenseListBinding;
import com.example.todolisttasks.models.ExpenseModel;
import com.example.todolisttasks.viewmodels.ExpenseViewModel;
import com.example.todolisttasks.viewmodels.LoginViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.StructuredQuery;

import java.io.ByteArrayOutputStream;

public class ExpenseListFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private FragmentExpenseListBinding binding;
    private ExpenseViewModel expenseViewModel;
    private String imageUrl;

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri photoUri = result.getData().getData();
                    binding.expenseIV.setImageURI(photoUri);
                    binding.saveBTN.setText("Please Wait....");
                    binding.saveBTN.setEnabled(false);
                    uploadImage(photoUri);
                }

            });

    private void uploadImage(Uri photoUri) {
        final StorageReference photoRef =
                FirebaseStorage.getInstance().getReference()
                        .child("image/" + System.currentTimeMillis());
        binding.expenseIV.setDrawingCacheEnabled(true);
        binding.expenseIV.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) binding.expenseIV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                binding.saveBTN.setText("Save");
                binding.saveBTN.setEnabled(true);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }


                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    binding.saveBTN.setText("Save");
                    binding.saveBTN.setEnabled(true);
                } else {

                }
            }
        });


    }


    public ExpenseListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExpenseListBinding.inflate(inflater);
        loginViewModel = new ViewModelProvider(requireActivity())
                .get(LoginViewModel.class);
        expenseViewModel = new ViewModelProvider(requireActivity())
                .get(ExpenseViewModel.class);

        loginViewModel.getStateLiveData()
                .observe(getViewLifecycleOwner(), authState -> {
                    if (authState == LoginViewModel.AuthState.UNAUTHENTICATED) {
                        Navigation.findNavController(container)
                                .navigate(R.id.login_action);
                    }
                });
        final ExpenseAdapter adapter = new ExpenseAdapter();
        binding.expenseRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.expenseRV.setAdapter(adapter);

        if (loginViewModel.getUser() != null) {
            expenseViewModel.getExpenseByUserId(loginViewModel.getUser().getUid())
                    .observe(getViewLifecycleOwner(), expenseList -> {
                        adapter.submitList(expenseList);
                    });
        }

        final BottomSheetBehavior<CardView> behavior =
                BottomSheetBehavior.from(binding.bottonSheetCardView);
        binding.floatingActionButton.setOnClickListener(view -> {
            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        binding.galleryBTN.setOnClickListener(view -> {
            final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });
        binding.saveBTN.setOnClickListener(view -> {
            final String title = binding.titleInputET.getText().toString();
            final double amount = Double.parseDouble(
                    binding.amountInputET.getText().toString());
            final ExpenseModel expenseModel = new ExpenseModel(
                    null, loginViewModel.getUser().getUid(),
                    title, amount, System.currentTimeMillis(), imageUrl
            );
            expenseViewModel.addExpense(expenseModel);
            binding.titleInputET.setText("");
            binding.amountInputET.setText("");
            InputMethodManager imm =
                    (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        return binding.getRoot();
    }
}