package com.csis4495_cmk.webuy.models;

public class ProductStyle {

    String styleName;
    double stylePrice;
    String stylePic;





    public ProductStyle() {}

    public ProductStyle(String styleName, double stylePrice, String stylePic) {
        this.styleName = styleName;
        this.stylePrice = stylePrice;
        this.stylePic = stylePic;
    }


    public String getStyleName() {
        return styleName;
    }

    public double getStylePrice() {
        return stylePrice;
    }

    public String getStylePic() {
        return stylePic;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public void setStylePrice(double stylePrice) {
        this.stylePrice = stylePrice;
    }

    public void setStylePic(String stylePic) {
        this.stylePic = stylePic;
    }
}
