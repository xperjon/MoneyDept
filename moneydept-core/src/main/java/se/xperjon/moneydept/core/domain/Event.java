package se.xperjon.moneydept.core.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Jon-Erik
 */
public class Event {

    private List<Person> participators;
    private List<Expense> expencies;
    private Map<Person, List<Dept>> peronalDeptMap = new HashMap<>();
    private Map<Person, Amount> expenseMap = new HashMap<>();

    public List<Person> getParticipators() {
        if (participators == null) {
            participators = new ArrayList<>();
        }
        return participators;
    }

    public void addParticipators(Person person) {
        getParticipators().add(person);
    }

    public Optional<Person> getPersonByName(String name) {
        return getParticipators().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void setExpencies(List<Expense> expencies) {
        this.expencies = expencies;
    }

    public List<Expense> getExpencies() {
        if (expencies == null) {
            expencies = new ArrayList<>();
        }
        return expencies;
    }

    public void calculateDepts() {
        initDeptMap();
        initExpenseMap();
        getExpencies().stream().forEach(this::handleExpense);
    }

    private void handleExpense(final Expense e) {
        e.getFore().stream()
                .filter(p -> !p.equals(e.getBy()))
                .forEach(p -> {
                    if (p.getRole().equals(Person.Role.PAYER)) {
                        peronalDeptMap.get(p).add(new Dept(p, e.getBy(), getDeptAmount(e),e.getName()));
                    } else if (p.getRole().equals(Person.Role.TREAT)) {
                        Amount a = getExpenseAmount(e);
//                        System.out.println("Person " + e.getBy().getName() + " adding " + a);
                        expenseMap.put(e.getBy(), expenseMap.get(e.getBy()).add(a));
//                        System.out.println("total: " + expenseMap.get(e.getBy()));
                    }
                });
    }

    public void printDeptResult() {
        peronalDeptMap.forEach((p, d) -> {
            System.out.println("---------------------------------------");
            System.out.println("Person: " + p.getName() + " has total expense for treaters: " + expenseMap.get(p));
            System.out.println("Personal depts:");
            d.stream().forEach(dept -> System.out.println(dept));
            System.out.println("---------------------------------------");
        });
    }

    private void initDeptMap() {
        getParticipators().stream().forEach(p -> {
            List<Dept> list = peronalDeptMap.get(p);
            if (list == null) {
                peronalDeptMap.put(p, new ArrayList<>());
            }
        });
    }

    private void initExpenseMap() {
        getParticipators().stream()
                .filter(p -> p.getRole().equals(Person.Role.PAYER))
                .forEach(p -> {
                    Amount a = expenseMap.get(p);
                    if (a == null) {
                        expenseMap.put(p, new Amount(0.0));
                    }
                });
    }

    private Amount getDeptAmount(Expense e) {
        long numOfFore = e.getFore().stream().count();
        return e.getAomunt().divide(new Amount(numOfFore));
    }

    private Amount getExpenseAmount(Expense e) {
        Amount a = getDeptAmount(e);
        long numOfFore = e.getFore().stream()
                .filter(p -> !p.equals(e.getBy()))
                .filter(p -> p.getRole().equals(Person.Role.TREAT))
                .count();
        return a.multiply(new Amount(numOfFore));
    }

}
