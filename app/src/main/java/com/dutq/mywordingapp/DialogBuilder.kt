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
            builder.setPositiveButton("YES") { dialog, i ->
                procedure.run()
                dialog.dismiss()
            }
            builder.setNegativeButton("NO") { dialog, i ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }
    }
}