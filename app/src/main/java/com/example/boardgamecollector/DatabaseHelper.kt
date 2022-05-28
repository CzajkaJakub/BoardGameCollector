package com.example.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

        val query = "CREATE TABLE $TABLE_NAME" +
                " ( $COLUMN_ID LONG PRIMARY KEY," +
                " $GAME_TITLE TEXT," +
                " $DATE_OF_RELEASE INTEGER, " +
                " $CURRENT_RANK_POSITION INTEGER," +
                " $IMAGE TEXT);"

        sqLiteDatabase.execSQL(query)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(sqLiteDatabase)
    }

    fun addGameToDatabase(game: GameInfo) {
        val database : SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(GAME_TITLE, game.gameName)
        contentValues.put(DATE_OF_RELEASE, game.yearPublished)
        contentValues.put(COLUMN_ID, game.id)
        contentValues.put(CURRENT_RANK_POSITION, game.currentRank)
        contentValues.put(IMAGE, game.image)

        database.insert(TABLE_NAME, null, contentValues)
    }

    companion object {
        const val DATABASE_NAME = "Board_Games_Database.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "Board_Games"
        const val GAME_TITLE = "Game_Title"
        const val DATE_OF_RELEASE = "Release_Date"
        const val COLUMN_ID = "_id"
        const val CURRENT_RANK_POSITION = "Current_rank_position"
        const val IMAGE = "Img"
    }
}