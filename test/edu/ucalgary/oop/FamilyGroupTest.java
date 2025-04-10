/**
 * FamilyGroupTest.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;


public class FamilyGroupTest {

    private Person personOne;
    private Person personTwo;
    private FamilyGroup familyGroup;
    private ArrayList<Person> members;


    @Before
    public void setUp() {
        personOne = new Person("Jay", "Luck");
        personTwo = new Person("Emily", "Fall");
        members = new ArrayList<>();
        members.add(personOne);
        members.add(personTwo);
        familyGroup = new FamilyGroup(members);
    }


    @Test
    public void testFamilyGroupConstructor() {
        assertNotNull("Constructor did not create an instance", familyGroup);
        assertEquals("Constructor set members incorrectly", members, familyGroup.getMembers());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testFamilyGroupConstructorWithNullMembers() {
        FamilyGroup familyGroupOne = new FamilyGroup(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFamilyGroupConstructorWithEmptyMembers() {
        FamilyGroup familyGroupOne = new FamilyGroup(new ArrayList<>());
    }

    @Test
    public void testAddMember() {
        Person newPerson = new Person("Lionel", "Messi");
        familyGroup.addMember(newPerson);
        assertTrue("Member was not added", familyGroup.getMembers().contains(newPerson));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullMember() {
        familyGroup.addMember(null);
    }

    @Test
    public void testRemoveMember() {
        familyGroup.removeMember(personOne);
        assertFalse("Member was not removed", familyGroup.getMembers().contains(personOne));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveMemberThatIsNotInFamilyGroup() {
        Person personThree = new Person("Sergio", "Aguero");
        familyGroup.removeMember(personThree);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullMember() {
        familyGroup.removeMember(null);
    }

    @Test
    public void testGetMembers() {
        assertEquals("Getter for members is incorrect", members, familyGroup.getMembers());
    }


}