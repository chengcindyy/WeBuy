package com.csis4495_cmk.webuy.models;

public class ProductStyle {

    String styleName;
    double stylePrice;
    String stylePicName;
    String styleId;

    public ProductStyle() {}

    public ProductStyle(String styleName, double stylePrice, String stylePicName, String styleId) {
        this.styleName = styleName;
        this.stylePrice = stylePrice;
        this.stylePicName = stylePicName;
        this.styleId = styleId;
    }


    public String getStyleId() {
        return styleId;
    }

    public String getStyleName() {
        return styleName;
    }

    public double getStylePrice() {
        return stylePrice;
    }

    public String getStylePicName() {
        return stylePicName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public void setStylePrice(double stylePrice) {
        this.stylePrice = stylePrice;
    }

    public void setStylePicName(String stylePicName) {
        this.stylePicName = stylePicName;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }
}
