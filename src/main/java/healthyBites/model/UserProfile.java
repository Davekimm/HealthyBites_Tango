package healthyBites.model;

import java.util.Date;

/**
 * Represent a user and all of their prefrences and attributes 
 */
public class UserProfile {
    //name.dob. height. weight, unit of measurement, sex, email, pw
    private String name, sex, email, unitOfMeasurement;
    private Date dob;
    private double height, weight;

    public UserProfile(String name, String sex, String email, String unitOfMeasurement, Date dob, double height,
            double weight) {
        this.name = name;
        this.sex = sex;
        this.email = email;
        this.unitOfMeasurement = unitOfMeasurement;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getEmail() {
        return email;
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
}
