package com.komputerkit.blogapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.komputerkit.blogapp.R
import com.komputerkit.blogapp.databinding.FragmentRegisterBinding
import com.komputerkit.blogapp.ui.home.HomeActivity
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            
            if (validateInput(email, password, confirmPassword)) {
                viewModel.register(email, password)
            }
        }
        
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
    
    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            return false
        }
        
        if (password.isEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            return false
        }
        
        if (password.length < 6) {
            binding.etPassword.error = "Password minimal 6 karakter"
            return false
        }
        
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
            return false
        }
        
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Password tidak sama"
            return false
        }
        
        return true
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        binding.btnRegister.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is AuthViewModel.AuthState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                    is AuthViewModel.AuthState.Error -> {
                        binding.btnRegister.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    is AuthViewModel.AuthState.Idle -> {
                        binding.btnRegister.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
    
    private fun navigateToHome() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}