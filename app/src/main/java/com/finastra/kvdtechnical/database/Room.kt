package com.finastra.kvdtechnical.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotosDao {
    @Query("select * from PhotosEntity")
    fun getPhotos(): Flow<List<PhotosEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotos(photos: List<PhotosEntity>)

//    get latest
    @Query("select * from PhotosEntity order by earth_date desc limit 1")
    fun getLatestPhoto(): Flow<PhotosEntity?>

//    delete all
    @Query("delete from PhotosEntity")
    fun deleteAll()

//    get photos by rover
    @Query("select * from PhotosEntity where roverName = :rover")
    fun getPhotosByRover(rover: String): Flow<List<PhotosEntity>?>

//    sortByDate
    @Query("select * from PhotosEntity order by earth_date asc")
    fun sortByDate(): Flow<List<PhotosEntity>?>

//    sortById
    @Query("select * from PhotosEntity order by id asc")
    fun sortById(): Flow<List<PhotosEntity>?>

}

@Database(entities = [PhotosEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val photosDao: PhotosDao
}