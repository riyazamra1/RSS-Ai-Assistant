package com.riyaz.rssaiassistant

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var input: TextInputEditText
    private lateinit var send: MaterialButton
    private lateinit var messageContainer: LinearLayout
    private lateinit var emptyState: LinearLayout
    private lateinit var scroll: ScrollView

    private val prefs by lazy { getSharedPreferences("rss_ai_chat", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        input = findViewById(R.id.message_input)
        send = findViewById(R.id.send_button)
        messageContainer = findViewById(R.id.message_container)
        emptyState = findViewById(R.id.empty_state)
        scroll = findViewById(R.id.message_scroll)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener { finish() }
        loadHistory()

        send.setOnClickListener { submitMessage() }
        input.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.keyCode == 66)) {
                submitMessage()
                true
            } else false
        }
    }

    private fun submitMessage() {
        val message = input.text?.toString()?.trim().orEmpty()
        if (message.isEmpty()) return

        input.setText("")
        addMessage(message, true)
        saveUserMessage(message)

        // AI service is intentionally not called yet. This placeholder keeps the
        // conversation UI functional until the secure backend is connected.
        addMessage("I received your message. The AI service connection is the next stage.", false)
        scrollToBottom()
    }

    private fun addMessage(text: String, fromUser: Boolean) {
        emptyState.visibility = android.view.View.GONE

        val bubble = TextView(this).apply {
            this.text = text
            textSize = 16f
            setTextColor(if (fromUser) Color.WHITE else Color.rgb(32, 42, 58))
            setPadding(18, 13, 18, 13)
            background = GradientDrawable().apply {
                cornerRadius = 22f
                setColor(if (fromUser) Color.rgb(49, 89, 166) else Color.WHITE)
            }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = if (fromUser) Gravity.END else Gravity.START
            setPadding(0, 5, 0, 5)
        }
        val params = LinearLayout.LayoutParams(
            (resources.displayMetrics.widthPixels * 0.82f).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.addView(bubble, params)
        messageContainer.addView(row)
        scrollToBottom()
    }

    private fun saveUserMessage(message: String) {
        val old = prefs.getString("messages", "").orEmpty()
        val updated = if (old.isEmpty()) message else "$old\\n$message"
        prefs.edit().putString("messages", updated.takeLast(12000)).apply()
    }

    private fun loadHistory() {
        val history = prefs.getString("messages", "").orEmpty()
        if (history.isEmpty()) return
        history.split("\\n").filter { it.isNotBlank() }.forEach { addMessage(it, true) }
        scrollToBottom()
    }

    private fun scrollToBottom() {
        scroll.post { scroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }
}
