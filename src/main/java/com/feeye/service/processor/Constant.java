package com.feeye.service.processor;

public enum Constant {
    L8("L8", "祥鹏航空");
    private String key;
    private String value;

    Constant(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
