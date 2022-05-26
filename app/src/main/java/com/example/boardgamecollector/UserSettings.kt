package com.example.boardgamecollector

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserSettings(

    @JsonProperty("username")
    var username: String,

    @JsonProperty("amountOfGame")
    var amountOfGame: Int,

    @JsonProperty("amountOfAdditions")
    var amountOfAdditions: Int,

    @JsonProperty("lastSynchronizedDate")
    var lastSynchronizedDate: String
)
