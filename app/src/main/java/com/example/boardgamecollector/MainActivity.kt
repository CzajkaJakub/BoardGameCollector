package com.example.boardgamecollector

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
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
    private lateinit var dataFile : String
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
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                parseXmlCollectionFile()
            }
        }

        clearDataButton.setOnClickListener{
            val userFile = File(this.filesDir.toString().plus("/user.json"))
            val dataFile = File(this.filesDir.toString().plus("/data.xml"))
            userFile.delete()
            dataFile.delete()
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseXmlCollectionFile() {
        dataFile = "$filesDir/data.xml"
        val requestData = request.readRequest("https://boardgamegeek.com/xmlapi2/collection?username=${user.username}&stats=1")
        request.saveData(dataFile, requestData)
        val pullParserFactory: XmlPullParserFactory
        val databaseAccess = DatabaseHelper(this)
        try{
            pullParserFactory = XmlPullParserFactory.newInstance()
            val parser = pullParserFactory.newPullParser()
            val inputStream = applicationContext.openFileInput("data.xml")
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            val games = parseXml(parser)

            for (game in games!!){
                databaseAccess.addGameToDatabase(game)
            }


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
        var amountOfGames = 0
        var amountOfExtensions = 0

        while (eventType != XmlPullParser.END_DOCUMENT){
            var name: String
            when (eventType){
                XmlPullParser.START_DOCUMENT -> games = ArrayList()
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    if (name == "item"){
                        game = GameInfo()
                        game.id = parser.getAttributeValue(null, "objectid")

                    } else if (game != null){
                        when (name) {
                            "name" -> game.gameName = parser.nextText()
                            "yearpublished" -> game.yearPublished = parser.nextText()
                            "thumbnail" -> game.image = parser.nextText()
                            "rank" -> {
                                if(parser.getAttributeValue(null, "name") == "boardgame"){
                                    game.currentRank = parser.getAttributeValue(null, "value")

                                    when (game.currentRank) {
                                        "Not Ranked" -> amountOfExtensions ++
                                        else -> amountOfGames ++
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
        reloadRefreshDate(amountOfGames, amountOfExtensions)
        return games
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadRefreshDate(amountOfGames: Int, amountOfExtensions: Int) {
        user.amountOfGame = amountOfGames
        user.amountOfExtensions = amountOfExtensions
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = currentDate.format(formatter)
        user.lastSynchronizedDate = formatted
        saveUserData(user)
    }

    private fun saveUserData(user: UserSettings) {
        val userPath = this.filesDir.toString().plus("/user.json")
        mapper.writeValue(File(userPath), user)
        readUserAndReloadFields(userPath)
    }
}

