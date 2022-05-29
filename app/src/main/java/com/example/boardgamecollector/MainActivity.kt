package com.example.boardgamecollector

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
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
            if (user.lastSynchronizedDate != "Synchronize to get data!" &&
                LocalDateTime.parse(
                    user.lastSynchronizedDate,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                ).dayOfYear == LocalDateTime.now().dayOfYear
            ) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.confirmation_title)
                    .setMessage(R.string.confirmation_message)
                    .setPositiveButton(R.string.confirmation_yes) { dialog, which ->
                        val executor = Executors.newSingleThreadExecutor()
                        executor.execute {
                            parseXmlCollectionFile()
                        }
                    }
                    .setNegativeButton(R.string.confirmation_no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            } else {
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    parseXmlCollectionFile()
                }
            }
        }

        clearDataButton.setOnClickListener{
            val filesToClear = listOf("/user.json", "/gamesData.xml", "/extensionsData.xml")
            filesToClear.stream().forEach { File(this.filesDir.toString().plus(it)).delete() }
            val database = DatabaseHelper(this)
            database.clearDatabase()
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }


        showDataTable.setOnClickListener{
            val intent = Intent(this, GamesExtensionsTable::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseXmlCollectionFile() {
        dataFileGames = "$filesDir/gamesData.xml"
        dataFileExtensions = "$filesDir/extensionsData.xml"

        val requestDataGame = request.readRequest("https://boardgamegeek.com/xmlapi2/collection?username=${user.username}&stats=1&subtype=boardgame")
        val requestDataExtensions = request.readRequest("https://boardgamegeek.com/xmlapi2/collection?username=${user.username}&stats=1&subtype=boardgameexpansion")

        request.saveData(dataFileGames, requestDataGame)
        request.saveData(dataFileExtensions, requestDataExtensions)

        val pullParserFactory: XmlPullParserFactory
        val databaseAccess = DatabaseHelper(this)
        try{
            pullParserFactory = XmlPullParserFactory.newInstance()
            val parser = pullParserFactory.newPullParser()
            val inputStreamGames = applicationContext.openFileInput("gamesData.xml")
            val inputStreamExtensions = applicationContext.openFileInput("extensionsData.xml")
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)

            parser.setInput(inputStreamGames, null)
            val listOfGames = parseXml(parser)

            parser.setInput(inputStreamExtensions, null)
            val listOfExtensions = parseXml(parser)

            listOfGames!!.stream().forEach { databaseAccess.addGameToDatabase(it)}
            listOfExtensions!!.stream().forEach { it.extension = true; databaseAccess.addGameToDatabase(it) }

            reloadRefreshDate(listOfGames.size, listOfExtensions.size)

        } catch (e: XmlPullParserException){
            println(e.printStackTrace())
        } catch (e: IOException){
            println(e.printStackTrace())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws (XmlPullParserException::class, IOException::class)
    private fun parseXml(parser: XmlPullParser): ArrayList<GameInfo>? {
        var games: ArrayList<GameInfo>? = null
        var eventType = parser.eventType
        var game: GameInfo? = null

        while (eventType != XmlPullParser.END_DOCUMENT){
            var name: String
            when (eventType){
                XmlPullParser.START_DOCUMENT -> games = ArrayList()
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    when (name) {
                        "item" -> {
                            game = GameInfo()
                            game.id = parser.getAttributeValue(null, "objectid")

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
                        games!!.add(game)
                    }
                }
            }
            eventType = parser.next()
        }
        return games
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadRefreshDate(amountOfGames: Int, amountOfExtensions: Int) {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = currentDate.format(formatter)
        user.amountOfGame = amountOfGames - amountOfExtensions
        user.amountOfExtensions = amountOfExtensions
        user.lastSynchronizedDate = formatted
        saveUserData(user)
    }

    private fun saveUserData(user: UserSettings) {
        val userPath = this.filesDir.toString().plus("/user.json")
        mapper.writeValue(File(userPath), user)
        readUserAndReloadFields(userPath)
    }
}

