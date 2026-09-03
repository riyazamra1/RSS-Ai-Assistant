package com.riyaz.rssaiassistant

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val email = findViewById<EditText>(R.id.email_input)
        val continueButton = findViewById<MaterialButton>(R.id.continue_button)
        val createButton = findViewById<MaterialButton>(R.id.create_button)

        continueButton.setOnClickListener { continueWithEmail(email) }
        createButton.setOnClickListener { continueWithEmail(email) }
    }

    private fun continueWithEmail(email: EditText) {
        val value = email.text.toString().trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            email.error = getString(R.string.invalid_email)
            return
        }
        getSharedPreferences("rss_ai", MODE_PRIVATE).edit().putString("email", value).apply()
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}
