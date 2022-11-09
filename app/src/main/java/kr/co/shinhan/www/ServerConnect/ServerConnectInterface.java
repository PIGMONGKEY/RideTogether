package kr.co.shinhan.www.ServerConnect;

import java.util.ArrayList;

import kr.co.shinhan.www.Carpool.CarpoolBoardList.CarpoolBoardListForm;
import kr.co.shinhan.www.Carpool.CarpoolInfo.CarpoolInfoDataForm;
import kr.co.shinhan.www.Carpool.NewCarpool.NewCarpoolDataForm;
import kr.co.shinhan.www.Login.LoginDataForm;
import kr.co.shinhan.www.MyPage.MyPageCarpoolDataForm;
import kr.co.shinhan.www.MyPage.MyPageDataForm;
import kr.co.shinhan.www.MyPage.MyPageTaxiDataForm;
import kr.co.shinhan.www.Register.RegisterDataForm;
import kr.co.shinhan.www.ResetPassword.ResetPasswordDataForm;
import kr.co.shinhan.www.Taxi.TaxiBoardList.TaxiBoardListForm;
import kr.co.shinhan.www.Taxi.NewTaxi.NewTaxiDataForm;
import kr.co.shinhan.www.Taxi.TaxiInfo.TaxiInfoDataForm;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServerConnectInterface {

    //Register
    @FormUrlEncoded
    @POST("RideTogether/Register.php")
    Call<RegisterDataForm> registerRequest_php(
            @Field("pn") String pn,
            @Field("pw") String pw,
            @Field("name") String name,
            @Field("gender") String gender,
            @Field("campus") String campus,
            @Field("smoke") String smoke
    );

    //Login
    @FormUrlEncoded
    @POST("RideTogether/Login.php")
    Call<LoginDataForm> loginRequest_php(
            @Field("pn") String pn,
            @Field("pw") String pw
    );

    //CheckRegisteredPhoneNumber
    @FormUrlEncoded
    @POST("RideTogether/CheckRegisteredPhoneNumber.php")
    Call<ResetPasswordDataForm> checkRegisteredPhoneNumber_php(
            @Field("pn") String pn
    );

    //ResetPassword
    @FormUrlEncoded
    @POST("RideTogether/ResetPassword.php")
    Call<ResetPasswordDataForm> resetPassword(
            @Field("newPW") String newPW,
            @Field("pn") String pn
    );

    //CarpoolBoardList
    @POST("RideTogether/Carpool/CarpoolBoardList.php")
    Call<ArrayList<CarpoolBoardListForm>> getCarpoolBoardList();

    //NewCarpoolRegister
    @FormUrlEncoded
    @POST("RideTogether/Carpool/NewCarpoolRegister.php")
    Call<NewCarpoolDataForm> requestMakeNewCarpool(
            @Field("writer_pn") String wp,
            @Field("write_time") String wt,
            @Field("start_point") String sp,
            @Field("start_point_D") String sp_d,
            @Field("destination") String de,
            @Field("destination_D") String de_d,
            @Field("departure_time") String dt,
            @Field("admit") int admit,
            @Field("gender") String gen,
            @Field("smoke") String smo
    );

    //CarpoolInfo
    @FormUrlEncoded
    @POST("RideTogether/Carpool/CarpoolInfo.php")
    Call<CarpoolInfoDataForm> getCarpoolInfo(
            @Field("no") int no,
            @Field("userPN") String pn
    );

    //JoinCarpool
    @FormUrlEncoded
    @POST("RideTogether/Carpool/JoinCarpool.php")
    Call<CarpoolInfoDataForm> joinCarpool(
            @Field("no") int no,
            @Field("passenger_pn") String ps_pn
    );

    //deleteCarpool
    @FormUrlEncoded
    @POST("RideTogether/Carpool/DeleteCarpool.php")
    Call<CarpoolInfoDataForm> deleteCarpool(
            @Field("no") int no
    );

    //cancelCarpoolJoin
    @FormUrlEncoded
    @POST("RideTogether/Carpool/CancelJoinCarpool.php")
    Call<CarpoolInfoDataForm> cancelCarpoolJoin(
            @Field("no") int no,
            @Field("userPN") String pn
    );

    //ChangePassword
    @FormUrlEncoded
    @POST("RideTogether/ChangePassword.php")
    Call<MyPageDataForm> requestChangePassword(
            @Field("pn") String pn,
            @Field("ps") String ps,
            @Field("newPs") String newPs
    );

    //getCarpoolUsedRecord
    @FormUrlEncoded
    @POST("RideTogether/Carpool/GetCarpoolUsedRecord.php")
    Call<ArrayList<MyPageCarpoolDataForm>> getCarpoolRecord(
            @Field("pn") String pn
    );

    //TaxiBoardList
    @POST("RideTogether/Taxi/TaxiBoardList.php")
    Call<ArrayList<TaxiBoardListForm>> getTaxiBoardList();

    //NewTaxiRegister
    @FormUrlEncoded
    @POST("RideTogether/Taxi/NewTaxiRegister.php")
    Call<NewTaxiDataForm> requestMakeNewTaxi(
            @Field("writer_pn") String wp,
            @Field("write_time") String wt,
            @Field("start_point") String sp,
            @Field("start_point_D") String sp_d,
            @Field("destination") String de,
            @Field("destination_D") String de_d,
            @Field("departure_time") String dt,
            @Field("admit") int admit,
            @Field("gender") String gen,
            @Field("smoke") String smo
    );

    //TaxiInfo
    @FormUrlEncoded
    @POST("RideTogether/Taxi/TaxiInfo.php")
    Call<TaxiInfoDataForm> getTaxiInfo(
            @Field("no") int no,
            @Field("userPN") String pn
    );

    //JoinTaxi
    @FormUrlEncoded
    @POST("RideTogether/Taxi/JoinTaxi.php")
    Call<TaxiInfoDataForm> joinTaxi(
            @Field("no") int no,
            @Field("passenger_pn") String ps_pn
    );

    //deleteTaxi
    @FormUrlEncoded
    @POST("RideTogether/Taxi/DeleteTaxi.php")
    Call<TaxiInfoDataForm> deleteTaxi(
            @Field("no") int no
    );

    //cancelTaxiJoin
    @FormUrlEncoded
    @POST("RideTogether/Taxi/CancelJoinTaxi.php")
    Call<TaxiInfoDataForm> cancelTaxiJoin(
            @Field("no") int no,
            @Field("userPN") String pn
    );

    //getTaxiUsedRecord
    @FormUrlEncoded
    @POST("RideTogether/Taxi/GetTaxiUsedRecord.php")
    Call<ArrayList<MyPageTaxiDataForm>> getTaxiRecord(
            @Field("pn") String pn
    );
}
