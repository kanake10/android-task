package com.example.vero.api

import com.example.vero.api.dtos.AuthRequest
import com.example.vero.iteractor.AuthDataStore
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
class TaskApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var taskApiService: TaskApiService
    private lateinit var authDataStore: AuthDataStore
    private val token = "sample_token"

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        authDataStore = mockk<AuthDataStore>()

        val authInterceptor = AuthInterceptor(authDataStore)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        taskApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login() should return AuthResponse and not include Bearer token`() = runTest {

        val loginJson = """
            {
              "oauth": {
                "access_token": "sampletokenabc123",
                "expires_in": 1200,
                "token_type": "Bearer",
                "scope": null,
                "refresh_token": "ref123"
              },
              "userInfo": {
                "personalNo": 123,
                "firstName": "John",
                "lastName": "Doe",
                "displayName": "John Doe",
                "active": true,
                "businessUnit": "IT"
              },
              "permissions": ["read", "write"],
              "apiVersion": "1.0.0",
              "showPasswordPrompt": false
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(loginJson).setResponseCode(200))

        val response = taskApiService.login(authRequest = AuthRequest(username = "365", password = "1"))

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/index.php/login", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
        assertFalse(recordedRequest.getHeader("Authorization")?.contains("Bearer") == true)

        assertEquals("John", response.userInfo.firstName)
        assertEquals("sampletokenabc123", response.oauth.access_token)
    }

    @Test
    fun `getTasks() should return list of tasks and include Bearer token`() = runTest {
        // Mock token from authDataStore
        coEvery { authDataStore.authToken } returns flowOf(token)

        val tasksJson = """
            [
              {
                "task": "100 Aufbau",
                "title": "Gerüst montieren",
                "description": "Gerüste montieren.",
                "sort": "0",
                "wageType": "100 Aufbau",
                "BusinessUnitKey": "Gerüstbau",
                "businessUnit": "Gerüstbau",
                "parentTaskID": "",
                "preplanningBoardQuickSelect": null,
                "colorCode": "",
                "workingTime": null,
                "isAvailableInTimeTrackingKioskMode": false,
                "isAbstract": false
              }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(tasksJson).setResponseCode(200))

        val response = taskApiService.getTasks()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/dev/index.php/v1/tasks/select", recordedRequest.path)
        assertEquals("GET", recordedRequest.method)
        assertTrue(recordedRequest.getHeader("Authorization")?.contains("Bearer $token") == true)

        assertEquals(1, response.size)
        assertEquals("100 Aufbau", response[0].task)
        assertEquals("Gerüst montieren", response[0].title)
    }
}
