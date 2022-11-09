package kr.co.shinhan.www.Taxi.NewTaxi;

import com.google.gson.annotations.SerializedName;

public class NewTaxiDataForm {
    @SerializedName("result") private String result;

    public String getResult() {
        return result;
    }
}
