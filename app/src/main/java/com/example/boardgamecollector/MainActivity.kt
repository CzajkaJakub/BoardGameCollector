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
import java.io.File
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
        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(dataFile))
        xmlDoc.documentElement.normalize()
        val myDatabase = DatabaseHelper(this)

        val amountOfGames : Node = xmlDoc.getElementsByTagName("items").item(0)
        if (amountOfGames.nodeType === Node.ELEMENT_NODE) {
            val elem = amountOfGames as Element
            println(elem.getElementsByTagName("totalitems"))
        }



        val gameList: NodeList = xmlDoc.getElementsByTagName("item")
        for (i in 0 until gameList.length) {
            val game: Node = gameList.item(i)

            if (game.nodeType === Node.ELEMENT_NODE) {
                val elem = game as Element


                try{
                    myDatabase.addDataToSQL(
                        elem.getElementsByTagName("name").item(0).textContent,
                        elem.getElementsByTagName("yearpublished").item(0).textContent.toInt(),
                        234203,
                        elem.getElementsByTagName("image").item(0).textContent,
                        elem.attributes.item(1).nodeValue.toLong()
                    )
                }catch (e: Exception){

                }
            }
        }
    }

    private fun reloadRefreshDate() {

    }
}

