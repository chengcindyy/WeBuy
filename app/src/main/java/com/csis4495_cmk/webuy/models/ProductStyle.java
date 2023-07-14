package com.csis4495_cmk.webuy.models;

public class ProductStyle {

    String styleName;
    double stylePrice;
    String stylePicName;
    String stylePicUrl;

    public ProductStyle() {}

    public ProductStyle(String styleName, double stylePrice, String stylePicName) {
        this.styleName = styleName;
        this.stylePrice = stylePrice;
        this.stylePicName = stylePicName;
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
}
