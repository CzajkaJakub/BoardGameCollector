package com.example.boardgamecollector


class GameInfo(
    var id: String,
    var gameName: String,
    var yearPublished: String,
    var currentRank: String,
    var extension: Boolean,
    var image: String
) {
    constructor() : this("", "", "", "", false, "")


    override fun toString(): String {
        return "GameInfo(id='$id', gameName='$gameName', yearPublished='$yearPublished', image='$image', currentRank='$currentRank', extension=$extension)"
    }


}