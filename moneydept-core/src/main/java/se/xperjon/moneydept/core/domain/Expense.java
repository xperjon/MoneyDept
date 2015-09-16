package se.xperjon.moneydept.core.domain;

import java.util.List;

/**
 *
 * @author Jon-Erik
 */
public class Expense {

    private String name;
    private Person by;
    private List<Person> fore;
    private Amount aomunt;

    public Expense(String name, Person by, List<Person> fore, Amount aomunt) {
        this.name = name;
        this.by = by;
        this.fore = fore;
        this.aomunt = aomunt;
    }

    public Person getBy() {
        return by;
    }

    public List<Person> getFore() {
        return fore;
    }

    public Amount getAomunt() {
        return aomunt;
    }

    public String getName() {
        return name;
    }
    
}
