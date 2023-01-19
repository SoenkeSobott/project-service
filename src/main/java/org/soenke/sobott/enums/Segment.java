package org.soenke.sobott.enums;

public enum Segment {
    Residential("Residential"),
    NonResidential("Non-Residential"),
    Infrastructure("Infrastructure"),
    Industrial("Industrial");

    private final String value;

    Segment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
