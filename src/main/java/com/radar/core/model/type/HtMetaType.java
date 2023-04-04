package com.radar.core.model.type;

import com.radar.core.model.common.EnumModel;

public enum HtMetaType implements EnumModel {

    ALL("all");

    private String type;

    HtMetaType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return this.type;
    }

}
