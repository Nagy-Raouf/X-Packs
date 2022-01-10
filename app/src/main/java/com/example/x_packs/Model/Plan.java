package com.example.x_packs.Model;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Plan {
    private String dayString;
    //@ColumnInfo(name = "data")
    private String data;
    private int done;
    private int max;
    private int totalNum;



    @PrimaryKey
    public int id;

    public Plan(int id, String day, String data, int done) {
        this.id = id;
        this.dayString = day;
        this.data = data;
        this.done = done;
        setTotalNum_Max();
    }
    private void setTotalNum_Max() {
        int num = 0;
        String[] session = data.split(" - ");
        for (int i=0;i<session.length;i++){
            int tmp=Integer.parseInt(session[i]);
            num+=tmp;
            if(this.max<tmp){
                this.max=tmp;
            }
        }
        this.totalNum =num;
    }



    public Plan() {
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
