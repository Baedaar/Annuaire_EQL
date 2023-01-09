package fr.eql.ai113.baeflopy.annuaire.application;

public class Student {
    private String name;
    private String firstName;
    private String year;
    private String formation;
    private String department;
    private long position;

    public Student() {
    }

    public Student(String name, String firstName, String year, String formation, String department, long position) {
        this.name = name;
        this.firstName = firstName;
        this.year = year;
        this.formation = formation;
        this.department = department;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getYear() {
        return year;
    }

    public String getFormation() {
        return formation;
    }

    public String getDepartment() {
        return department;
    }

    public long getPosition() {
        return position;
    }


    public void setPosition(long position) {
        this.position = position;
    }

    @Override
    public String toString(){
        String message = this.getName()
                + this.getFirstName()
                + this.getYear()
                + this.getFormation()
                + this.getDepartment();

        return message;
    }
}
