package com.riyaz.rssaiassistant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val email = getSharedPreferences("rss_ai", MODE_PRIVATE).getString("email", "") ?: ""
        val name = email.substringBefore("@").replace(".", " ").replace("_", " ").trim()
        findViewById<android.widget.TextView>(R.id.welcome_name).text =
            if (name.isBlank()) getString(R.string.default_user_name) else name.replaceFirstChar { it.uppercase() }

        findViewById<MaterialButton>(R.id.start_button).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
