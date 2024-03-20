package com.finastra.kvdtechnical.repository

import com.finastra.kvdtechnical.database.AppDatabase
import com.finastra.kvdtechnical.database.asDomainModel
import com.finastra.kvdtechnical.domain.Photos
import com.finastra.kvdtechnical.network.MarsRoverPhotosApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class PhotosRepositoryTest {

    @Mock
    private lateinit var photosApi: MarsRoverPhotosApi

    @Mock
    private lateinit var appDatabase: AppDatabase

    private lateinit var photosRepository: PhotosRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        photosRepository = PhotosRepository(photosApi, appDatabase)
//        add photos to database
        runBlocking {
            photosRepository.getLatestPhoto("perseverance")
        }
    }

//    @Test
//    fun loadMorePhotos() {
//
////        add photos to database
//        runBlocking {
//            photosRepository.loadMorePhotos("perseverance", 1, "2021-06-01")
////            assert that photos is not empty
//            assert(photosRepository.photos.map { it?.isNotEmpty() }.toString().toBoolean())
//        }
//    }


}