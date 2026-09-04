package com.riyaz.rssaiassistant

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
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
    private val handler = Handler(Looper.getMainLooper())
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
        if (message.isEmpty() || send.isEnabled.not()) return

        input.setText("")
        addMessage(message, true)
        appendConversation("U", message)
        setComposerEnabled(false)

        val typing = addMessage("Thinking…", false)
        handler.postDelayed({
            messageContainer.removeView(typing)
            val response = "I’m ready to help. The secure AI service layer is being connected next, so real model responses will replace this local response."
            addMessage(response, false)
            appendConversation("A", response)
            setComposerEnabled(true)
            input.requestFocus()
        }, 650)
    }

    private fun addMessage(text: String, fromUser: Boolean): LinearLayout {
        emptyState.visibility = View.GONE
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
        return row
    }

    private fun appendConversation(role: String, text: String) {
        val old = prefs.getString("conversation", "").orEmpty()
        val entry = "$role|${text.replace("\\n", " ")}"
        val updated = if (old.isEmpty()) entry else "$old\\n$entry"
        prefs.edit().putString("conversation", updated.takeLast(20000)).apply()
    }

    private fun loadHistory() {
        val history = prefs.getString("conversation", "").orEmpty()
        if (history.isEmpty()) return
        history.split("\\n").forEach { line ->
            if (line.length > 2 && line[1] == '|') {
                addMessage(line.substring(2), line[0] == 'U')
            }
        }
        scrollToBottom()
    }

    private fun setComposerEnabled(enabled: Boolean) {
        send.isEnabled = enabled
        input.isEnabled = enabled
        if (enabled) input.alpha = 1f else input.alpha = 0.65f
    }

    private fun scrollToBottom() {
        scroll.post { scroll.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
