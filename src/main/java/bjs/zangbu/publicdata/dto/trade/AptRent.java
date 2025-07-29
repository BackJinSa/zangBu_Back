package bjs.zangbu.publicdata.dto.trade;

public class AptRent {
    private String aptNm;
    private Integer buildYear;
    private Integer dealYear;
    private Integer dealMonth;
    private Integer dealDay;
    private String deposit;
    private Integer monthlyRent;
    private Double excluUseAr;
    private Integer floor;
    private Integer jibun;
    private String umdNm;
    private Integer sggCd;

    // getters / setters
    public String getAptNm() { return aptNm; }
    public void setAptNm(String aptNm) { this.aptNm = aptNm; }

    public Integer getBuildYear() { return buildYear; }
    public void setBuildYear(Integer buildYear) { this.buildYear = buildYear; }

    public Integer getDealYear() { return dealYear; }
    public void setDealYear(Integer dealYear) { this.dealYear = dealYear; }

    public Integer getDealMonth() { return dealMonth; }
    public void setDealMonth(Integer dealMonth) { this.dealMonth = dealMonth; }

    public Integer getDealDay() { return dealDay; }
    public void setDealDay(Integer dealDay) { this.dealDay = dealDay; }

    public String getDeposit() { return deposit; }
    public void setDeposit(String deposit) { this.deposit = deposit; }

    public Integer getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(Integer monthlyRent) { this.monthlyRent = monthlyRent; }

    public Double getExcluUseAr() { return excluUseAr; }
    public void setExcluUseAr(Double excluUseAr) { this.excluUseAr = excluUseAr; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public Integer getJibun() { return jibun; }
    public void setJibun(Integer jibun) { this.jibun = jibun; }

    public String getUmdNm() { return umdNm; }
    public void setUmdNm(String umdNm) { this.umdNm = umdNm; }

    public Integer getSggCd() { return sggCd; }
    public void setSggCd(Integer sggCd) { this.sggCd = sggCd; }
}
