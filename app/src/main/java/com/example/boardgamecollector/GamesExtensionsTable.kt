package com.example.boardgamecollector

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.*
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_games_extensions_table.*
import java.net.URL


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

        val dataTable = dataTable
        val tableHeader = TableRow(this)

        val headers = listOf("id", "Game Title", "Release Date", "Current rank", "Extension", "Img")
        val textViews = listOf(
            TextView(this),
            TextView(this),
            TextView(this),
            TextView(this),
            TextView(this),
            TextView(this)
        )

        for ((index, value) in textViews.withIndex()) {
            value.setTextColor(Color.WHITE)
            value.gravity = Gravity.CENTER
            value.text = headers[index]
            tableHeader.addView(value)
        }
        dataTable.addView(tableHeader)

        data.stream().forEach {
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
                value.setTextColor(Color.WHITE)
                value.text = valueHeaders[index].toString()
                valueTableRow.addView(value)
            }

            val imageView = ImageView(this)
            Picasso.get().load(it.image).into(imageView);
            valueTableRow.addView(imageView)
            dataTable.addView(valueTableRow)
        }


    }

}