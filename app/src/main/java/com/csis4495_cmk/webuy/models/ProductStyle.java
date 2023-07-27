package com.csis4495_cmk.webuy.models;

public class ProductStyle {

    String styleName;
    double stylePrice;
    String stylePicName;
    String styleId;
    private int inStock = 33;

    public ProductStyle() {}

    public ProductStyle(String styleName, double stylePrice, String stylePicName, String styleId) {
        this.styleName = styleName;
        this.stylePrice = stylePrice;
        this.stylePicName = stylePicName;
        this.styleId = styleId;
    }
    public ProductStyle(String styleName, double stylePrice, String stylePicName) {
        this.styleName = styleName;
        this.stylePrice = stylePrice;
        this.stylePicName = stylePicName;
    }

    public ProductStyle(String styleName, double stylePrice, String stylePicName, String styleId, int inStock) {
        this.styleName = styleName;
        this.stylePrice = stylePrice;
        this.stylePicName = stylePicName;
        this.styleId = styleId;
        this.inStock = inStock;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
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
