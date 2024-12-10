package com.tariapp.scancare.ui.login

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
import com.tariapp.scancare.MainActivity
import com.tariapp.scancare.R
import com.tariapp.scancare.ResultState
import com.tariapp.scancare.auth.AuthViewModel
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.ui.register.RegisterActivity
import com.tariapp.scancare.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.apply {
            linkToSignup.setOnClickListener{
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
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
        val emailtext = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(400)
        val emailEdit = ObjectAnimator.ofFloat(binding.inputEmail, View.ALPHA, 1f).setDuration(400)
        val passwordtext = ObjectAnimator.ofFloat(binding.tvPass, View.ALPHA, 1f).setDuration(400)
        val passwordEdit = ObjectAnimator.ofFloat(binding.inputPassword, View.ALPHA, 1f).setDuration(400)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(400)
        val ask = ObjectAnimator.ofFloat(binding.tvAskLogin, View.ALPHA, 1f).setDuration(400)
        val link = ObjectAnimator.ofFloat(binding.linkToSignup, View.ALPHA, 1f).setDuration(400)

        val together = AnimatorSet().apply {
            playTogether(ask, link)
        }

        AnimatorSet().apply {
            playSequentially(welcometext, slogantext, slogantext2, logintext, signuptext, emailtext, emailEdit, passwordtext, passwordEdit, login, together)
            start()
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passEditText.text.toString().trim()
            when{
                email.isEmpty() -> {
                    binding.inputEmail.error = getString(R.string.empty)
                }
                password.isEmpty() -> {
                    binding.inputPassword.error = getString(R.string.empty)
                }

                else -> {
                    binding.inputEmail.error = null
                    binding.inputPassword.error = null

                    viewModel.login(email, password).observe(this){ result ->
                        if (result != null) {
                            when (result) {
                                is ResultState.Success -> {
//                                    loginUser(result.data.token)
                                    navigateToMain()
                                    binding.progressBar.visibility = View.GONE

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

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }
}