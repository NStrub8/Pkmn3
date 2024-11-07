package ru.mirea.pkmn;

import java.io.Serial;
import java.io.Serializable;

public class Student implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public Student() {}
    private String firstName;

    public Student(String firstName, String surName, String familyName, String group) {
        this.firstName = firstName;
        this.surName = surName;
        this.familyName = familyName;
        this.group = group;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String value) {
        firstName = value;
    }

    private String surName;
    public String getSurName() {
        return surName;
    }
    public void setSurName(String value) {
        surName = value;
    }

    private String familyName;
    public String getFamilyName() {
        return familyName;
    }
    public void setFamilyName(String value) {
        familyName = value;
    }

    private String group;
    public String getGroup() {
        return group;
    }
    public void setGroup(String value) {
        group = value;
    }

    @Override
    public String toString() {
        return "Student{" +
                "firstName=" + firstName +
                ", surName=" + surName +
                ", familyName=" + familyName +
                ", group=" + group +
                '}';
    }
}