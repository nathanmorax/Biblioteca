package com.example.libros

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView

class AdapterRecord(): RecyclerView.Adapter<AdapterRecord.HolderRecord>() {

    private  lateinit var context :Context
    var recordList = emptyList<ModelRecord>()

    constructor(context: Context): this() {
        this.context = context
    }

    class HolderRecord(itemView: View) : RecyclerView.ViewHolder(itemView){
        var titleIv : TextView = itemView.findViewById(R.id.rTitle)
        var authorIv : TextView = itemView.findViewById(R.id.rAuthor)
        var editorialIv : TextView = itemView.findViewById(R.id.rEditorial)
        var yearIv : TextView = itemView.findViewById(R.id.rYear)
        var profileIv : ImageView = itemView.findViewById(R.id.rBook)
        var edit : AppCompatImageButton = itemView.findViewById(R.id.editBtn)
        var deleteButton : AppCompatImageButton = itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        return HolderRecord(LayoutInflater.from(context).inflate(R.layout.record_list, parent, false))
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        val modelRecord = recordList[position]
        val id = modelRecord.id
        val title = modelRecord.title
        val author = modelRecord.author
        val editorial = modelRecord.editorial
        val year = modelRecord.year
        val image = modelRecord.image?.toBitmap()

        holder.titleIv.text = title
        holder.authorIv.text = author
        holder.editorialIv.text = editorial
        holder.yearIv.text = year

        if(image == null) {
            holder.profileIv.setImageResource(R.drawable.ic_book_foreground)
        } else {
            holder.profileIv.setImageBitmap(image)
        }

        holder.itemView.setOnClickListener{

        }

        holder.edit.setOnClickListener{
            val intent = Intent(context, AddRecordActivity::class.java)
            intent.putExtra("book", modelRecord)
            context.startActivity(intent)
        }
        holder.deleteButton.setOnClickListener{
            (context as MainActivity).deleteBook(id)
        }
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

}
