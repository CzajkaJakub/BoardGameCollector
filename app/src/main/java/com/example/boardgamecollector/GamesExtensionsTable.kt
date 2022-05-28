package com.example.boardgamecollector

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_games_extensions_table.*
import kotlin.collections.ArrayList


class GamesExtensionsTable : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    private lateinit var tableData: TableLayout

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
        tableData = findViewById(R.id.dataTable)
        tableData.removeAllViews()
        val headers = listOf("id", "Game Title", "Release Date", "Current rank", "Extension", "Img")
        val textViews = listOf(TextView(this), TextView(this), TextView(this), TextView(this), TextView(this), TextView(this))
        val tableRow = TableRow(this)
        for ((index, value) in textViews.withIndex()) {
            value.setTextColor(Color.RED)
            value.text = headers[index]
            tableRow.addView(value)
        }
        tableData.addView(tableRow)

        data.stream().forEach {
            val valueHeaders =
                listOf(it.id, it.gameName, it.yearPublished, it.currentRank, it.extension, it.image)
            val valueTextViews = listOf(
                TextView(this),
                TextView(this),
                TextView(this),
                TextView(this),
                TextView(this),
                TextView(this)
            )
            val valueTableRow = TableRow(this)
            for ((index, value) in valueTextViews.withIndex()) {
                value.setTextColor(Color.BLUE)
                value.text = valueHeaders[index].toString()
                valueTableRow.addView(value)
            }
            tableData.addView(valueTableRow)
        }
    }
}