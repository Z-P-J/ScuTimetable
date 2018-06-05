package com.zpj;

public class FragmentItem {
    private String kechengName;
    private String jieshu;
    private String shijian;

    public FragmentItem(String kechengName,String jieshu,String shijian){
        this.kechengName=kechengName;
        this.jieshu=jieshu;
        this.shijian=shijian;
    }

    public String getKechengName(){
        return kechengName;
    }

    public void setKechengName(String kechengName){
        this.kechengName=kechengName;
    }

    public String getJieshu(){
        return jieshu;
    }

    public void setJieshu(String jieshu){
        this.jieshu=jieshu;
    }

    public String getShijian(){
        return shijian;
    }

    public void setShijian(String shijian){
        this.shijian=shijian;
    }
}
