package com.example.boardgamecollector


class GameInfo {
    var id : String = ""
    var gameName : String = ""
    var yearPublished : String = ""
    var image : String = ""
    var currentRank : String = "0"

    override fun toString(): String {
        return "GameInfo(id='$id', gameName='$gameName', yearPublished='$yearPublished', image='$image', currentRank='$currentRank')"
    }
}