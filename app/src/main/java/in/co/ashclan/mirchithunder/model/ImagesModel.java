package in.co.ashclan.mirchithunder.model;

import java.util.ArrayList;

public class ImagesModel {
        String id;
        String image;
        String mobile;
        ArrayList<String> picutres;

    public ImagesModel(String id, String image) {
        this.id = id;
        this.image = image;
    }

    @Override
    public String toString() {
         return "ImageModel :"+
                " id ='" + id + '\'' +
                ", pictures ='" + picutres + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public ImagesModel() {
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public ArrayList<String> getPicutres() {
        return picutres;
    }
    public void setPicutres(ArrayList<String> picutres) {
        this.picutres = picutres;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
