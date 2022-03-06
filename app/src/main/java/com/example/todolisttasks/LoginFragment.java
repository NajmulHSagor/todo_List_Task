package com.example.todolisttasks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolisttasks.databinding.FragmentLoginBinding;
import com.example.todolisttasks.viewmodels.LoginViewModel;


public class LoginFragment extends Fragment {

 private LoginViewModel loginViewModel;
 private FragmentLoginBinding binding;
 private Boolean isLogin;


    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater);
        loginViewModel = new ViewModelProvider(requireActivity())
                .get(LoginViewModel.class);
        binding.loginBtn.setOnClickListener(V ->{
            isLogin = true;
            authenticate();
        });
        binding.registerBtn.setOnClickListener(V->{
            isLogin = false;
            authenticate();
        });
        loginViewModel.getStateLiveData()
                .observe(getViewLifecycleOwner(),authState -> {
                    if (authState == LoginViewModel.AuthState.AUTHENTICATED){
                        Navigation.findNavController(container)
                                .navigate(R.id.action_loginFragment_to_expenseListFragment2);

                    }

                });
        loginViewModel.getErrMsgLiveData()
                .observe(getViewLifecycleOwner(),errMsg->{
                    binding.errMsgTV.setText(errMsg);

                });
        return binding.getRoot();
    }

    private void authenticate() {
        final String email = binding.emailET.getText().toString();
        final String password = binding.passwordET.getText().toString();
        if (isLogin){
            loginViewModel.login(email,password);
        }else {
            loginViewModel.register(email,password);
        }

    }
}