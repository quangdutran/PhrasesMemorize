package com.dutq.mywordingapp

import android.app.AlertDialog
import android.content.Context

class DialogBuilder {
    companion object {
        fun deleteConfirmation(context: Context,
                               procedure: Procedure
                               ) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete the item?")
            builder.setPositiveButton("YES") { dialog, _ ->
                procedure.run()
                dialog.dismiss()
            }
            builder.setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }

        fun showSentence(context: Context, sentence: String, procedure: Procedure?) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(sentence)
            procedure?.let {
                builder.setPositiveButton("LISTEN", null)
                builder.setNegativeButton("CLOSE") {dialog, _ -> dialog.dismiss()}
            }
            val alert = builder.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { procedure?.run() }
        }
    }
}