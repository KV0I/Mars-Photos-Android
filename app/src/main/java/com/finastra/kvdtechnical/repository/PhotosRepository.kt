package com.finastra.kvdtechnical.repository;

import com.finastra.kvdtechnical.database.AppDatabase
import com.finastra.kvdtechnical.database.asDomainModel
import com.finastra.kvdtechnical.domain.Photos;
import com.finastra.kvdtechnical.network.MarsRoverPhotosApi;
import com.finastra.kvdtechnical.network.model.asDatabaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

import javax.inject.Inject;

class PhotosRepository @Inject constructor(
    private val photosApi: MarsRoverPhotosApi,
    private val appDatabase: AppDatabase
) {

    val photos: Flow<List<Photos>?> =
        appDatabase.photosDao.getPhotos().map { it?.asDomainModel() }

    suspend fun refreshPhotos() {
        Timber.d("TESTINGG: $photos")
        try {
            appDatabase.photosDao.deleteAll()
            val photos = photosApi.getLatestPhotos(rover = "curiosity")
            Timber.d("Photos: $photos")
            photos.asDatabaseModel()?.let { appDatabase.photosDao.insertPhotos(it) }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getPhotos() {
        Timber.d("TESTINGG: $photos")
        try {
            val photos = photosApi.getPhotos(rover = "curiosity", earthDate = "2021-08-01")
            Timber.d("Photos: $photos")
            photos.asDatabaseModel()?.let { appDatabase.photosDao.insertPhotos(it) }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun getLatestPhoto() {
        Timber.d("TESTINGG: $photos")
        try {
            val photos = photosApi.getLatestPhotos(rover = "curiosity")
            Timber.d("Photos: $photos")
            photos.asDatabaseModel()?.let { appDatabase.photosDao.insertPhotos(it) }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    suspend fun loadMorePhotos(page: Int) {
//        try {
//            val photos = photosApi.getPhotos(page = page)
//            Timber.d("Photos: $photos")
//            photos.asDatabaseModel()?.let { appDatabase.photosDao.insertPhotos(it) }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

}
