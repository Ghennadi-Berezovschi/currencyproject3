package com.currencyapi.model;

public class Currency {
    private int id;
    private String code;

    private String name;
    private String sign;

    public Currency(int id, String code, String fullname, String sign) {
        this.id = id;
        this.code = code;
        this.name = fullname;
        this.sign = sign;
    }
    public Currency() {

    }


    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullname='" + name + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
