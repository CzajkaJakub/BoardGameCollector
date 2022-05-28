package com.example.boardgamecollector

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.Executors
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {

    private val mapper = jacksonObjectMapper()
    lateinit var user : UserSettings
    private lateinit var dataFile : String
    private val request = Request()


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
            readUser(userPath)
            true
        } else {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            false
        }
    }

    private fun readUser(userPath: String) {
        user = mapper.readValue(File(userPath))
        usernameView.text = user.username
        amountOfGamesLabel.text = user.amountOfGame.toString()
        amountOfAdditionsLabel.text = user.amountOfAdditions.toString()
        lastSynchronizedDate.text = user.lastSynchronizedDate
    }

    private fun addListeners() {
        reloadDataButton.setOnClickListener {
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                parseXmlCollectionFile()
                reloadRefreshDate()
            }
        }
    }

    private fun parseXmlCollectionFile() {
        dataFile = "$filesDir/data.xml"
        val requestData = request.readRequest("https://boardgamegeek.com/xmlapi2/collection?username=${user.username}&stats=1")
        request.saveData(dataFile, requestData)

        val pullParserFactory: XmlPullParserFactory

        try{
            pullParserFactory = XmlPullParserFactory.newInstance()
            val parser = pullParserFactory.newPullParser()
            val inputStream = applicationContext.openFileInput("data.xml")
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            val games = parseXml(parser)

            for (game in games!!){
                println(game.toString())
            }


        } catch (e: XmlPullParserException){
            println(e.printStackTrace())
        } catch (e: IOException){
            println(e.printStackTrace())
        }
    }

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
                    println(name)
                    if (name == "item"){
                        game = GameInfo()
                        game.id = parser.getAttributeValue(null, "objectid")
                    } else if (game != null){
                        when (name) {
                            "name" -> game.gameName = parser.nextText()
                            "yearpublished" -> game.yearPublished = parser.nextText()
                            "thumbnail" -> game.image = parser.nextText()
                            "rank" -> {
                                if(parser.getAttributeValue(null, "id") == "1"){
                                    game.currentRank = parser.getAttributeValue(null, "value")
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

    private fun reloadRefreshDate() {

    }
}

