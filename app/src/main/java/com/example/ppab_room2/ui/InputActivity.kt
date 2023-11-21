package com.example.ppab_room2.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.ppab_room2.database.Note
import com.example.ppab_room2.database.NoteDao
import com.example.ppab_room2.database.NoteRoomDatabase
import com.example.ppab_room2.databinding.ActivityInputBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class InputActivity : AppCompatActivity(){
    private lateinit var binding: ActivityInputBinding
    private lateinit var mNotesDao : NoteDao
    private lateinit var executorService: ExecutorService
    private var updateId: Int= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInputBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!
        var command = intent.getStringExtra("COMMAND")

        with(binding) {
            if(command=="UPDATE"){
                binding.btnAdd.isVisible = false
                binding.btnUpdate.isVisible = true
                updateId = intent.getIntExtra("ID", 0)

                var item_title = intent.getStringExtra("TITLE")
                var item_desc = intent.getStringExtra("DESCRIPTION")
                var item_date = intent.getStringExtra("DATE")
            }

            else {
                binding.btnAdd.isVisible = true
                binding.btnUpdate.isVisible = false
            }

            btnAdd.setOnClickListener(View.OnClickListener {
                if (validateInput()) {
                    insert(
                        Note(
                            title = txtTitle.text.toString(),
                            description = txtDesc.text.toString(),
                            date = txtDate.text.toString()
                        )
                    )
                    setEmptyField()
                    val IntentToHome = Intent(this@InputActivity, MainActivity::class.java)
                    Toast.makeText(this@InputActivity, "Berhasil Menambahkan Data", Toast.LENGTH_SHORT).show()
                    startActivity(IntentToHome)
                }else{
                    Toast.makeText(this@InputActivity, "Kolom Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()
                }
            })

            btnUpdate.setOnClickListener {
                if (validateInput()) {
                    update(
                        Note(
                            id = updateId,
                            title = txtTitle.text.toString(),
                            description = txtDesc.text.toString(),
                            date = txtDate.text.toString()
                        )
                    )
                    updateId = 0
                    setEmptyField()
                    val IntentToHome = Intent(this@InputActivity, MainActivity::class.java)
                    Toast.makeText(
                        this@InputActivity,
                        "Berhasil Mengupdate Data",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(IntentToHome)
                } else {
                    Toast.makeText(
                        this@InputActivity,
                        "Kolom Tidak Boleh Kosong !!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
//        getAllNotes()
    }

    private fun setEmptyField() {
        with(binding) {
            txtTitle.setText("")
            txtDate.setText("")
            txtDesc.setText("")
        }
    }

    private fun validateInput(): Boolean {
        with(binding) {
            if (txtDate.text.toString() != "" && txtTitle.text.toString() != "" && txtDesc.text.toString() != "") {
                return true
            } else {
                return false
            }
        }
    }
}