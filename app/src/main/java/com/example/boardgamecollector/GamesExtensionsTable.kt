package com.example.boardgamecollector

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.*
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_games_extensions_table.*
import java.lang.Exception


class GamesExtensionsTable : AppCompatActivity() {

    private lateinit var database: DatabaseHelper


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_extensions_table)
        database = DatabaseHelper(this)
        addListeners()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addListeners() {
        showGamesButton.setOnClickListener {
            val data = database.getDataGamesExtensions(QueriesTypes.GET_GAMES_BY_NAME_ASC)
            createTable(data)
        }

        showExtensionsButton.setOnClickListener {
            val data = database.getDataGamesExtensions(QueriesTypes.GET_EXTENSIONS_BY_NAME_ASC)
            createTable(data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createTable(data: ArrayList<GameInfo>) {

        dataTable.removeAllViews()

        //header
        val tableHeader = TableRow(this)
        val headers = listOf("Id", "Game Title", "Release Date", "Current rank", "Extension", "Image")
        val textViews = listOf(
            TextView(this),
            TextView(this),
            TextView(this),
            TextView(this),
            TextView(this),
            TextView(this)
        )

        for ((index, value) in textViews.withIndex()) {
            value.setTextColor(Color.parseColor("#19678b"))
            value.setBackgroundColor(Color.parseColor("#1F2739"))
            value.textSize = 20.toFloat()
            value.gravity = Gravity.CENTER
            value.text = headers[index]
            value.setPadding(30, 30, 30, 30)
            tableHeader.addView(value)
        }
        dataTable.addView(tableHeader)


        //data
        data.stream().forEach {
            println(it)
            val valueTableRow = TableRow(this)
            val valueHeaders =
                listOf(it.id, it.gameName, it.yearPublished, it.currentRank, it.extension)
            val valueTextViews = listOf(
                TextView(this),
                TextView(this),
                TextView(this),
                TextView(this),
                TextView(this)
            )

            for ((index, value) in valueTextViews.withIndex()) {
                when (index) {
                    0 -> value.setTextColor(Color.parseColor("#FB667A"))
                    else -> value.setTextColor(Color.parseColor("#A7A1AE"))
                }
                value.text = valueHeaders[index].toString()
                value.gravity = Gravity.CENTER
                value.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
                value.maxWidth = 300
                value.textSize = 15.toFloat()
                value.setPadding(30, 30, 30, 30)
                valueTableRow.addView(value)
            }


            try{
                val imageView = ImageView(this)
                imageView.setPadding(30, 30, 30, 30)
                Picasso.get().load(it.image).resize(250, 0).into(imageView)
                valueTableRow.addView(imageView)
            } catch (e: Exception){
                val textView = TextView(this)
                textView.setTextColor(Color.WHITE)
                textView.setPadding(30, 30, 30, 30)
                textView.gravity = Gravity.CENTER
                textView.text = ""
                valueTableRow.addView(textView)
            } finally {
                dataTable.addView(valueTableRow)
            }

        }


    }

}