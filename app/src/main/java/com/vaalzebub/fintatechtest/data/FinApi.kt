package com.vaalzebub.fintatechtest.data

import com.vaalzebub.fintatechtest.BuildConfig
import com.vaalzebub.fintatechtest.data.source.AuthData
import com.vaalzebub.fintatechtest.data.source.DataWrapper
import com.vaalzebub.fintatechtest.data.source.HistoryPriceDto
import com.vaalzebub.fintatechtest.data.source.ItemDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private object AuthKeys{
    const val USER = "username"
    const val PASS = "password"
    const val CLIENT_ID = "client_id"
    const val GRANT_TYPE = "grant_type"

}
interface FinApi {
    @FormUrlEncoded
    @POST("/identity/realms/fintatech/protocol/openid-connect/token")
    suspend fun getToken(
        @Field(AuthKeys.USER) user:String = BuildConfig.API_USER,
        @Field(AuthKeys.PASS) pass:String = BuildConfig.API_PASS,
        @Field(AuthKeys.CLIENT_ID) clientId:String = "app-cli",
        @Field(AuthKeys.GRANT_TYPE) type:String = "password",
    ):Response<AuthData>

    @GET("/api/instruments/v1/instruments")
    suspend fun getInstruments(
        @Query("provider") provider: String = "oanda",
        @Query("kind") kind: String = "forex",
    ): Response<DataWrapper<ItemDto>>

    @GET("/api/bars/v1/bars/date-range")
    suspend fun getPrices(
        @Query("instrumentId") instrumentId: String,
        @Query("provider") provider: String = "oanda",
        @Query("interval") interval: String = "30",
        @Query("periodicity") periodicity: String = "minute",
        @Query("startDate") startDate: String = "2024-10-29",
    ): Response<DataWrapper<HistoryPriceDto>>
}