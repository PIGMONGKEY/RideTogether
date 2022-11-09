package kr.co.shinhan.www;

import java.io.Serializable;

public class LogOnUserInfo implements Serializable {
    private final String pn, name, gender, campus, smoke;

    public LogOnUserInfo(String pn, String name, String gender, String campus, String smoke) {
        this.pn = pn;
        this.name = name;
        this.gender = gender;
        this.campus = campus;
        this.smoke = smoke;
    }

    public LogOnUserInfo(LogOnUserInfo info) {
        this.pn = info.pn;
        this.name= info.name;
        this.gender = info.gender;
        this.campus = info.campus;
        this.smoke = info.smoke;
    }

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
}
