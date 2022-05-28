package com.example.boardgamecollector

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_games_extensions_table.*


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
            loadData(data)
        }

        showExtensionsButton.setOnClickListener {
            val data = database.getDataGamesExtensions(QueriesTypes.GET_EXTENSIONS_BY_NAME_ASC)
            loadData(data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadData(data: ArrayList<GameInfo>) {
        data.stream().forEach {
            println(it)
            val tableRow = TableRow(this)
            val textView = TextView(this)
            textView.text = it.toString()
            tableRow.addView(textView)
            dataTable.addView(tableRow)
        }
    }
}