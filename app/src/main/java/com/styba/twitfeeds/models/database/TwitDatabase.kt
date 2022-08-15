package com.styba.twitfeeds.models.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.styba.twitfeeds.models.Source
import com.styba.twitfeeds.models.daos.SourceDao

@Database(entities = [
    (Source::class)
],
        version = 1, exportSchema = false)
abstract class TwitDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
}