package com.example.farmmobileapp.core.data.storage

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FileUploadService @Inject constructor(
    @Named("SignedUrlUploadClient") private val httpClient: HttpClient // Inject the minimal client
) {

    /**
     * Uploads file bytes directly to a pre-signed URL (typically using PUT).
     *
     * @param signedUrl The pre-signed URL obtained from the backend.
     * @param fileBytes The raw ByteArray of the file.
     * @param contentType The MIME type of the file (e.g., "image/jpeg").
     * @return A Result indicating success (Unit) or failure (Exception).
     */
    suspend fun uploadToSignedUrl(
        signedUrl: String,
        fileBytes: ByteArray,
        contentType: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Supabase signed URLs typically expect PUT
            val response: HttpResponse = httpClient.put(signedUrl) {
                setBody(fileBytes)
                contentType(ContentType.parse(contentType))
                // Headers like Content-Length are usually handled automatically by Ktor/engine
            }

            if (response.status.isSuccess()) {
                Result.success(Unit) // Indicate success
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(
                    Exception("Upload to signed URL failed: ${response.status}. Body: $errorBody")
                )
            }
        } catch (e: Exception) {
            Result.failure(Exception("Upload failed: ${e.message}", e))
        }
    }
}