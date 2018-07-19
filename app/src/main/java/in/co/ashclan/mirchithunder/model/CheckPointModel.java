package in.co.ashclan.mirchithunder.model;

public class CheckPointModel {
    String id,puid,batch,start_time,checkpoint_time,status,endPointTime;
    String startptImage,checkptImage,endptImage;

    public CheckPointModel(String id, String puid, String batch, String start_time, String checkpoint_time, String status, String endPointTime, String startptImage, String checkptImage, String endptImage) {
        this.id = id;
        this.puid = puid;
        this.batch = batch;
        this.start_time = start_time;
        this.checkpoint_time = checkpoint_time;
        this.status = status;
        this.endPointTime = endPointTime;
        this.startptImage = startptImage;
        this.checkptImage = checkptImage;
        this.endptImage = endptImage;
    }

    @Override
    public String toString() {
        return "CheckPointModel{" +
                "id='" + id + '\'' +
                ", puid='" + puid + '\'' +
                ", batch='" + batch + '\'' +
                ", start_time='" + start_time + '\'' +
                ", checkpoint_time='" + checkpoint_time + '\'' +
                ", status='" + status + '\'' +
                ", endPointTime='" + endPointTime + '\'' +
                ", startptImage='" + startptImage + '\'' +
                ", checkptImage='" + checkptImage + '\'' +
                ", endptImage='" + endptImage + '\'' +
                '}';
    }

    public CheckPointModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getCheckpoint_time() {
        return checkpoint_time;
    }

    public void setCheckpoint_time(String checkpoint_time) {
        this.checkpoint_time = checkpoint_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndPointTime() {
        return endPointTime;
    }

    public void setEndPointTime(String endPointTime) {
        this.endPointTime = endPointTime;
    }

    public String getStartptImage() {
        return startptImage;
    }

    public void setStartptImage(String startptImage) {
        this.startptImage = startptImage;
    }

    public String getCheckptImage() {
        return checkptImage;
    }

    public void setCheckptImage(String checkptImage) {
        this.checkptImage = checkptImage;
    }

    public String getEndptImage() {
        return endptImage;
    }

    public void setEndptImage(String endptImage) {
        this.endptImage = endptImage;
    }
}
