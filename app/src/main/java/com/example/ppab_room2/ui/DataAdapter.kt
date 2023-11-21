package com.example.ppab_room2.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ppab_room2.R
import com.example.ppab_room2.database.Note
import com.example.ppab_room2.database.NoteDao
import com.example.ppab_room2.database.NoteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DataAdapter (val dataNotes: List<Note>?): RecyclerView.Adapter<DataAdapter.MyViewHolder>() {
    lateinit var executorService: ExecutorService
    lateinit var mNoteDao: NoteDao

    class MyViewHolder (view: View): RecyclerView.ViewHolder(view){
        val title = view.findViewById<TextView>(R.id.title_txt)
        val desc = view.findViewById<TextView>(R.id.desc_txt)
        val date = view.findViewById<TextView>(R.id.date_txt)
        val btnUpdate = view.findViewById<TextView>(R.id.btn_update)
        val btnDelete = view.findViewById<TextView>(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rvnotes, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (dataNotes!= null){
            return dataNotes.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(holder.itemView.context)
        mNoteDao = db!!.noteDao()!!

        holder.title.text = "title : ${dataNotes?.get(position)?.title}"
        holder.desc.text = "description : ${dataNotes?.get(position)?.description}"
        holder.date.text = "date : ${dataNotes?.get(position)?.date}"

        holder.btnUpdate.setOnClickListener{
            val intentToDetails = Intent(holder.itemView.context, InputActivity::class.java)
            intentToDetails.putExtra("ID", dataNotes?.get(position)?.id)
            intentToDetails.putExtra("TITTLE", dataNotes?.get(position)?.title)
            intentToDetails.putExtra("DESC", dataNotes?.get(position)?.description)
            intentToDetails.putExtra("DATE", dataNotes?.get(position)?.date)
            intentToDetails.putExtra("COMMAND", "UPDATE")
            holder.itemView.context.startActivity(intentToDetails)
        }

        holder.btnDelete.setOnClickListener{
            val noteId = dataNotes?.get(position)?.id
            noteId?.let { deleteNoteById(it) }
            Toast.makeText(holder.itemView.context, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun deleteNoteById(noteId: Int){
        executorService.execute {
            mNoteDao.deleteById(noteId)
        }
    }
}