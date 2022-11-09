package kr.co.shinhan.www.Carpool.CarpoolBoardList;

import com.google.gson.annotations.SerializedName;

public class CarpoolBoardListForm {
    @SerializedName("result") private String result;
    @SerializedName("no") private int no;
    @SerializedName("start_point") private String sp;
    @SerializedName("destination") private String de;
    @SerializedName("admit") private int admit;
    @SerializedName("cur_admit") private int curAdmit;

    public String getResult() { return result; }

    public int getNo() {
        return no;
    }

    public String getSp() {
        return sp;
    }

    public String getDe() {
        return de;
    }

    public int getAdmit() {
        return admit;
    }

    public int getCurAdmit() {
        return curAdmit;
    }
}
