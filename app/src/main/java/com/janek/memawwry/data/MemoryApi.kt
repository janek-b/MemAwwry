package com.janek.memawwry.data

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET

interface MemoryApi {
    @GET("woof?filter=mp4,webm,png,gif")
    fun getUrl(): Observable<ResponseBody>
}

class MemoryClient() {
    fun getClient(): MemoryApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://random.dog")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(MemoryApi::class.java)
    }
}