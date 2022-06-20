package com.example.itracker.model;

public class Sensor {
    private String user_id;
    private String user_activity;
    private String w_acc_x;
    private String w_acc_y;
    private String w_acc_z;
    private String w_bvp;
    private String w_timestamp;
    private  String m_acc_x;
    private  String m_acc_y;
    private  String m_acc_z;
    private  String m_gyro_x;
    private  String m_gyro_y;
    private  String m_gyro_z;


    public Sensor(){}

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setW_acc_x(String w_acc_x) {
        this.w_acc_x = w_acc_x;
    }

    public void setW_acc_y(String w_acc_y) {
        this.w_acc_y = w_acc_y;
    }

    public void setW_acc_z(String w_acc_z) {
        this.w_acc_z = w_acc_z;
    }

    public void setW_bvp(String w_bvp) {
        this.w_bvp = w_bvp;
    }

    public void setW_timestamp(String w_timestamp) {
        this.w_timestamp = w_timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getW_acc_x() {
        return w_acc_x;
    }

    public String getW_acc_y() {
        return w_acc_y;
    }

    public String getW_acc_z() {
        return w_acc_z;
    }

    public String getW_bvp() {
        return w_bvp;
    }

    public String getW_timestamp() {
        return w_timestamp;
    }

    public String getM_acc_x() {
        return m_acc_x;
    }

    public String getM_acc_y() {
        return m_acc_y;
    }

    public String getM_acc_z() {
        return m_acc_z;
    }

    public String getM_gyro_x() {
        return m_gyro_x;
    }

    public String getM_gyro_y() {
        return m_gyro_y;
    }

    public String getM_gyro_z() {
        return m_gyro_z;
    }

    public void setM_acc_x(String m_acc_x) {
        this.m_acc_x = m_acc_x;
    }

    public void setM_acc_y(String m_acc_y) {
        this.m_acc_y = m_acc_y;
    }

    public void setM_acc_z(String m_acc_z) {
        this.m_acc_z = m_acc_z;
    }

    public void setM_gyro_x(String m_gyro_x) {
        this.m_gyro_x = m_gyro_x;
    }

    public void setM_gyro_y(String m_gyro_y) {
        this.m_gyro_y = m_gyro_y;
    }

    public void setM_gyro_z(String m_gyro_z) {
        this.m_gyro_z = m_gyro_z;
    }

    public void setUser_activity(String user_activity) {
        this.user_activity = user_activity;
    }

    public String getUser_activity() {
        return user_activity;
    }
}
