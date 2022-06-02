package com.example.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

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


        val queryGamesHistory = "CREATE TABLE $TABLE_GAMES_HISTORY" +
                " ( $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $GAME_ID LONG," +
                " $RANK_COLUMN INTEGER, " +
                " $DATE_RANK TEXT);"


        sqLiteDatabase.execSQL(queryGames)
        sqLiteDatabase.execSQL(queryGamesHistory)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_GAMES")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES_HISTORY")
        onCreate(sqLiteDatabase)
    }

    fun truncateDatabase() {
        val database : SQLiteDatabase = this.writableDatabase
        database.execSQL("DELETE FROM $TABLE_NAME_GAMES")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addGameToDatabase(game: GameInfo) {
        val database : SQLiteDatabase = this.writableDatabase
        val contentValuesGame = getContentValueGames(game)

        val id =  database.insertWithOnConflict(TABLE_NAME_GAMES, null, contentValuesGame, SQLiteDatabase.CONFLICT_IGNORE).toInt()
        if (id == -1) {
            database.update(TABLE_NAME_GAMES, contentValuesGame, "_id=?", arrayOf(game.id))
        }

        if (game.extension){
            val contentValuesHistory = getContentValueGamesHistory(game)
            database.insert(TABLE_GAMES_HISTORY, null, contentValuesHistory)
        }
    }

    private fun getContentValueGames(game: GameInfo): ContentValues {
        val contentValuesGame = ContentValues()
        contentValuesGame.put(GAME_TITLE, game.gameName)
        contentValuesGame.put(DATE_OF_RELEASE, game.yearPublished)
        contentValuesGame.put(COLUMN_ID, game.id)
        contentValuesGame.put(CURRENT_RANK_POSITION, game.currentRank)
        contentValuesGame.put(IMAGE, game.image)
        contentValuesGame.put(EXTENSION, game.extension)
        return contentValuesGame
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getContentValueGamesHistory(game: GameInfo): ContentValues {
        val contentValuesHistory = ContentValues()
        contentValuesHistory.put(GAME_ID, game.id)
        contentValuesHistory.put(RANK_COLUMN, game.currentRank)
        contentValuesHistory.put(DATE_RANK, LocalDate.now().toString())
        return contentValuesHistory
    }

    fun getDataGamesExtensions(getGamesByNameAsc: QueriesTypes): ArrayList<GameInfo> {

        val data = ArrayList<GameInfo>()
        val database : SQLiteDatabase = this.readableDatabase

        val query : String = when (getGamesByNameAsc){
            QueriesTypes.GET_GAMES_BY_NAME_ASC -> "select * from Board_Games where Extension = 0;"
            QueriesTypes.GET_EXTENSIONS_BY_NAME_ASC -> "select * from Board_Games where Extension = 1;"
            QueriesTypes.GET_ALL_BY_NAME_ASC -> "select * from Board_Games"
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
                        cursorData.getString(4).equals("1"),
                        cursorData.getString(5)
                    )
                )
            } while (cursorData.moveToNext())
        }
        cursorData.close()
        return data
    }

    fun getGameHistory(game_id: String): ArrayList<GameHistoryData>{
        val data = ArrayList<GameHistoryData>()
        val database : SQLiteDatabase = this.readableDatabase

        val query = "select * from $TABLE_GAMES_HISTORY where $GAME_ID = $game_id"
        val cursorData = database.rawQuery(query, null)

        if (cursorData.moveToFirst()) {
            do {
                data.add(
                    GameHistoryData(
                        cursorData.getInt(0),
                        cursorData.getString(1),
                        cursorData.getString(2),
                        cursorData.getString(3)
                    )
                )
            } while (cursorData.moveToNext())
        }
        cursorData.close()
        return data

    }

    fun getAmountOfGames(): Pair<Int, Int> {
        val database : SQLiteDatabase = this.readableDatabase
        val cursorData = database.rawQuery("select count(*) from Board_Games group by Extension;", null)
        cursorData.moveToFirst()
        val amountOfGames = cursorData.getInt(0)
        cursorData.moveToNext()
        val amountOfExtensions = cursorData.getInt(0)
        cursorData.close()
        return Pair(amountOfGames, amountOfExtensions)
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

        const val TABLE_GAMES_HISTORY = "History_ranks"
        const val RANK_COLUMN = "Rank"
        const val DATE_RANK = "Date"
        const val GAME_ID = "Game_id"

    }
}