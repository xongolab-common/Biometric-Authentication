package com.example.biometricauthentication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.biometricauthentication.databinding.ActivityMainBinding
import java.util.concurrent.Executor
import androidx.biometric.BiometricManager

// https://www.youtube.com/watch?v=AapEF9ZL6mw

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor = ContextCompat.getMainExecutor(this)

        // Check if the device supports biometric authentication (including face recognition)
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Toast.makeText(this, "Biometric authentication is available", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "No biometric hardware available", Toast.LENGTH_LONG).show()
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometric hardware unavailable", Toast.LENGTH_LONG).show()
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No biometric data enrolled. Please set up Face Unlock or Fingerprint in settings.", Toast.LENGTH_LONG).show()
                return
            }
        }

        // Set up Biometric Authentication
        setupBiometricAuthentication()

        // Button click listener to start face authentication
        binding.btnAuthentication.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun setupBiometricAuthentication() {
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@MainActivity, "Authentication Error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(this@MainActivity, "Authentication Successful!", Toast.LENGTH_SHORT).show()
                // Navigate to the next activity or unlock the app
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@MainActivity, "Authentication Failed! Try again.", Toast.LENGTH_SHORT).show()
            }
        })

        // Create biometric prompt dialog
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Face Unlock")
            .setSubtitle("Use Face or Fingerprint to unlock")
            .setDescription("Look at the camera to authenticate")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG) // Ensures Face or Fingerprint is used
            .setNegativeButtonText("Cancel")
            .build()
    }
}
