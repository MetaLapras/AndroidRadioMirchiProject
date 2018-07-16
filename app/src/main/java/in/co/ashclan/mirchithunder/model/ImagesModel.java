package in.co.ashclan.mirchithunder.model;

import java.util.ArrayList;
import java.util.HashMap;

public class ImagesModel {
        String bkid;
        String puid;
        ArrayList<String> images;
        String mobile;
        //ArrayList<String> images;


    public ImagesModel() {
    }

    public ImagesModel(String bkid, String puid, ArrayList<String> images, String mobile) {
        this.bkid = bkid;
        this.puid = puid;
        this.images = images;
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "ImagesModel{" +
                "bkid='" + bkid + '\'' +
                ", puid='" + puid + '\'' +
                ", images=" + images +
                ", mobile='" + mobile + '\'' +
                '}';
    }

    public String getBkid() {
        return bkid;
    }

    public void setBkid(String bkid) {
        this.bkid = bkid;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
    public void addImage(String s){
        images.add(s);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
