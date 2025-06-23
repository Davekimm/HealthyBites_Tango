package healthyBites.model;

import java.util.Date;

public class UserProfile {
    //name.dob. height. weight, unit of measurement, sex, email, pw
    private String name, sex,email, pw, unitOfMeasurement;
    private Date dob;
    private double height, weight;

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getEmail() {
        return email;
    }

    public String getPw() {
        return pw;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public Date getDob() {
        return dob;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public UserProfile(String name, String sex, String email, String pw, String unitOfMeasurement, Date dob, double height,
            double weight) {
        this.name = name;
        this.sex = sex;
        this.email = email;
        this.pw = pw;
        this.unitOfMeasurement = unitOfMeasurement;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
    }


}
