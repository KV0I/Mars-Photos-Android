package com.finastra.kvdtechnical.repository

import com.finastra.kvdtechnical.database.AppDatabase
import com.finastra.kvdtechnical.database.asDomainModel
import com.finastra.kvdtechnical.domain.Photos
import com.finastra.kvdtechnical.network.MarsRoverPhotosApi
import com.finastra.kvdtechnical.network.model.asDatabaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

import javax.inject.Inject

class PhotosRepository @Inject constructor(
    private val photosApi: MarsRoverPhotosApi,
    private val appDatabase: AppDatabase
) {

    val photos: Flow<List<Photos>?> =
        appDatabase.photosDao.getPhotos().map { it?.asDomainModel() }


    suspend fun getLatestPhoto(rover: String = "p") {
        Timber.d("TESTINGG: $photos")
        try {
            val photos = photosApi.getLatestPhotos(rover = rover)
            Timber.d("Photos: $photos")
            appDatabase.photosDao.insertPhotos(photos.asDatabaseModel())

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadMorePhotos(rover: String = "perseverance", page: Int, earthDate: String? = null) {
        try {
            val photos = earthDate?.let {
                photosApi.getPhotos(rover = rover, page = page, earthDate = it)
            } ?: photosApi.getLatestPhotos(rover = rover, page = page)
            Timber.d("Photos: $photos")
//            break if no more photos
            if (photos.photos?.isEmpty() == true && photos.latest_photos?.isEmpty() == true ) return

            appDatabase.photosDao.insertPhotos(photos.asDatabaseModel())

        } catch (e: Exception) {
            Timber.d("ERROR loadMorePhotos: $e")
            e.printStackTrace()
        }
    }

    suspend fun getPhotosByEarthDate (rover: String = "perseverance", page: Int, earthDate: String?) {
        try {
            val photos = photosApi.getPhotos(rover = rover, page = page, earthDate = earthDate)
            Timber.d("Photos: $photos")
            if (photos.photos?.isEmpty() == true && photos.latest_photos?.isEmpty() == true) return
            if (photos.asDatabaseModel().isEmpty()) return
            appDatabase.photosDao.insertPhotos(photos.asDatabaseModel())

        } catch (e: Exception) {
            Timber.d("ERROR getPhotosByEarthDate: $e")
            e.printStackTrace()
        }
    }

    suspend fun selectPhotosByRover(rover: String, page: Int, earthDate: String? = null) {
        try {
            appDatabase.photosDao.deleteAll()
            Timber.d("ROVERRRR")
            var photos = earthDate?.let {
                photosApi.getPhotos(rover = rover, page = page, earthDate = it)
            } ?: photosApi.getLatestPhotos(rover = rover, page = page)
            if ((photos.photos == null || photos.photos?.isEmpty() == true) && (photos.latest_photos == null || photos.latest_photos?.isEmpty() == true)) return
            Timber.d("ADDING Photos: ${photos.photos}")
            Timber.d("is Empty: ${((photos.photos == null || photos.photos?.isEmpty() == true) && (photos.latest_photos == null || photos.latest_photos?.isEmpty() == true))}")

            appDatabase.photosDao.insertPhotos(photos.asDatabaseModel())

        } catch (e: Exception) {
            Timber.d("ERROR selectPhotosByRover: $e")
            e.printStackTrace()
        }
    }

    suspend fun fetchPhotos(rover: String = "perseverance", page: Int, earthDate: String? = null): List<Photos>? {
        try {
            val photos = earthDate?.let {
                photosApi.getPhotos(rover = rover, page = page, earthDate = it)
            } ?: photosApi.getLatestPhotos(rover = rover, page = page)
            Timber.d("Photos: $photos")

            if (photos.photos?.isEmpty() == true && photos.latest_photos?.isEmpty() == true) return null

            return photos.asDatabaseModel().asDomainModel()
        } catch (e: Exception) {
            Timber.d("ERROR fetchPhotos: $e")
            e.printStackTrace()
        }
        return null
    }

    fun deleteAllPhotos() {
//                appDatabase.clearAllTables()
        appDatabase.photosDao.deleteAll()
    }

}
