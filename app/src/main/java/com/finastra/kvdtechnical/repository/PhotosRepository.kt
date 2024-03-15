package com.finastra.kvdtechnical.repository;

import com.finastra.kvdtechnical.database.AppDatabase
import com.finastra.kvdtechnical.database.asDomainModel
import com.finastra.kvdtechnical.domain.Photos;
import com.finastra.kvdtechnical.network.MarsRoverPhotosApi;
import com.finastra.kvdtechnical.network.model.Photo
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

    suspend fun refreshPhotos(rover: String = "curiosity") {
        Timber.d("TESTINGG: $photos")
        try {
            appDatabase.photosDao.deleteAll()
            val photos = photosApi.getLatestPhotos(rover = rover)
            Timber.d("Photos: $photos")
            appDatabase.photosDao.insertPhotos(photos.asDatabaseModel())

            appDatabase.photosDao.sortById()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getPhotos(rover: String = "curiosity") {
        Timber.d("TESTINGG: $photos")
        try {
//            filter database by rover
            appDatabase.photosDao.getPhotosByRover(rover).map { it?.asDomainModel() }
            val photos = photosApi.getPhotos(rover = "curiosity", earthDate = "2021-08-01")
            Timber.d("Photos: $photos")
            photos.asDatabaseModel()?.let { appDatabase.photosDao.insertPhotos(it) }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun getLatestPhoto(rover: String = "curiosity") {
        Timber.d("TESTINGG: $photos")
        try {
            val photos = photosApi.getLatestPhotos(rover = rover)
            Timber.d("Photos: $photos")
            appDatabase.photosDao.insertPhotos(photos.asDatabaseModel())

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadMorePhotos(rover: String = "curiosity", page: Int, earthDate: String? = null) {
        try {
            val photos = earthDate?.let {
                photosApi.getPhotos(rover = rover, page = page, earthDate = it)
            } ?: photosApi.getLatestPhotos(rover = rover, page = page)
            Timber.d("Photos: $photos")
//            if latest photos, sort database by date
            if (earthDate == null) appDatabase.photosDao.sortByDate()
//            break if no more photos
            if (photos.photos?.isEmpty() == true && photos.latest_photos?.isEmpty() == true ) return

//            filter database by rover
            Timber.d("Photos: ${photos.asDatabaseModel()}")
            appDatabase.photosDao.insertPhotos(photos.asDatabaseModel())

            // sort by id
            appDatabase.photosDao.sortById()
//            appDatabase.photosDao.getPhotosByRover(rover).map { it?.asDomainModel() }


        } catch (e: Exception) {
            Timber.d("ERROR loadMorePhotos: $e")
            e.printStackTrace()
        }
    }

    suspend fun fetchPhotos(rover: String = "curiosity", page: Int, earthDate: String? = null): List<Photos>? {
        try {
            val photos = earthDate?.let {
                photosApi.getPhotos(rover = rover, page = page, earthDate = it)
            } ?: photosApi.getLatestPhotos(rover = rover, page = page)
            Timber.d("Photos: $photos")

            if (photos.photos?.isEmpty() == true && photos.latest_photos?.isEmpty() == true) return null

            return photos.asDatabaseModel()?.asDomainModel()
        } catch (e: Exception) {
            Timber.d("ERROR fetchPhotos: $e")
            e.printStackTrace()
        }
        return null
    }

            //    delete
            suspend fun deleteAllPhotos() {
                appDatabase.photosDao.deleteAll()
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
