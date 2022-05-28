package com.example.boardgamecollector


class GameInfo {
    lateinit var id : String
    lateinit var gameName : String
    var yearPublished : String = ""
    lateinit var image : String
    var currentRank : String = "0"

    override fun toString(): String {
        return "GameInfo(id='$id', gameName='$gameName', yearPublished='$yearPublished', image='$image', currentRank='$currentRank')"
    }
}