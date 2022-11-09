package kr.co.shinhan.www.MyPage;

import com.google.gson.annotations.SerializedName;

public class MyPageDataForm {
    @SerializedName("result") private String result;

    public String getResult() {
        return result;
    }
}
