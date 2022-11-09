package kr.co.shinhan.www.Carpool.CarpoolInfo;

import com.google.gson.annotations.SerializedName;

public class CarpoolInfoDataForm {
    @SerializedName("result") private String result;
    @SerializedName("no") private int no;
    @SerializedName("writer_pn") private String w_pn;
    @SerializedName("start_point") private String sp;
    @SerializedName("start_point_D") private String sp_d;
    @SerializedName("destination") private String de;
    @SerializedName("destination_D") private String de_d;
    @SerializedName("departure_time") private String dt;
    @SerializedName("gender") private String gender;
    @SerializedName("smoke") private String smoke;
    @SerializedName("admit") private int admit;
    @SerializedName("cur_admit") private int curAdmit;

    public CarpoolInfoDataForm(CarpoolInfoDataForm dataForm) {
        this.result = dataForm.getResult();
        this.no = dataForm.getNo();
        this.w_pn = dataForm.getW_pn();
        this.sp = dataForm.getSp();
        this.sp_d = dataForm.getSp_d();
        this.de = dataForm.getDe();
        this.de_d = dataForm.getDe_d();
        this.dt = dataForm.getDt();
        this.gender = dataForm.getGender();
        this.smoke = dataForm.getSmoke();
        this.admit = dataForm.getAdmit();
        this.curAdmit = dataForm.getCurAdmit();
    }

    public String getResult() {
        return result;
    }

    public int getNo() {
        return no;
    }

    public String getW_pn() {
        return w_pn;
    }

    public String getSp() {
        return sp;
    }

    public String getSp_d() {
        return sp_d;
    }

    public String getDe() {
        return de;
    }

    public String getDe_d() {
        return de_d;
    }

    public String getDt() {
        return dt;
    }

    public String getGender() {
        return gender;
    }

    public String getSmoke() {
        return smoke;
    }

    public int getAdmit() {
        return admit;
    }

    public int getCurAdmit() {
        return curAdmit;
    }
}
