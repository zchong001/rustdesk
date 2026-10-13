package com.carriez.flutter_hbb

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.TextView

/**
 * RustDesk 远程输入法（自定义补丁，新增文件）
 */
class RustDeskIme : InputMethodService() {

    companion object {
        private const val TAG = "RustDeskIme"

        @Volatile
        private var instance: RustDeskIme? = null

        fun commitRemoteText(text: String): Boolean {
            val ime = instance ?: return false
            val conn = ime.currentInputConnection ?: return false
            return try {
                val ok = conn.commitText(text, 1)
                conn.finishComposingText()
                Log.i(TAG, "commitText(\"$text\") -> $ok")
                ok
            } catch (e: Exception) {
                Log.e(TAG, "commitText failed", e)
                false
            }
        }

        fun sendRemoteKeyEvent(event: KeyEvent): Boolean {
            val ime = instance ?: return false
            val conn = ime.currentInputConnection ?: return false
            val sent = conn.sendKeyEvent(event)
            if (event.action == KeyEvent.ACTION_DOWN) {
                conn.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, event.keyCode))
            }
            return sent
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.i(TAG, "onCreate")
    }

    override fun onDestroy() {
        instance = null
        Log.i(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onStartInput(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInput(editorInfo, restarting)
        Log.d(TAG, "onStartInput: ${editorInfo?.packageName}")
    }

    override fun onCreateInputView(): View {
        val view = TextView(this)
        view.text = "RustDesk 远程输入法\n（请勿在手机上输入，请在控制端键盘输入）"
        view.textSize = 13f
        view.setPadding(24, 18, 24, 18)
        return view
    }
}
