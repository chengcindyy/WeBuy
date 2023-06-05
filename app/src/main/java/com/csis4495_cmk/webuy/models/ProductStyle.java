package com.csis4495_cmk.webuy.models;

public class ProductStyle {
    String stylePic;
    String style;
    double stylePrice;
    int styleQty;
    String styleTax;

    public ProductStyle(){

    }

    public ProductStyle(String stylePic, String style, double stylePrice, int styleQty, String styleTax) {
        this.stylePic = stylePic;
        this.style = style;
        this.stylePrice = stylePrice;
        this.styleQty = styleQty;
        this.styleTax = styleTax;
    }

    public String getStylePic() {
        return stylePic;
    }

    public void setStylePic(String stylePic) {
        this.stylePic = stylePic;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public double getStylePrice() {
        return stylePrice;
    }

    public void setStylePrice(double stylePrice) {
        this.stylePrice = stylePrice;
    }

    public int getStyleQty() {
        return styleQty;
    }

    public void setStyleQty(int styleQty) {
        this.styleQty = styleQty;
    }

    public String getStyleTax() {
        return styleTax;
    }

    public void setStyleTax(String styleTax) {
        this.styleTax = styleTax;
    }
}
