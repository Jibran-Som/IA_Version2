package edu.ucalgary.oop;

import java.util.ArrayList;


public class FamilyGroup {
    private ArrayList<Person> members;
    private int familyGroupId = createFamilyGroupId();
    private static int counter = 100;

    // Constructors
    public FamilyGroup(ArrayList<Person> members) {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Members cannot be null or empty");
        }
        this.members = members;
    }



    // Getters
    public ArrayList<Person> getMembers() {
        return members;
    }

    public int getFamilyGroupId() {
        return familyGroupId;
    }


    // Setters
    public void setFamilyGroupId(int familyGroupId) {
        this.familyGroupId = familyGroupId;
    }

    public void setMembers(ArrayList<Person> members) {
        this.members = members;
    }




    // Class Specific Code
    public void addMember(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        members.add(person);
    }

    public void removeMember(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        if (!members.contains(person)) {
            throw new IllegalArgumentException("Person is not in the family group");
        }
        members.remove(person);
    }









    // Private Code for Checking or Initialization

    private int createFamilyGroupId() {
        return counter++;
    }

}
