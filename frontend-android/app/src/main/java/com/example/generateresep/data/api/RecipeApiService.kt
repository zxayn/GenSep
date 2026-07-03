package com.example.generateresep.data.api

import com.example.generateresep.model.CrudResponse
import com.example.generateresep.model.EditCatatanRequest
import com.example.generateresep.model.IngredientDetectionResponse
import com.example.generateresep.model.NoteListResponse
import com.example.generateresep.model.RecipeResponse
import com.example.generateresep.model.RecipeSearchResponse
import com.example.generateresep.model.TambahCatatanRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RecipeApiService {
    @Multipart
    @POST("api/detect-only/")
    suspend fun detectOnly(
        @Part image: MultipartBody.Part,
        @Part("username") username: RequestBody
    ): Response<IngredientDetectionResponse>

    @Multipart
    @POST("api/detect/")
    suspend fun detectAndGenerate(
        @Part image: MultipartBody.Part,
        @Part("username") username: RequestBody
    ): Response<RecipeResponse>

    @GET("api/generate-text/")
    suspend fun generateFromText(
        @Query("q") query: String,
        @Query("username") username: String
    ): Response<RecipeResponse>

    // --- New Cloud Sync Endpoints ---

    @GET("api/riwayat/")
    suspend fun getRiwayat(
        @Query("username") username: String
    ): RecipeSearchResponse

    @DELETE("api/riwayat/hapus/{id}/")
    suspend fun deleteRiwayat(
        @Path("id") id: Int
    ): CrudResponse

    @GET("api/catatan/")
    suspend fun getCatatan(
        @Query("username") username: String
    ): NoteListResponse

    @POST("api/catatan/tambah/")
    suspend fun addCatatan(
        @Body request: TambahCatatanRequest
    ): CrudResponse

    @PUT("api/catatan/edit/{id}/")
    suspend fun editCatatan(
        @Path("id") id: Int,
        @Body request: EditCatatanRequest
    ): CrudResponse

    @DELETE("api/catatan/hapus/{id}/")
    suspend fun deleteCatatan(
        @Path("id") id: Int
    ): CrudResponse
}
