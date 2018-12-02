package com.vt.disposisibandung.utils;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.vt.disposisibandung.models.DisposisiDefault;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.Token;
import com.vt.disposisibandung.models.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by irvan on 6/24/15.
 */
public class WebServiceHelper implements RequestInterceptor {

//    http://surat.pareparekota.go.id/api/auth

    private static final String API_ENDPOINT = "http://surat.pareparekota.go.id/";
    private static final String APP_KEY = "1eO9nzivSNv8UUhEmDf0IIAZJ94hWjCX";

    private static WebServiceHelper instance;

    private Services services;
    private String deviceId;
    private String accessToken;

    private WebServiceHelper() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getAnnotation(Expose.class) == null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(API_ENDPOINT)
                .setRequestInterceptor(this)
                .setConverter(new GsonConverter(gson))
                // TODO: remove retrofit logs
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        services = adapter.create(Services.class);
    }

    public static WebServiceHelper getInstance() {
        if (instance == null) instance = new WebServiceHelper();
        return instance;
    }

    public void setAccessToken(Token accessToken) {
        if (accessToken == null) {
            this.deviceId = null;
            this.accessToken = null;
        } else {
            this.deviceId = accessToken.getDeviceId();
            this.accessToken = accessToken.getAccessToken();
        }
    }

    public Services getServices() {
        return services;
    }

    @Override
    public void intercept(RequestFacade request) {
        if (accessToken != null) {
            request.addQueryParam("device_id", deviceId);

            long time = System.currentTimeMillis() / 1000l;
            request.addQueryParam("time", String.valueOf(time));

            String key = "";
            try {
                byte[] digest = MessageDigest.getInstance("MD5").digest((APP_KEY + accessToken + time).getBytes());
                key = new BigInteger(1, digest).toString(16);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("@@@", deviceId);
            Log.d("@@@", APP_KEY + " " + accessToken + " " + time);

            if (key.length() < 32) {
                Log.d("@BEFORE", key);
                key = "0" + key;
                Log.d("@AFTER", key);
            }

            request.addQueryParam("key", key);
        }
    }

    public interface Services {

        // Authentication

        @FormUrlEncoded
        @POST("/api/auth")
        void authenticate(@Field("device_id") String deviceId, @Field("username") String username, @Field("password") String password, @Field("device_type") String deviceType, @Field("device_description") String deviceDescription, Callback<Token> callback);

        @POST("/api/user/deauthenticate")
        void deauthenticate(Callback callback);

        @FormUrlEncoded
        @POST("/api/user/change-gcm")
        void updateGcmToken(@Field("gcm_key") String gcmToken, Callback callback);

        @FormUrlEncoded
        @POST("/api/forget-password")
        void forgotPassword(@Field(encodeValue = false, value = "email") String email, Callback callback);

        // General
        @GET("/api/user/profile")
        void getUserProfile(Callback<User> callback);

        @GET("/api/surat/all/{page}")
        void getArsipSurat(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/default-disposisi")
        void getDefaultDisposisi(Callback<RealmList<DisposisiDefault>> callback);

        @GET("/api/surat/search/{page}")
        void searchSurat(@Query("param") String query, @Query("jenis_surat") int type, @Path("page") int page, Callback<RealmList<Surat>> callback);

        @FormUrlEncoded
        @POST("/api/surat/comment/create/{id}")
        void submitKomentar(@Path("id") long id, @Field("comment") String komentar, Callback callback);

        // Surat masuk
        /*@GET("/api/surat/masuk/list-by-jenis/surat_undangan,audiensi,surat_umum/{page}")
        void getListSuratMasuk(@Path("page") int page, Callback<RealmList<Surat>> callback);*/

        @GET("/api/surat/masuk/list/{page}")
        void getListSuratMasuk(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/masuk/list-by-jenis/surat_undangan/{page}")
        void getListSuratUndangan(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/masuk/list-by-jenis/audiensi/{page}")
        void getListSuratAudiensi(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/masuk/list-by-jenis/surat_umum_external,surat_umum_external_tembusan,surat_umum_internal_asli,surat_umum_internal_tembusan,surat_umum_masyarakat/{page}")
        void getListSuratUmum(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/masuk/list-by-status/belum/{page}")
        void getListSuratBelumSelesai(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/masuk/list-by-status/sudah_selesai/{page}")
        void getListSuratSudahSelesai(@Path("page") int page, Callback<RealmList<Surat>> callback);

        /*@GET("/api/surat/masuk/my-list/{page}")
        void getListDisposisiMasuk(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/masuk/my-forwarded-list/{page}")
        void getListDisposisiKeluar(@Path("page") int page, Callback<RealmList<Surat>> callback);*/

        @GET("/api/surat/masuk/my-list/{page}")
        void getListDisposisiMasuk(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/masuk/my-forwarded-list/{page}")
        void getListDisposisiKeluar(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/masuk/detail/{id}/grouped")
        void getDetailSuratMasuk(@Path("id") long id, Callback<Surat> callback);

        @FormUrlEncoded
        @POST("/api/surat/masuk/action-disposisi/{id}")
        void submitDisposisi(@Path("id") long id, @Field("keterangan") String keterangan, @FieldMap Map<Long, Boolean> selectedTargets, Callback callback);

        @POST("/api/surat/set-status/{id_surat}/{status_surat}")
        void submitStatusSurat(@Path("id_surat") long id, @Path("status_surat") String status, Callback callback);

        @FormUrlEncoded
        @POST("/api/surat/masuk/edit-disposisi/{id}")
        void editDisposisi(@Path("id") long id, @Field("keterangan") String keterangan, @FieldMap Map<Long, Boolean> selectedTargets, @Field("tipe_edit") int editType, Callback callback);

        @POST("/api/surat/masuk/add")
        void submitSuratMasuk(@Body Surat surat, Callback<Surat> callback);

        @Multipart
        @POST("/api/surat/masuk/upload/{id}")
        void uploadFileSuratMasuk(@Path("id") long suratId, @Part("file") TypedFile file, retrofit.Callback<String> callback);

        @GET("/api/surat/masuk/jenis")
        void getJenisSuratMasuk(Callback<Map<String, String>> callback);

        @GET("/api/surat/kepada")
        void getTargetList(Callback<HashMap<Long, String>> callback);

        // Surat keluar
        @GET("/api/surat/keluar/list/{page}")
        void getListSuratKeluar(@Path("page") int page, Callback<RealmList<Surat>> callback);

        @GET("/api/surat/keluar/detail/{id}")
        void getDetailSuratKeluar(@Path("id") long id, Callback<Surat> callback);

        @POST("/api/surat/keluar/add")
        void submitSuratKeluar(@Body Surat surat, Callback<Surat> callback);

        @Multipart
        @POST("/api/surat/keluar/upload/{id}")
        void uploadFileSuratKeluar(@Path("id") long suratId, @Part("file") TypedFile file, retrofit.Callback<String> callback);
    }
}
