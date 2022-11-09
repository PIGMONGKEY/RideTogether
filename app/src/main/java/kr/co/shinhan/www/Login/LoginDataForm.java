package kr.co.shinhan.www.Login;

import com.google.gson.annotations.SerializedName;

public class LoginDataForm {
    @SerializedName("result") private String result;
    @SerializedName("pn") private String pn;
    @SerializedName("name") private String name;
    @SerializedName("gender") private String gender;
    @SerializedName("campus") private String campus;
    @SerializedName("smoke") private String smoke;

    public String getPn() {
        return pn;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getCampus() {
        return campus;
    }

    public String getSmoke() {
        return smoke;
    }

    public String getResult() {
        return result;
    }
}
