package cn.wuzuqing.greendao.bean;

import cn.wuzuqing.lib_annotation.*;

@Entity()
public class School {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    private String address;

    private Long createDate;

    private Short sex;

    private Boolean old;

    private Integer ex0;

    private Float ex1;

    private Double ex2;

    private Byte ex3;

    @Gentrace(-1224959694)
    public School() {
    }

    @Gentrace(-988586986)
    public School(Long id, String name, String address, Long createDate, Short sex, Boolean old, Integer ex0, Float ex1, Double ex2, Byte ex3) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.createDate = createDate;
        this.sex = sex;
        this.old = old;
        this.ex0 = ex0;
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.ex3 = ex3;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getCreateDate() {
        return this.createDate;
    }

    public void setSex(Short sex) {
        this.sex = sex;
    }

    public Short getSex() {
        return this.sex;
    }

    public void setOld(Boolean old) {
        this.old = old;
    }

    public Boolean getOld() {
        return this.old;
    }

    public void setEx0(Integer ex0) {
        this.ex0 = ex0;
    }

    public Integer getEx0() {
        return this.ex0;
    }

    public void setEx1(Float ex1) {
        this.ex1 = ex1;
    }

    public Float getEx1() {
        return this.ex1;
    }

    public void setEx2(Double ex2) {
        this.ex2 = ex2;
    }

    public Double getEx2() {
        return this.ex2;
    }

    public void setEx3(Byte ex3) {
        this.ex3 = ex3;
    }

    public Byte getEx3() {
        return this.ex3;
    }

    @Override
    public String toString() {
        return "School{" +
                "id=" + id +
                ",name=" + name +
                ",address=" + address +
                ",createDate=" + createDate +
                ",sex=" + sex +
                ",old=" + old +
                ",ex0=" + ex0 +
                ",ex1=" + ex1 +
                ",ex2=" + ex2 +
                ",ex3=" + ex3 +
                "}";
    }

}
