package edu.ucalgary.oop;

import java.util.ArrayList;


public class FamilyGroup {
    private ArrayList<Person> members;
    private int familyGroupId = createFamilyGroupId();
    private static int counter = 100;

    /**
     * Constructs a FamilyGroup with a list of members.
     *
     * @param members The list of Person objects that make up the family group. Must not be null or empty.
     * @throws IllegalArgumentException if the members list is null or empty.
     */
    public FamilyGroup(ArrayList<Person> members) {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Members cannot be null or empty");
        }
        this.members = members;
    }



    /**
     * Returns the list of members in the family group.
     *
     * @return An ArrayList of Person objects representing the members of the family group.
     */
    public ArrayList<Person> getMembers() {
        return members;
    }

    /**
     * Returns the ID for the family group.
     *
     * @return The unique ID of the family group.
     */
    public int getFamilyGroupId() {
        return familyGroupId;
    }


    /**
     * Sets the ID for the family group.
     *
     * @param familyGroupId The unique ID to set for the family group.
     */
    public void setFamilyGroupId(int familyGroupId) {
        this.familyGroupId = familyGroupId;
    }

    /**
     * Sets the list of members for the family group.
     *
     * @param members The list of `Person` objects to set as the family group's members.
     */
    public void setMembers(ArrayList<Person> members) {
        this.members = members;
    }




    /**
     * Adds a new member to the family group.
     *
     * @param person The `Person` to be added to the family group.
     * @throws IllegalArgumentException If the person is null.
     */
    public void addMember(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        members.add(person);
    }


    /**
     * Removes a member from the family group.
     *
     * @param person The `Person` to be removed from the family group.
     * @throws IllegalArgumentException If the person is null or if the person is not a member of the family group.
     */
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

    /**
     * Generates a new unique family group ID.
     *
     * @return The newly generated family group ID.
     */
    private int createFamilyGroupId() {
        return counter++;
    }

}
