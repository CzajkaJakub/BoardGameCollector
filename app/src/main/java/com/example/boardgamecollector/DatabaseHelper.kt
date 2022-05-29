package com.example.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

        val queryGames = "CREATE TABLE $TABLE_NAME_GAMES" +
                " ( $COLUMN_ID LONG PRIMARY KEY," +
                " $GAME_TITLE TEXT," +
                " $DATE_OF_RELEASE INTEGER, " +
                " $CURRENT_RANK_POSITION INTEGER," +
                " $EXTENSION BOOLEAN," +
                " $IMAGE TEXT);"


        sqLiteDatabase.execSQL(queryGames)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_GAMES")
        onCreate(sqLiteDatabase)
    }

    fun clearDatabase() {
        val database : SQLiteDatabase = this.writableDatabase
        database.execSQL("DELETE FROM $TABLE_NAME_GAMES")
    }

    fun addGameToDatabase(game: GameInfo) {
        val database : SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(GAME_TITLE, game.gameName)
        contentValues.put(DATE_OF_RELEASE, game.yearPublished)
        contentValues.put(COLUMN_ID, game.id)
        contentValues.put(CURRENT_RANK_POSITION, game.currentRank)
        contentValues.put(IMAGE, game.image)
        contentValues.put(EXTENSION, game.extension)

        val id =  database.insertWithOnConflict(TABLE_NAME_GAMES, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE).toInt()
        if (id == -1) {
            database.update(TABLE_NAME_GAMES, contentValues, "_id=?", arrayOf(game.id))
        }
        database.close()
    }

    fun getDataGamesExtensions(getGamesByNameAsc: QueriesTypes): ArrayList<GameInfo> {
        val data = ArrayList<GameInfo>()
        val database : SQLiteDatabase = this.readableDatabase
        val query : String = when (getGamesByNameAsc){
            QueriesTypes.GET_GAMES_BY_NAME_ASC -> "select * from Board_Games where Extension = 0;"
            QueriesTypes.GET_EXTENSIONS_BY_NAME_ASC -> "select * from Board_Games where Extension = 1;"
        }

        val cursorData = database.rawQuery(query, null)

        if (cursorData.moveToFirst()) {
            do {
                data.add(
                    GameInfo(
                        cursorData.getString(0),
                        cursorData.getString(1),
                        cursorData.getString(2),
                        cursorData.getString(3),
                        cursorData.getString(4) == "1",
                        cursorData.getString(5)
                    )
                )
            } while (cursorData.moveToNext())
        }
        cursorData.close()
        return data
    }

    companion object {
        const val DATABASE_NAME = "Board_Games_Database.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME_GAMES = "Board_Games"
        const val GAME_TITLE = "Game_Title"
        const val DATE_OF_RELEASE = "Release_Date"
        const val COLUMN_ID = "_id"
        const val CURRENT_RANK_POSITION = "Current_rank_position"
        const val IMAGE = "Img"
        const val EXTENSION = "Extension"
    }
}