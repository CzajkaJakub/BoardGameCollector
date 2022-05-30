package com.example.boardgamecollector

import java.io.File
import java.net.URL

class Request {

    fun readDataFromRequest(url: String): String{
        return URL(url).readText()
    }

    fun saveDataToFile(filePath: String, dataToSave: String) {
        val dataFile = File(filePath)
        if(!dataFile.exists()) dataFile.createNewFile()
        dataFile.writeText(dataToSave)
    }
}