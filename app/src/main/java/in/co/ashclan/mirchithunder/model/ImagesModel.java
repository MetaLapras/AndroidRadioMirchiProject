package in.co.ashclan.mirchithunder.model;

public class ImagesModel {
String id,image;

    public ImagesModel(String id, String image) {
        this.id = id;
        this.image = image;
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
}
