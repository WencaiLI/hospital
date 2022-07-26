package com.thtf.office.common.response;

public enum Status {
    SUCCESS("success"),
    ERROR("error");

    private String value;

    Status(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
