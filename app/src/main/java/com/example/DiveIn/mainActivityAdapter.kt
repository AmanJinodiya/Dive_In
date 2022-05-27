package com.example.DiveIn

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*


class mainActivityAdapter(var list : ArrayList<File>,var del_check:Boolean,var pp : String) : RecyclerView.Adapter<mainActivityAdapter.MyViewHolder>(),Filterable {

    lateinit var context: Context
    var updated_list = ArrayList<File>()

    init {
        updated_list = list
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.iv)
        var name = itemView.findViewById<TextView>(R.id.txtsong)
        var del = itemView.findViewById<Button>(R.id.button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context

        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item1, parent, false)
        )
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var name_of_song =
            updated_list.get(position).name.toString().replace(".mp3", "").replace(".wav", "")
        holder.name.text = name_of_song
        holder.name.isSelected = true

        val mmr = MediaMetadataRetriever()

        if (del_check == true) {
            holder.del.setVisibility(View.VISIBLE)
        } else {
            holder.del.setVisibility(View.GONE)
        }

        holder.del.setOnClickListener {
            var f = updated_list[position]
            f.delete()
            updated_list.remove(f)
            Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }
        mmr.setDataSource(updated_list.get(position).path)
        val data = mmr.embeddedPicture
        mmr.release()
        var bitmap: Bitmap? = null
        if (data != null) bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

        if (data == null) {
            holder.image.setImageResource(R.drawable.temp2)
        } else holder.image.setImageBitmap(bitmap)

        holder.itemView.setOnClickListener {
            val pause_me = 0
            val p = list.indexOf(updated_list[position])

            val intent = Intent(context, recycle::class.java).putExtra("songname", name_of_song)
                .putExtra("songs", list).putExtra("pos", p).putExtra("pause_me", pause_me)
                .putExtra("pat", pp).putExtra("all_is_not", "all")
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return updated_list.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    updated_list = list
                } else {
                    val resultList = ArrayList<File>()
                    for (row in list) {
                        if (row.name.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    updated_list = resultList

                }
                val filterResults = FilterResults()
                filterResults.values = updated_list
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                updated_list =
                    results?.values as ArrayList<File> /* = java.util.ArrayList<java.io.File> */
                notifyDataSetChanged()
            }


        }


    }
}