package com.finastra.kvdtechnical.network

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.finastra.kvdtechnical.database.AppDatabase
import com.finastra.kvdtechnical.network.model.asDatabaseModel
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class ApiIsolationTest: TestCase() {

    @Test
    fun test_photos_Mars_Rover_Result_Success() {
        val api = TestApiImplementation.provideApi()
        val test = runBlocking {
            api.getMarsRoverPhotos("perseverance")
        }

        assertEquals(test.isSuccessful, true)
    }

}