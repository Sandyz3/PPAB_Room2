package com.example.ppab_room2.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ppab_room2.database.Note
import com.example.ppab_room2.database.NoteDao
import com.example.ppab_room2.database.NoteRoomDatabase
import com.example.ppab_room2.databinding.ActivityMainBinding
import java.nio.file.Files.delete
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private lateinit var ArrayData : LiveData<List<Note>>
    private var updateId: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao  = db!!.noteDao()!!
        ArrayData = db!!.noteDao()!!.allNotes
        getAllNotes()

        with(binding) {
            btnadd.setOnClickListener{
                val intentToInput = Intent(this@MainActivity, InputActivity::class.java)
                intentToInput.putExtra("COMMAND", "ADD")
                startActivity(intentToInput)
            }
        }
    }

    private fun getAllNotes() {
        mNotesDao.allNotes.observe(this) { notes ->
            if (notes.isNotEmpty()) { // Periksa apakah daftar catatan tidak kosong
                binding.rvnotes.isVisible = true
                binding.textEmpty.isVisible = false
                val recyclerAdapter = DataAdapter(notes)
                binding.rvnotes.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    setHasFixedSize(true)
                    adapter = recyclerAdapter
                }
            }else{
                binding.rvnotes.isVisible = false
                binding.textEmpty.isVisible = true
            }
        }
    }

    private fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }

    private fun delete(note: Note) {
        executorService.execute { mNotesDao.delete(note) }
    }

    private fun update(note: Note) {
        executorService.execute { mNotesDao.update(note) }
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }
}