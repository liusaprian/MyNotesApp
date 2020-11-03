package com.liusaprian.mynotesapp.helper

import android.database.Cursor
import com.liusaprian.mynotesapp.db.DatabaseContract
import com.liusaprian.mynotesapp.entity.Note

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notes = ArrayList<Note>()

        notesCursor?.apply {
            while(moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                val desc = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
                notes.add(Note(id, title, desc, date))
            }
        }
        return notes
    }
}