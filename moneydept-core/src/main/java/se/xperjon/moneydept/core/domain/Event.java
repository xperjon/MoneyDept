package se.xperjon.moneydept.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Jon-Erik
 */
public class Event {

    private List<Person> participators;
    private List<Expense> expencies;
    private Map<Person, List<Dept>> peronalDeptMap = new HashMap<>();
    private Map<Person, Amount> expenseMap = new HashMap<>();
    private Map<Person, Map<Person, Amount>> personalDeptSumMap = new HashMap<>();

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

    public void processExpencies() {
        initDeptMap();
        initExpenseMap();
        getExpencies().stream().forEach(this::populateExpenseMap);
        getExpencies().stream().forEach(this::populatePersonalDeptMap);
        sumPersonalDepts();
    }

    public void sumPersonalDepts() {
        this.peronalDeptMap.forEach((Person from, List<Dept> l) -> {
            Map<Person, List<Dept>> deptsPerPerson = l.stream().collect(Collectors.groupingBy(d -> d.getTo()));
            deptsPerPerson.forEach((to, deptList) -> {
                Double totalDept = deptList.stream().collect(Collectors.summingDouble(d -> d.getAmount().getAsDouble()));
                Map<Person, Amount> hashMap = personalDeptSumMap.getOrDefault(from, new HashMap<>());
                hashMap.put(to, new Amount(totalDept));
                personalDeptSumMap.put(from, hashMap);
            });
        });
    }

    private void populateExpenseMap(final Expense e) {
        e.getFore().stream()
                .filter(p -> !p.equals(e.getBy()))
                .filter(p -> p.getRole().equals(Person.Role.TREAT))
                .forEach(p -> {
                    Amount newAmount = expenseMap.get(e.getBy()).plus(getExpenseAmount(e));
                    expenseMap.put(e.getBy(), newAmount);
                });
    }

    private void populatePersonalDeptMap(final Expense e) {
        e.getFore().stream()
                .filter(p -> !p.equals(e.getBy()))
                .filter(p -> p.getRole().equals(Person.Role.PAYER))
                .forEach(p -> {
                    peronalDeptMap.get(p).add(new Dept(p, e.getBy(), getPersonalDeptAmount(e), e.getName()));
                });
    }

    public void printResult() {
        peronalDeptMap.forEach((p, d) -> {
            if (p.getRole().equals(Person.Role.PAYER)) {
                System.out.println("Person: " + p.getName() + " has total expense for treaters: " + expenseMap.get(p));
                System.out.println("Personal depts:");
                d.stream().forEach(dept -> System.out.println(dept));
                Map<Person, Amount> personDeptSumMap = personalDeptSumMap.getOrDefault(p, Collections.EMPTY_MAP);
                personDeptSumMap.forEach((to, amount) -> {
                    Map<Person, Amount> orDefault = personalDeptSumMap.getOrDefault(to, Collections.EMPTY_MAP);
                    Amount minus = orDefault.getOrDefault(p, new Amount(0));
                    System.out.println("Total to: " + to + " : " + amount + " - " + minus + " = " + new Amount(amount.minus(minus)) + " bucks");
                });
                System.out.println("-----------------------------------------------------");
            }
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

    private Amount getPersonalDeptAmount(Expense e) {
        long numOfFore = e.getFore().stream().count();
        return e.getAomunt().divide(new Amount(numOfFore));
    }

    private Amount getExpenseAmount(Expense e) {
        Amount a = getPersonalDeptAmount(e);
        long numOfFore = e.getFore().stream()
                .filter(p -> !p.equals(e.getBy()))
                .filter(p -> p.getRole().equals(Person.Role.TREAT))
                .count();
        return a.multiply(new Amount(numOfFore));
    }

}
