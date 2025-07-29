package bjs.zangbu.publicdata.dto.trade;

public class AptTrade {
    private String aptDong;
    private String aptNm;
    private int buildYear;
    private String dealAmount;
    private int dealYear;
    private int dealMonth;
    private int dealDay;
    private double excluUseAr;
    private int floor;
    private String jibun;
    private String landLeaseholdGbn;
    private String umdNm;
    private int sggCd;

    // getters / setters
    public String getAptDong() { return aptDong; }
    public void setAptDong(String aptDong) { this.aptDong = aptDong; }

    public String getAptNm() { return aptNm; }
    public void setAptNm(String aptNm) { this.aptNm = aptNm; }

    public int getBuildYear() { return buildYear; }
    public void setBuildYear(int buildYear) { this.buildYear = buildYear; }

    public String getDealAmount() { return dealAmount; }
    public void setDealAmount(String dealAmount) { this.dealAmount = dealAmount; }

    public int getDealYear() { return dealYear; }
    public void setDealYear(int dealYear) { this.dealYear = dealYear; }

    public int getDealMonth() { return dealMonth; }
    public void setDealMonth(int dealMonth) { this.dealMonth = dealMonth; }

    public int getDealDay() { return dealDay; }
    public void setDealDay(int dealDay) { this.dealDay = dealDay; }

    public double getExcluUseAr() { return excluUseAr; }
    public void setExcluUseAr(double excluUseAr) { this.excluUseAr = excluUseAr; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getJibun() { return jibun; }
    public void setJibun(String jibun) { this.jibun = jibun; }

    public String getLandLeaseholdGbn() { return landLeaseholdGbn; }
    public void setLandLeaseholdGbn(String landLeaseholdGbn) { this.landLeaseholdGbn = landLeaseholdGbn; }

    public String getUmdNm() { return umdNm; }
    public void setUmdNm(String umdNm) { this.umdNm = umdNm; }

    public int getSggCd() { return sggCd; }
    public void setSggCd(int sggCd) { this.sggCd = sggCd; }
}