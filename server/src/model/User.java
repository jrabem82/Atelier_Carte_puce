package model;

public class User {
    private String idUser;
    private long x_hmac;
    private long y_hmac;
    private String hmac_x_y_pw;
    private String histoR;
    private String histoG;
    private String histoB;
    private int NBAuthEchouees;

    public User() {}

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getX_hmac() {
        return x_hmac;
    }

    public void setX_hmac(long x_hmac) {
        this.x_hmac = x_hmac;
    }

    public long getY_hmac() {
        return y_hmac;
    }

    public void setY_hmac(long y_hmac) {
        this.y_hmac = y_hmac;
    }

    public String getHmac_x_y_pw() {
        return hmac_x_y_pw;
    }

    public void setHmac_x_y_pw(String hmac_x_y_pw) {
        this.hmac_x_y_pw = hmac_x_y_pw;
    }

    public String getHistoR() {
        return histoR;
    }

    public void setHistoR(String histoR) {
        this.histoR = histoR;
    }

    public String getHistoG() {
        return histoG;
    }

    public void setHistoG(String histoG) {
        this.histoG = histoG;
    }

    public String getHistoB() {
        return histoB;
    }

    public void setHistoB(String histoB) {
        this.histoB = histoB;
    }

    public int getNBAuthEchouees() {
        return NBAuthEchouees;
    }

    public void setNBAuthEchouees(int NBAuthEchouees) {
        this.NBAuthEchouees = NBAuthEchouees;
    }

    public User(String idUser, long x_hmac, long y_hmac, String hmac_x_y_pw, String histoR, String histoG, String histoB, int NBAuthEchouees ) {
        this.idUser = idUser;
        this.x_hmac = x_hmac;
        this.y_hmac = y_hmac;
        this.hmac_x_y_pw = hmac_x_y_pw;
        this.histoR = histoR;
        this.histoG = histoG;
        this.histoB = histoB;
        this.NBAuthEchouees = NBAuthEchouees;
    }



}
