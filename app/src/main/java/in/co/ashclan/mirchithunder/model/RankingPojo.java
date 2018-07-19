package in.co.ashclan.mirchithunder.model;

public class RankingPojo {
    String puid,batch,name,starttime,endtime,cat;

    public RankingPojo(String puid, String batch, String name, String starttime, String endtime, String cat) {
        this.puid = puid;
        this.batch = batch;
        this.name = name;
        this.starttime = starttime;
        this.endtime = endtime;
        this.cat = cat;
    }

    public RankingPojo() {
    }

    @Override
    public String toString() {
        return "RankingPojo{" +
                "puid='" + puid + '\'' +
                ", batch='" + batch + '\'' +
                ", name='" + name + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", cat='" + cat + '\'' +
                '}';
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }
}
