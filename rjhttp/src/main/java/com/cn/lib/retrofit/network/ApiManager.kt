package com.cn.lib.retrofit.network

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiManager {

    @POST
    fun postBody(@Url url: String, @Body mRequestBody: RequestBody): Observable<ResponseBody>

    @POST
    fun postBody(@Url url: String, @Body obj: Any): Observable<ResponseBody>

    @POST
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun potJsonStr(@Url url: String, @Body jsonBody: RequestBody): Observable<ResponseBody>

    @POST
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postJson(@Url url: String, @Body jsonObject: JSONObject): Observable<ResponseBody>

    @POST
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun postJson(@Url url: String, @Body jsonArray: JSONArray): Observable<ResponseBody>

    @POST
    fun post(@Url url: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST
    fun postMap(@Url mUrl: String, @FieldMap(encoded = false) maps: Map<String, String>): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFileWithPartList(@Url mUrl: String, @Part partList: List<MultipartBody.Part>): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFileWithPartMap(@Url mUrl: String, @PartMap partMap: Map<String, MultipartBody.Part>): Observable<ResponseBody>

    @POST
    fun uploadFileWithBody(@Url url: String, @Body Body: RequestBody): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFileWithBodyMap(@Url mUrl: String, @PartMap maps: Map<String, RequestBody>): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFileWithBodyMap(@Url mUrl: String, @Body maps: List<RequestBody>): Observable<ResponseBody>

    @Streaming
    @GET
    fun downloadFile(@Url mUrl: String): Observable<ResponseBody>

    @GET
    fun get(@Url url: String, @QueryMap maps: Map<String, String>?): Observable<ResponseBody>

    @DELETE
    fun delete(@Url url: String, @QueryMap maps: Map<String, String>): Observable<ResponseBody>

    //@DELETE()//delete body请求比较特殊 需要自定义
    @HTTP(method = "DELETE", hasBody = true)/*path = "",*/
    fun deleteBody(@Url url: String, @Body `object`: Any): Observable<ResponseBody>

    //@DELETE()//delete body请求比较特殊 需要自定义
    @HTTP(method = "DELETE", hasBody = true)/*path = "",*/
    fun deleteBody(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>

    //@DELETE()//delete body请求比较特殊 需要自定义
    @Headers("Content-Type: application/json", "Accept: application/json")
    @HTTP(method = "DELETE", hasBody = true)/*path = "",*/
    fun deleteJson(@Url url: String, @Body jsonBody: RequestBody): Observable<ResponseBody>

    @DELETE
    fun deleteUrl(url: String): Observable<ResponseBody>

    @PUT
    fun put(@Url url: String, @QueryMap maps: Map<String, String>): Observable<ResponseBody>

    @PUT
    fun putBody(@Url url: String, @Body `object`: Any): Observable<ResponseBody>

    @PUT
    fun putBody(@Url url: String, @Body body: RequestBody): Observable<ResponseBody>

    @PUT
    @Headers("Content-Type: application/json", "Accept: application/json")
    fun putJson(@Url url: String, @Body jsonBody: RequestBody): Observable<ResponseBody>

    @PUT
    fun putUrl(mUrl: String): Observable<ResponseBody>

}
