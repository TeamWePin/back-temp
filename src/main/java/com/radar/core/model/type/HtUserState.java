package com.radar.core.model.type;


import com.radar.core.model.common.CodeEnumModel;

public enum HtUserState implements CodeEnumModel {


    ACTIVE(1, "ACTIVE"),
    DORMANT(2, "DORMANT"),
    TROT(3, "STANDBY"),
    WITHDRAW(4, "WITHDRAW"),
    BLACK(5, "BLACK");

    private int state;
    private String name;

    HtUserState(int state, String name) {
        this.state = state;
        this.name = name;
    }

    public int getState() {
        return this.state;
    }

    public String getName() {
        return this.name;
    }

    public String geValue(){
        return super.toString();
    }

    @Override
    public int getCode() {
        return getState();
    }
}
