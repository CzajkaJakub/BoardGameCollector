package com.example.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase

class DatabaseHelper(val context: Context?) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

        val query = "CREATE TABLE $TABLE_NAME" +
                " ( $COLUMN_ID LONG PRIMARY KEY," +
                " $GAME_TITLE TEXT," +
                " $ORIGINAL_GAME_TITLE TEXT, " +
                " $DATE_OF_RELEASE INTEGER, " +
                " $CURRENT_RANK_POSITION INTEGER," +
                " $IMAGE TEXT);"

        sqLiteDatabase.execSQL(query)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(sqLiteDatabase)
    }

    fun addDataToSQL(gameTitle : String, originalGameTitle : String, dateOfRelease : Int, currentRankPosition : Int, image : String, gameId : Long){
        val database : SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(GAME_TITLE, gameTitle)
        contentValues.put(ORIGINAL_GAME_TITLE, originalGameTitle)
        contentValues.put(DATE_OF_RELEASE, dateOfRelease)
        contentValues.put(COLUMN_ID, gameId)
        contentValues.put(CURRENT_RANK_POSITION, currentRankPosition)
        contentValues.put(IMAGE, image)

        database.insert(TABLE_NAME, null, contentValues)
    }

    companion object {
        const val DATABASE_NAME = "BoardGamesDatabase.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "BoardGames"
        const val GAME_TITLE = "game_title"
        const val ORIGINAL_GAME_TITLE = "Original_Game_Title"
        const val DATE_OF_RELEASE = "Release_Date"
        const val COLUMN_ID = "_id"
        const val CURRENT_RANK_POSITION = "Current_rank_position"
        const val IMAGE = "Img"
    }
}