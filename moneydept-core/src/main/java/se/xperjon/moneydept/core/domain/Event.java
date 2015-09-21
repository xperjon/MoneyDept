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
    private final Map<Person, List<Dept>> peronalDeptMap = new HashMap<>();
    private final Map<Person, Amount> expenseMap = new HashMap<>();
    private final Map<Person, Map<Person, Amount>> personalDeptSumMap = new HashMap<>();

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
        getExpencies().stream().forEach(this::populateExpenseMap);
        getExpencies().stream().forEach(this::populatePersonalDeptMap);
        //get treaters payer
        List<Person> treaterPayers = expenseMap.keySet().stream()
                .collect(Collectors.partitioningBy(p -> expenseMap.get(p).isGreaterThanZero()))
                .get(true);
        treaterPayers.stream().forEach(payer -> {
            Amount amount = expenseMap.get(payer);
            Amount perPayingPerson = amount.divide(new Amount(6.0));
            participators.stream()
                    .filter(p -> p.getRole().equals(Person.Role.PAYER))
                    .forEach(p -> {
                        List<Dept> orDefault = peronalDeptMap.getOrDefault(p, new ArrayList<>());
                        orDefault.add(new Dept(p, payer, perPayingPerson, "Utlägg P&L"));
                        peronalDeptMap.put(p, orDefault);
                    });
        });
        //divide treater expencies over all payers (/num/2);
        //add peronal dept from all payers to treater payers.
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
        Amount orDefault = expenseMap.getOrDefault(e.getBy(),  new Amount(0));
        expenseMap.put(e.getBy(), orDefault.plus(getExpenseAmountForTreaters(e)));
    }

    private void populatePersonalDeptMap(final Expense e) {
        e.getFore().stream()
                .filter(p -> !p.equals(e.getBy()))
                .filter(p -> p.getRole().equals(Person.Role.PAYER))
                .forEach(p -> {
                    List<Dept> orDefault = peronalDeptMap.getOrDefault(p, new ArrayList<>());
                    orDefault.add(new Dept(p, e.getBy(), getExpenseAmountPerAttendingPerson(e), e.getName()));
                    peronalDeptMap.put(p, orDefault);
                });
    }

    public void printResult() {
        peronalDeptMap.forEach((p, d) -> {
            if (p.getRole().equals(Person.Role.PAYER)) {
                System.out.println("Person: " + p.getName());
                System.out.println("Utlägg för P&L");
                expencies.stream().filter(ex -> ex.getBy().equals(p))
                        .forEach(e -> {
                            System.out.println(e.getName()+ " " +getExpenseAmountForTreaters(e));
                        });
                System.out.println("Total: " + expenseMap.getOrDefault(p,new Amount(0L)));
                System.out.println("Personliga skulder:");
                d.stream().forEach(dept -> System.out.println(dept));
                Map<Person, Amount> personDeptSumMap = personalDeptSumMap.getOrDefault(p, Collections.EMPTY_MAP);
                System.out.println("TOTAL:");
                personDeptSumMap.forEach((to, amount) -> {
                    Map<Person, Amount> orDefault = personalDeptSumMap.getOrDefault(to, Collections.EMPTY_MAP);
                    Amount minus = orDefault.getOrDefault(p, new Amount(0));
                    System.out.println("Totalt till: " + to + " : " + amount + " - " + minus + " = " + new Amount(amount.minus(minus)) + ":-");
                });
                System.out.println("-----------------------------------------------------");
            }
        });
    }

    private Amount getExpenseAmountPerAttendingPerson(Expense e) {
        long numOfFore = e.getFore().stream().count();
        return e.getAomunt().divide(new Amount(numOfFore));
    }

    private Amount getExpenseAmountForTreaters(Expense e) {
        Amount a = getExpenseAmountPerAttendingPerson(e);
        long numOfFore = e.getFore().stream()
                .filter(p -> !p.equals(e.getBy()))
                .filter(p -> p.getRole().equals(Person.Role.TREAT))
                .count();
        return a.multiply(new Amount(numOfFore));
    }

}
