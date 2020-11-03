package com.liusaprian.mynotesapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liusaprian.mynotesapp.CustomOnItemClickListener
import com.liusaprian.mynotesapp.NoteAddUpdateActivity
import com.liusaprian.mynotesapp.R
import com.liusaprian.mynotesapp.entity.Note
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(private val activity: Activity) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var notes = ArrayList<Note>()
        set(notes) {
            if(notes.size > 0) this.notes.clear()
            this.notes.addAll(notes)
            notifyDataSetChanged()
        }

    fun addItem(note: Note) {
        this.notes.add(note)
        notifyItemInserted(this.notes.size - 1)
    }

    fun updateItem(position: Int, note: Note) {
        this.notes[position] = note
        notifyItemChanged(position, note)
    }

    fun removeItem(position: Int) {
        this.notes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.notes.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteAdapter.NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount() = this.notes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: Note) {
            with(itemView) {
                title.text = note.title
                date.text = note.date
                desc.text = note.description
                note_item_view.setOnClickListener(CustomOnItemClickListener(adapterPosition, object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val toDetail = Intent(activity, NoteAddUpdateActivity::class.java)
                        toDetail.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position)
                        toDetail.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, note)
                        activity.startActivityForResult(toDetail, NoteAddUpdateActivity.REQUEST_UPDATE)
                    }
                }))
            }
        }
    }
}