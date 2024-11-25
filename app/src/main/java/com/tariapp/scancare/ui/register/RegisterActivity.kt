package com.tariapp.scancare.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tariapp.scancare.R
import com.tariapp.scancare.ResultState
import com.tariapp.scancare.auth.AuthViewModel
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.ui.login.LoginActivity
import com.tariapp.scancare.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.apply {
//            btnRegist.setOnClickListener{
//                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
            linkToLogin.setOnClickListener{
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        setupAction()
        playAnimation()
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogoLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcometext = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(400)
        val slogantext = ObjectAnimator.ofFloat(binding.tvSlogan, View.ALPHA, 1f).setDuration(400)
        val slogantext2 = ObjectAnimator.ofFloat(binding.tvSlogan2, View.ALPHA, 1f).setDuration(400)
        val logintext = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(400)
        val signuptext = ObjectAnimator.ofFloat(binding.textSubLogin, View.ALPHA, 1f).setDuration(400)
        val nametext = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(400)
        val nameEdit = ObjectAnimator.ofFloat(binding.inputName, View.ALPHA, 1f).setDuration(400)
        val emailtext = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(400)
        val emailEdit = ObjectAnimator.ofFloat(binding.inputEmail, View.ALPHA, 1f).setDuration(400)
        val passwordtext = ObjectAnimator.ofFloat(binding.tvPass, View.ALPHA, 1f).setDuration(400)
        val passwordEdit = ObjectAnimator.ofFloat(binding.inputPassword, View.ALPHA, 1f).setDuration(400)
        val confirmtext = ObjectAnimator.ofFloat(binding.tvConfirmPass, View.ALPHA, 1f).setDuration(400)
        val confirmEdit = ObjectAnimator.ofFloat(binding.confirmInputPassword, View.ALPHA, 1f).setDuration(400)
        val signup = ObjectAnimator.ofFloat(binding.btnRegist, View.ALPHA, 1f).setDuration(400)
        val ask = ObjectAnimator.ofFloat(binding.tvAskSignup, View.ALPHA, 1f).setDuration(400)
        val link = ObjectAnimator.ofFloat(binding.linkToLogin, View.ALPHA, 1f).setDuration(400)

        val together = AnimatorSet().apply {
            playTogether(ask, link)
        }

        AnimatorSet().apply {
            playSequentially(welcometext, slogantext, slogantext2, logintext, signuptext, nametext, nameEdit, emailtext, emailEdit, passwordtext, passwordEdit, confirmtext, confirmEdit, signup, together)
            start()
        }
    }

    private fun setupAction() {
        binding.btnRegist.setOnClickListener {
            val name = binding.nameEdit.text.toString().trim()
            val email = binding.emailEdit.text.toString().trim()
            val password = binding.passEdit.text.toString().trim()
            val confirmPassword = binding.confirmEdit.text.toString().trim()
            when{
                name.isEmpty() -> {
                    binding.inputName.error = getString(R.string.empty)
                }
                email.isEmpty() -> {
                    binding.inputEmail.error = getString(R.string.empty)
                }
                password.isEmpty() -> {
                    binding.inputPassword.error = getString(R.string.empty)
                }
                confirmPassword.isEmpty() -> {
                    binding.confirmInputPassword.error = getString(R.string.empty)
                }

                else -> {
                    binding.inputName.error = null
                    binding.inputEmail.error = null
                    binding.inputPassword.error = null
                    binding.confirmInputPassword.error = null

                    viewModel.register(name, email, password, confirmPassword).observe(this){ result ->
                        if (result != null) {
                            when(result){
                                is ResultState.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    navigateToLoginActivity()
                                }

                                is ResultState.Error -> {
                                    showToast(result.error)
                                    binding.progressBar.visibility = View.GONE
                                }

                                is ResultState.Loading -> {
                                    showLoading()

                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}