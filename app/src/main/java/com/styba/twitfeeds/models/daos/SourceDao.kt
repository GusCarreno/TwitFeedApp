package com.styba.twitfeeds.models.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.styba.twitfeeds.models.Source
import io.reactivex.Flowable
import io.reactivex.Single
import android.arch.persistence.room.Update

@Dao
abstract class SourceDao: BaseDao<Source>() {

    @Query("SELECT count(*) FROM Source WHERE locationId = :locationId ")
    abstract fun getCount(locationId: Int?): Long

    @Query("SELECT * FROM Source WHERE locationId = :locationId ORDER BY id ASC")
    abstract fun getAll(locationId: Int?): Flowable<List<Source>>

    @Query("SELECT * FROM Source WHERE enabled = 1 AND locationId = :locationId")
    abstract fun getSourceSelected(locationId: Int?): Flowable<List<Source>>

    @Query("SELECT * FROM Source WHERE id = :id")
    abstract fun findById(id: Long): Single<Source>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<Source>)

    @Update
    abstract fun updateSource(source: Source)

    @Query("DELETE FROM Source WHERE locationId = :locationId")
    abstract fun deleteAll(locationId: Int?)

}