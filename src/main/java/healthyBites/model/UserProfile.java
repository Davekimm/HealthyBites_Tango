package healthyBites.model;

import java.util.Date;

/**
 * This class represents a user's profile in the HealthyBites app.
 * It stores personal details like name, date of birth, email, etc.
 */
public class UserProfile {
    //name.dob. height. weight, unit of measurement, sex, email, pw
    private String name, sex, email, unitOfMeasurement;
    private Date dob;
    private double height, weight;

    /**
     * Constructor to create a new user profile.
     *
     * @param name the user's name
     * @param sex the user's sex
     * @param email the user's email
     * @param unitOfMeasurement unit of measurement (imperial/metric)
     * @param dob the user's date of birth
     * @param height the user's height
     * @param weight the user's weight
     */
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

    /**
     * Gets the user's name.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's sex.
     *
     * @return the user's sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * Gets the user's email.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the unit of measurement (like kg or lb).
     *
     * @return the unit of measurement
     */
    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    /**
     * Gets the user's date of birth.
     *
     * @return the date of birth
     */
    public Date getDob() {
        return dob;
    }

    /**
     * Gets the user's height.
     *
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets the user's weight.
     *
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the user's name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the user's sex.
     *
     * @param sex the new sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Sets the user's email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the unit of measurement.
     *
     * @param unitOfMeasurement the new unit of measurement
     */
    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    /**
     * Sets the user's date of birth.
     *
     * @param dob the new date of birth
     */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /**
     * Sets the user's height.
     *
     * @param height the new height
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Sets the user's weight.
     *
     * @param weight the new weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
