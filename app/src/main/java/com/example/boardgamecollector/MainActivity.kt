package com.example.boardgamecollector

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.util.concurrent.Executors
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {

    private val mapper = jacksonObjectMapper()
    lateinit var user : UserSettings

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUsersSettings()
        val reloadDataButton : FloatingActionButton = findViewById(R.id.reloadDataButton)

        reloadDataButton.setOnClickListener {
            val executor  = Executors.newSingleThreadExecutor()
            executor.execute {
                val dataFile = "$filesDir/data.xml"
                val request = Request()
                val data = request.readRequest("https://boardgamegeek.com/xmlapi2/collection?username=${user.username}")
                request.saveData(dataFile, data)
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(dataFile))
                xmlDoc.documentElement.normalize()

                val gameList: NodeList = xmlDoc.getElementsByTagName("items")
                for(i in 0 until gameList.length){

                    var game: Node = gameList.item(i)

                    if (game.nodeType === Node.ELEMENT_NODE) {

                        val elem = game as Element


                        val mMap = mutableMapOf<String, String>()


                        for(j in 0 until elem.attributes.length)
                        {
                            mMap.putIfAbsent(elem.attributes.item(j).nodeName, elem.attributes.item(j).nodeValue)
                        }
                        println("Current Book : ${game.nodeName} - $mMap")

                        println("name: ${elem.getElementsByTagName("name").item(0).textContent}")
                        println("yearpublished: ${elem.getElementsByTagName("yearpublished").item(0).textContent}")
                        println("image: ${elem.getElementsByTagName("image").item(0).textContent}")
                        println("numplays: ${elem.getElementsByTagName("numplays").item(0).textContent}")

                    }
                }

            }
        }



//        val myDatabase = DatabaseHelper(this)
//            myDatabase.addDataToSQL("gsfdfra", "gfszdfame", 11422332, 234203, "ifasage", 12322)
        }



    private fun checkUsersSettings() {
        val userPath = this.filesDir.toString().plus("/user.json")
        if(File(userPath).exists()){
            setContentView(R.layout.activity_main)
            readUser(userPath)
        } else {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
    }

    private fun readUser(userPath: String) {
        user = mapper.readValue(File(userPath))
        usernameView.text = user.username
    }
}

//    fun loadData(){
//        forex = mutableListOf()
//        kantor = mutableListOf()
//        santander = mutableListOf()
//
//        val filename = "waluty.xml"
//        val path = filesDir
//        val inDir = File(path, "XML")
//
//        if (inDir.exists()){
//            val file = File(inDir, filename)
//            if(file.exists()){
//                val xmlDoc : Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
//                xmlDoc.documentElement.normalize()
//                val items: NodeList = xmlDoc.getElementsByTagName("item")
//
//                for (i in 0 .. items.length-1){
//                    val itemNode: Node = items.item(i)
//                    if (itemNode.nodeType == Node.ELEMENT_NODE){
//                        val elem = itemNode as Element
//                        val children = elem.childNodes
//
//                        var currentCategory : MutableList<Currency>? = null
//                        var currentCode : String? = null
//                        var currentRate : String? = null
//                        var currentDate : String? = null
//
//                        for(j in 0 .. children.length - 1){
//                            val node = children.item(j)
//                            if (node is Element){
//                                when (node.nodeName){
//                                    "category" -> {
//                                        when (node.textContent){
//                                            "Forex" -> currentCategory = forex
//                                            "Alior" -> currentCategory = kantor
//                                            "BZWBK" -> currentCategory = santander
//                                            else -> currentCategory = null
//                                        }
//                                    }
//                                    "title" -> {
//                                        currentCode = node.textContent
//                                    }
//                                    "description" -> {
//                                        currentRate = node.textContent
//                                    }
//                                    "pubDate" -> {
//                                        currentDate = node.textContent
//                                    }
//                                }
//                            }
//                        }
//
//                        if (currentCategory != null && currentCode != null && currentDate != null && currentRate != null){
//                            val rates = currentRate.split(" ")
//                            val r = rates[0].toDouble()
//                            val ch = rates[1].toDouble()
//                            val pattern = "EEE, dd MMM yyyy HH:mm:ss Z"
//                            val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
//                            val d = sdf.parse(currentDate)
//                            val c = Currency(currentCode, r, ch, d)
//                            currentCategory.add(c)
//                        }
//                    }
//                }
//            }
//        }
//    }
