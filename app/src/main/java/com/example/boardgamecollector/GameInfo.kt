package com.example.boardgamecollector


class GameInfo {
    var id : String = ""
    var gameName : String = "No Data"
    var yearPublished : String = "No Data"
    var image : String = "No Data"
    var currentRank : String = "No Data"

    override fun toString(): String {
        return "GameInfo(id='$id', gameName='$gameName', yearPublished='$yearPublished', image='$image', currentRank='$currentRank')"
    }
}