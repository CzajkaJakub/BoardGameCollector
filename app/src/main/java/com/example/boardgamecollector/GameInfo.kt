package com.example.boardgamecollector


class GameInfo(
    var id: String,
    var gameName: String,
    var yearPublished: String,
    var currentRank: String,
    var extension: Boolean,
    var image: String
) {
    constructor() : this("", "", "No Data", "No Data", false, "No Data")


    override fun toString(): String {
        return "GameInfo(id='$id', gameName='$gameName', yearPublished='$yearPublished', image='$image', currentRank='$currentRank', extension=$extension)"
    }

    fun getIntYear(): Int{
        return try {
            yearPublished.toInt()
        } catch (e: Exception){
            0
        }
    }

    fun getIntRank(): Int{
        return try {
            currentRank.toInt()
        } catch (e: Exception){
            0
        }
    }
}