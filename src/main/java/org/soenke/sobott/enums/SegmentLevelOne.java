package org.soenke.sobott.enums;

public enum SegmentLevelOne {
    Residential("Residential"),
    NonResidential("Non-Residential"),
    Infrastructure("Infrastructure"),
    Industrial("Industrial");

    private final String value;

    SegmentLevelOne(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
