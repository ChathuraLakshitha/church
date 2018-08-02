package com.example.zeonit.nclc.response;

public class Member {
    private int memberId;
    private String name;
    private String age;
    private int tel;
    private String address;
    private int isAttendance;
    private boolean isPressent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public int getMemberId() {
        return memberId;
    }
    public int getIsAttendance() {
        return isAttendance;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    public void setisAttendance(int isAttendance) {
        this.setIsAttendance(isAttendance);
    }

    public void setIsAttendance(int isAttendance) {
        this.isAttendance = isAttendance;
    }

    public boolean isPressent() {
        return isPressent;
    }

    public void setPressent(boolean pressent) {
        isPressent = pressent;
    }
}
