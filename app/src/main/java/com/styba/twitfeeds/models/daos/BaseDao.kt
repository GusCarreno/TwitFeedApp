package com.styba.twitfeeds.models.daos

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

@Dao
abstract class BaseDao<in T> {

    @Insert(onConflict = REPLACE)
    abstract fun insert(type: T): Long

    @Update(onConflict = REPLACE)
    abstract fun update(type: T)

    @Delete
    abstract fun delete(type: T)

}