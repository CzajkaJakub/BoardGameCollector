package com.example.boardgamecollector

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_games_extensions_table.*
import java.lang.Exception
import kotlin.collections.ArrayList

class GamesExtensionsTable : AppCompatActivity() {

    private lateinit var database: DatabaseHelper
    private lateinit var gameData: ArrayList<GameInfo>
    private var sortedType: SortedTypes = SortedTypes.GAME_NAME


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
            gameData = database.getDataGamesExtensions(QueriesTypes.GET_GAMES_BY_NAME_ASC)
            createHeaders()
        }

        showExtensionsButton.setOnClickListener {
            gameData = database.getDataGamesExtensions(QueriesTypes.GET_EXTENSIONS_BY_NAME_ASC)
            createHeaders()
        }

        showAllButton.setOnClickListener {
            gameData = database.getDataGamesExtensions(QueriesTypes.GET_ALL_BY_NAME_ASC)
            createHeaders()
        }

        backToMainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createHeaders() {
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

            when (headers[index]) {
                "Game Title" -> {
                    value.setTextColor(Color.parseColor("#FF00FF37"))
                    value.setOnClickListener {
                        if(sortedType == SortedTypes.GAME_NAME) {
                            gameData.reverse()
                        } else {
                            sortedType = SortedTypes.GAME_NAME
                            gameData.sortBy { it.gameName }
                        }
                        fillTableWithData()
                    }
                }
                "Release Date" -> {
                    value.setTextColor(Color.parseColor("#FF00FF37"))
                    value.setOnClickListener {
                        if(sortedType == SortedTypes.DATE_RELEASE) {
                            gameData.reverse()
                        } else {
                            sortedType = SortedTypes.DATE_RELEASE
                            gameData.sortBy { it.getIntYear() }
                        }
                        fillTableWithData()
                    }
                }
                "Current rank" -> {
                    value.setTextColor(Color.parseColor("#FF00FF37"))
                    value.setOnClickListener {
                        if(sortedType == SortedTypes.CURRENT_RANK){
                            gameData.reverse()
                        } else {
                            sortedType = SortedTypes.CURRENT_RANK
                            gameData.sortBy { it.getIntRank() }
                        }
                        fillTableWithData()
                    }
                }
            }
            tableHeader.addView(value)
        }
        dataTable.addView(tableHeader)
        fillTableWithData()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fillTableWithData() {
        //data
        dataTable.removeViews(1, dataTable.childCount - 1)
        var rowNum = 0
        gameData.stream().forEach {
            rowNum++
            val valueTableRow = TableRow(this)
            val valueHeaders =
                listOf(rowNum, it.gameName, it.yearPublished, it.currentRank, it.extension)
            val valueTextViews = listOf(
                TextView(this),
                TextView(this),
                TextView(this),
                TextView(this),
                TextView(this)
            )

            for ((index, value) in valueTextViews.withIndex()) {

                value.text = valueHeaders[index].toString()
                value.gravity = Gravity.CENTER
                value.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
                value.maxWidth = 300
                value.textSize = 15.toFloat()
                value.setPadding(30, 30, 30, 30)

                when (index) {
                    0 -> value.setTextColor(Color.parseColor("#FB667A"))
                    4 -> if (it.extension) value.text = getString(R.string.extensionTrue) else value.text = getString(R.string.extensionFalse)
                    else -> value.setTextColor(Color.parseColor("#A7A1AE"))
                }
                valueTableRow.addView(value)
            }
            tryLoadImages(valueTableRow, it)
        }
    }

    private fun tryLoadImages(valueTableRow: TableRow, gameInfo: GameInfo) {
        var imageView : View? = null
        try{
            imageView = ImageView(this)
            Picasso.get().load(gameInfo.image).resize(250, 0).into(imageView)
        } catch (e: Exception){
            imageView = TextView(this)
            imageView.setTextColor(Color.WHITE)
            imageView.text = gameInfo.image
            imageView.gravity = Gravity.CENTER
        } finally {
            imageView!!.setPadding(30, 30, 30, 30)
            valueTableRow.addView(imageView)
            dataTable.addView(valueTableRow)
        }
    }
}