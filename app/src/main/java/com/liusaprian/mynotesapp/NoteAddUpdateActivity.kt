package com.liusaprian.mynotesapp

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.liusaprian.mynotesapp.db.DatabaseContract
import com.liusaprian.mynotesapp.db.NoteHelper
import com.liusaprian.mynotesapp.entity.Note
import kotlinx.android.synthetic.main.activity_note_add_update.*
import java.text.SimpleDateFormat
import java.util.*

class NoteAddUpdateActivity : AppCompatActivity(), View.OnClickListener {

    private var isEdit = false
    private var note: Note? = null
    private var position: Int = 0
    private lateinit var noteHelper: NoteHelper

    companion object {
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_add_update)

        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        note = intent.getParcelableExtra(EXTRA_NOTE)
        if(note != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        }
        else note = Note()

        val actionBarTitle: String
        val btnTitle: String

        if(isEdit) {
            actionBarTitle = "Ubah"
            btnTitle = "Update"

            note?.let {
                edit_title.setText(it.title)
                edit_desc.setText(it.description)
            }
        }
        else {
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        submit.text = btnTitle
        submit.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        if(p0.id == R.id.submit) {
            val title = edit_title.text.toString().trim()
            val desc = edit_desc.text.toString().trim()

            if(title.isEmpty()) {
                edit_title.error = "Field cannot be blank"
                return
            }

            note?.title = title
            note?.description = desc

            val intent = Intent()
            intent.putExtra(EXTRA_NOTE, note)
            intent.putExtra(EXTRA_POSITION, position)

            val values = ContentValues()
            values.put(DatabaseContract.NoteColumns.TITLE, title)
            values.put(DatabaseContract.NoteColumns.DESCRIPTION, desc)

            if(isEdit) {
                val result = noteHelper.update(note?.id.toString(), values).toLong()
                if(result > 0) {
                    setResult(RESULT_UPDATE, intent)
                    finish()
                }
                else Toast.makeText(this, "Gagal mengupdate data", Toast.LENGTH_SHORT).show()
            }
            else {
                note?.date = getCurrentDate()
                values.put(DatabaseContract.NoteColumns.DATE, getCurrentDate())
                val result = noteHelper.insert(values)

                if(result > 0) {
                    note?.id =  result.toInt()
                    setResult(RESULT_ADD, intent)
                    finish()
                }
                else Toast.makeText(this, "Gagal menambah data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(isEdit) menuInflater.inflate(R.menu.menu_form, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog(alertCode: Int) {
        val isDialogClose = alertCode == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if(isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form ?"
        }
        else {
            dialogTitle = "Hapus Note"
            dialogMessage = "Apakah anda ingin menghapus item ini ?"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                if(isDialogClose) finish()
                else {
                    val result = noteHelper.delete(note?.id.toString()).toLong()
                    if(result > 0) {
                        val intent = Intent()
                        intent.putExtra(EXTRA_POSITION, position)
                        setResult(RESULT_DELETE, intent)
                        finish()
                    }
                    else Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Tidak") {dialog, _ -> dialog.cancel()}
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }
}