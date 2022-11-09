package kr.co.shinhan.www.MyPage;

import com.google.gson.annotations.SerializedName;

public class MyPageTaxiDataForm {
    @SerializedName("result") private String result;
    @SerializedName("no") private int no;
    @SerializedName("departure_time") private String dt;
    @SerializedName("start_point") private String sp;
    @SerializedName("destination") private String de;

    public String getResult() {
        return result;
    }

    public int getNo() {
        return no;
    }

    public String getDt() {
        return dt;
    }

    public String getSp() {
        return sp;
    }

    public String getDe() {
        return de;
    }
}
