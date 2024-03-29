package com.example.boardgamecollector

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_main.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private val mapper = jacksonObjectMapper()
    private lateinit var user : UserSettings
    private lateinit var dataFileGames : String
    private lateinit var dataFileExtensions : String
    private val request = Request()
    private lateinit var databaseAccess : DatabaseHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(checkUsersSettings()){
            addListeners()
        }
    }

    private fun checkUsersSettings(): Boolean {
        val userPath = this.filesDir.toString().plus("/user.json")
        return if(File(userPath).exists()){
            setContentView(R.layout.activity_main)
            databaseAccess = DatabaseHelper(this)
            dataFileGames = "$filesDir/gamesData.xml"
            dataFileExtensions = "$filesDir/extensionsData.xml"
            readUserAndReloadFields(userPath)
            true
        } else {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            false
        }
    }

    private fun readUserAndReloadFields(userPath: String) {
        runOnUiThread {
            user = mapper.readValue(File(userPath))
            usernameView.text = user.username
            amountOfGamesLabel.text = user.amountOfGame.toString()
            amountOfAdditionsLabel.text = user.amountOfExtensions.toString()
            lastSynchronizedDate.text = user.lastSynchronizedDate
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addListeners() {
        synchronizedDataButton.setOnClickListener {
            if(checkLastSynchronizedDate()){
                showWarningDialog()
            } else {
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    parseXmlCollectionFile()
                }
            }
        }

        clearDataButton.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle(R.string.clear_data)
                .setMessage(R.string.confirm_clear_data_message)
                .setPositiveButton(R.string.confirmation_yes) { _, _ ->
                    val filesToClear = listOf("/user.json", "/gamesData.xml", "/extensionsData.xml")
                    filesToClear.stream().forEach { File(this.filesDir.toString().plus(it)).delete() }
                    databaseAccess.truncateDatabase()
                    val intent = Intent(this, Settings::class.java)
                    startActivity(intent)
                }
                .setNegativeButton(R.string.confirmation_no, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
        }

        showDataTable.setOnClickListener{
            val intent = Intent(this, GamesExtensionsTable::class.java)
            startActivity(intent)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkLastSynchronizedDate(): Boolean {
        if(user.lastSynchronizedDate != "Synchronize to get data!"){

            val lastSynchronizedUserDay = LocalDateTime.parse(user.lastSynchronizedDate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).dayOfYear
            val currentDay = LocalDateTime.now().dayOfYear
            if(lastSynchronizedUserDay == currentDay){
                return true
            }
        }
        return false
    }

    private fun showWarningDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirmation_title)
            .setMessage(R.string.confirmation_message)
            .setPositiveButton(R.string.confirmation_yes) { _, _ ->
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        parseXmlCollectionFile()
                    }
                }
            }
            .setNegativeButton(R.string.confirmation_no, null)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show()
    }



    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseXmlCollectionFile() {
        saveRequestDataToFile()

        runOnUiThread{
            synchronizedDataButton.visibility = FloatingActionButton.INVISIBLE
        }

        val pullParserFactory: XmlPullParserFactory

        try{
            pullParserFactory = XmlPullParserFactory.newInstance()
            val parser = pullParserFactory.newPullParser()
            val inputStreamGames = applicationContext.openFileInput("gamesData.xml")
            val inputStreamExtensions = applicationContext.openFileInput("extensionsData.xml")
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)

            parser.setInput(inputStreamGames, null)
            parseXml(parser, false)

            parser.setInput(inputStreamExtensions, null)
            parseXml(parser, true)

            reloadRefreshDate()

        } catch (e: XmlPullParserException){
            println(e.printStackTrace())
        } catch (e: IOException){
            println(e.printStackTrace())
        }
    }

    private fun saveRequestDataToFile() {
        val gameUrlRequest = "https://boardgamegeek.com/xmlapi2/collection?username=${user.username}&stats=1&subtype=boardgame"
        val extensionsUrlRequest = "https://boardgamegeek.com/xmlapi2/collection?username=${user.username}&stats=1&subtype=boardgameexpansion"

        val requestDataGame = request.readDataFromRequest(gameUrlRequest)
        val requestDataExtensions = request.readDataFromRequest(extensionsUrlRequest)

        request.saveDataToFile(dataFileGames, requestDataGame)
        request.saveDataToFile(dataFileExtensions, requestDataExtensions)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws (XmlPullParserException::class, IOException::class)
    private fun parseXml(parser: XmlPullParser, extensionXml: Boolean){
        var eventType = parser.eventType
        var game: GameInfo? = null

        while (eventType != XmlPullParser.END_DOCUMENT){
            var name: String
            when (eventType){
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    when (name) {
                        "items" ->
                        {
                            val amountOfObjects = parser.getAttributeValue(null, "totalitems").toInt()
                            runOnUiThread {

                                when (extensionXml){
                                    true -> progressBarTitle.text = this.resources.getString(R.string.progressBarTitleExtension)
                                    false -> progressBarTitle.text = this.resources.getString(R.string.progressBarTitleGames)
                                }

                                synchronizeProgressBar.progress = 0
                                synchronizeProgressBar.max = amountOfObjects
                                progressBarTitle.visibility = ProgressBar.VISIBLE
                                synchronizeProgressBar.visibility = ProgressBar.VISIBLE
                            }
                        }

                        "item" -> {
                            game = GameInfo()
                            game.id = parser.getAttributeValue(null, "objectid")
                            game.extension = extensionXml
                            runOnUiThread {
                                synchronizeProgressBar.incrementProgressBy(1)
                            }


                        } else -> {
                            if (game != null) {
                                when (name) {
                                    "name" -> game.gameName = parser.nextText()
                                    "yearpublished" -> game.yearPublished = parser.nextText()
                                    "thumbnail" -> game.image = parser.nextText()
                                    "rank" -> {
                                        if (parser.getAttributeValue(null, "name") == "boardgame") {
                                            game.currentRank =
                                                parser.getAttributeValue(null, "value")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    name = parser.name
                    if (name.equals("item", ignoreCase = true) && game != null){
                        databaseAccess.addGameToDatabase(game)
                    }
                }
            }
            eventType = parser.next()
        }

        runOnUiThread {
            synchronizeProgressBar.visibility = ProgressBar.INVISIBLE
            progressBarTitle.visibility = ProgressBar.INVISIBLE
        }

    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadRefreshDate() {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = currentDate.format(formatter)
        val (amountOfGames, amountOfExtensions) = databaseAccess.getAmountOfGames()
        user.amountOfGame = amountOfGames
        user.amountOfExtensions = amountOfExtensions
        user.lastSynchronizedDate = formatted
        saveUserData(user)

        runOnUiThread{
            synchronizedDataButton.visibility = FloatingActionButton.VISIBLE
        }
    }

    private fun saveUserData(user: UserSettings) {
        val userPath = this.filesDir.toString().plus("/user.json")
        mapper.writeValue(File(userPath), user)
        readUserAndReloadFields(userPath)
    }
}

