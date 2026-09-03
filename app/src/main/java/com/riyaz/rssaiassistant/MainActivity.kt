package com.riyaz.rssaiassistant

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<TextInputEditText>(R.id.message_input)
        val send = findViewById<MaterialButton>(R.id.send_button)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)

        toolbar.setOnMenuItemClickListener { false }
        send.setOnClickListener {
            val message = input.text?.toString()?.trim().orEmpty()
            if (message.isNotEmpty()) {
                input.setText("")
                android.widget.Toast.makeText(this, "Message ready for the AI engine", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
