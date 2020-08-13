package com.leon.estimate.Infrastructure;

import com.leon.estimate.Tables.CalculationInfo;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.CalculationUserInputSend;
import com.leon.estimate.Tables.ImageDataThumbnail;
import com.leon.estimate.Tables.ImageDataTitle;
import com.leon.estimate.Tables.Input;
import com.leon.estimate.Tables.Login;
import com.leon.estimate.Tables.LoginFeedBack;
import com.leon.estimate.Tables.LoginInfo;
import com.leon.estimate.Tables.UploadImage;
import com.leon.estimate.Tables.Uri;
import com.leon.estimate.Utils.SimpleMessage;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Leon on 12/9/2017.
 */
public interface IAbfaService {

//    @GET("/Api1/Apk/GetNewest")
//    Call<ResponseBody> getNewestAppVersion(
//            @Query("CurrentVersion") int Version);
//
//    @PATCH("Auth/UserManager/ChangePassword")
//    Call<SimpleMessage> changePassword(
//            @Body ChangePassword changePassword
//    );
//
//
//    @GET("/Api1/Apk/GetNewestInfo")
//    Call<AppInfo> getLastApkInfo();
//
//    @GET("/Api1/Loading/Load")
//    Call<MobileInput> downloadOff(
//            @Query("currentVersion") int currentVersion
//    );
//
//    @GET("/Api1/Loading/Load")
//    Call<MobileInput> download(
//            @Query("currentVersion") int currentVersion
//    );
//
//    @GET("/Api1/Loading/Load")
//    Call<MobileInput> downloadSpecial(
//            @Query("currentVersion") int currentVersion
//    );
//
//    @GET("/Api1/Loading/Reload")
//    Call<MobileInput> downloadRetry(
//            @Query("trackNumbers[]") List<Integer> trackNumbers
//    );
//
//    @POST("/Api1/QeireMojaz/Register")
//    Call<SimpleMessage> uploadForbid(
//            @Header("Authorization") String token,
//            @Query("preEshterak") String preEshterak,
//            @Query("nextEshterak") String nextEshterak,
//            @Query("expandableTextView") String address,
//            @Query("tedadVahed") int tedadVahed,
//            @Query("latitude") double latitude,
//            @Query("longitude") double longitude,
//            @Query("trackNumber") int trackNumber
//    );
//
//    @Multipart
//    @POST("/Api1/QeireMojaz/Register")
//    Call<SimpleMessage> uploadForbid(
//            @Header("Authorization") String token,
//            @Query("preEshterak") String preEshterak,
//            @Query("nextEshterak") String nextEshterak,
//            @Query("expandableTextView") String address,
//            @Query("tedadVahed") int tedadVahed,
//            @Query("latitude") double latitude,
//            @Query("longitude") double longitude,
//            @Query("trackNumber") int trackNumber,
//            @Part MultipartBody.Part file
//    );
//
//    @Multipart
//    @POST("/Api1/Offloading/RegisterDescription")
//    Call<SimpleMessage> uploadDescription(
//            @Query("trackNumber") int trackNumber,
//            @Query("billId") String billId,
//            @Query("description") String description,
//            @Part MultipartBody.Part file
//    );
//
//    @Multipart
//    @POST("/Api1/Offloading/RegisterDescription")
//    Call<SimpleMessage> uploadDescription(
//            @Query("trackNumber") int trackNumber,
//            @Query("billId") String billId,
//            @Part MultipartBody.Part file
//    );
//
//    @POST("/Api1/Offloading/RegisterDescription")
//    Call<SimpleMessage> uploadDescription(
//            @Query("trackNumber") int trackNumber,
//            @Query("billId") String billId,
//            @Query("description") String description
//    );
//
//    @Multipart
//    @POST("/Api1/Offloading/UploadImage")
//    Call<SimpleMessage> uploadImage(
//            @Query("trackNumber") int trackNumber,
//            @Query("billId") String billId,
//            @Part MultipartBody.Part file
//    );
//
//    @PATCH("/Api1/Offloading/Offload/")
//    Call<ArrayList<UploadReadFeedback>> uploadRead(
//            @Body UploadReadData uploadReadData
//
//    );
//
//    @PATCH("/Api1/Offloading/SetCounterPosition")
//    Call<SimpleMessage> counterPosition(
//            @Body Location location);
//
//    @PATCH("/Api1/Offloading/OffloadEmptyBody")
//    Call<SimpleMessage> uploadEmpty(
//            @Query("trackNumber") String trackNumber
//    );
//
//    @PUT("/Api1/ToziGhabsManager/Add")
//    Call<SimpleMessage> toziGhabs(@Body LocationUpdateModel locationUpdateModel);

    @POST("/Auth/Account/login")
    Call<LoginFeedBack> login(@Body LoginInfo logininfo);

    @POST("MoshtarakinApi/SepanoDMS/V1/Auth/Login/{userName}/{password}")
    Call<LoginFeedBack> login1(
            @Path("userName") String username,
            @Path("password") String password);

    @POST("/MoshtarakinApi/SepanoDMS/V1/Login/{username}/{password}")
    Call<Login> login2(
            @Path("username") String username,
            @Path("password") String password
    );

//    @POST("/MoshtarakinApi/SepanoDMS/V1/GetDoc/PHPSESSID=k8a8m5q2gh2k96mhfs6qtcnnv3; remember_me=5660bce40fb96587ad34e559a7383933")
//    Call<ResponseBody> GetDoc(@Body Uri uri);

    @POST("/MoshtarakinApi/SepanoDMS/V1/GetDoc/{token}")
    Call<ResponseBody> getDoc(
            @Path("token") String token, @Body Uri uri
    );

    @GET("/MoshtarakinApi/SepanoDms/V1/GetTitles/{token}")
    Call<ImageDataTitle> getTitle(
            @Path("token") String token
    );

//    @GET("/MoshtarakinApi/SepanoDMS/V1/GetDocsListThumbnail/{billIdOrTrackNumber}/{token}")
@GET("/MoshtarakinApi/SepanoDMS/V1/GetDocsListHighQuality/{billIdOrTrackNumber}/{token}")
Call<ImageDataThumbnail> getDocsListThumbnail(
        @Path("token") String token,
        @Path("billIdOrTrackNumber") String billIdOrTrackNumber
);

    @GET("/MoshtarakinApi/SepanoDMS/V1/GetDocsListHighQuality/{billIdOrTrackNumber}/{token}")
    Call<ArrayList<String>> getDocsListHighQuality(
            @Path("token") String token,
            @Path("billIdOrTrackNumber") String billIdOrTrackNumber
    );

    @Multipart
    @POST("/MoshtarakinApi/SepanoDMS/V1/Upload/{token}")
    Call<UploadImage> uploadDoc(
            @Path("token") String token,
            @Part MultipartBody.Part imageFile,
            @Part("docId") int docId,
            @Part("billId") String billId
    );

    @Multipart
    @POST("/MoshtarakinApi/SepanoDMS/V1/Upload/{token}")
    Call<UploadImage> uploadDocNew(
            @Path("token") String token,
            @Part MultipartBody.Part imageFile,
            @Part("docId") int docId,
            @Part("trackingNumber") String trackingNumber
    );

    @PATCH("/Auth/Account/UpdateDeviceId")
    Call<SimpleMessage> signSerial(
            @Query("deviceId") String deviceId);

    @PATCH("/Auth/Account/UpdateDeviceIdAnanymous")
    Call<SimpleMessage> signSerial(
            @Body LoginInfo logininfo);

    @GET("/MoshtarakinApi/ExaminationManager/GetMyWorks")
    Call<Input> getMyWorks();

    @GET("/MoshtarakinApi/ExaminationManager/GetExaminationDetails/")
    Call<CalculationInfo> getMyWorksDetails(@Query("trackNumber") String trackNumber);


    @POST("/MoshtarakinApi/ExaminationManager/SetExaminationInfo")
    Call<SimpleMessage> setExaminationInfo(
            @Body ArrayList<CalculationUserInputSend> calculationUserInputSend);

    @POST("/MoshtarakinApi/ExaminationManager/SetExaminationInfo")
    Call<SimpleMessage> SetExaminationInfo(
            @Body CalculationUserInput calculationUserInput);

}

