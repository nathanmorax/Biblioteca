package com.example.libros

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.SearchView



class MainActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    var recordList = emptyList<ModelRecord>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRecord
    private lateinit var searchView : SearchView
    private lateinit var dbHelper: DatabaseHelper

    var dbH = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.search)




        fab = findViewById(R.id.addFabButton)
        recyclerView = findViewById(R.id.recyclerview_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterRecord(this)
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            startActivity(Intent(this, AddRecordActivity::class.java))
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                dbH.searchRecords(query = String())
                return true
            }

        })

    }


    override fun onResume() {
        super.onResume()
        reload()
    }

    fun reload() {
        adapter.recordList = dbH.getAllRecords()
        adapter.notifyDataSetChanged()
    }

    fun deleteBook(id: String) {
        dbH.deleteBook(id)
        reload()
    }
}