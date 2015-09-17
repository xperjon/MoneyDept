package se.xperjon.moneydept.core;

import java.util.Arrays;
import java.util.List;
import se.xperjon.moneydept.core.domain.Amount;
import se.xperjon.moneydept.core.domain.Event;
import se.xperjon.moneydept.core.domain.Expense;
import se.xperjon.moneydept.core.domain.Person;

/**
 *
 * @author Jon-Erik
 */
public class Main {

    public static void main(String[] args) {
        final Event event = new Event();
        List<Person> persons = Arrays.asList(new Person("Jon-Erik", Person.Role.PAYER),
                new Person("Emma", Person.Role.PAYER),
                new Person("Jörgen", Person.Role.PAYER),
                new Person("Adam", Person.Role.PAYER),
                new Person("Mathilda", Person.Role.PAYER),
                new Person("Pär", Person.Role.TREAT),
                new Person("Lena", Person.Role.TREAT));

        persons.stream().forEach(p -> event.addParticipators(p));

        Expense e1 = new Expense("Lunch", event.getPersonByName("Jon-Erik").get(),
                Arrays.asList(event.getPersonByName("Jon-Erik").get(), event.getPersonByName("Adam").get(), event.getPersonByName("Pär").get(), event.getPersonByName("Jörgen").get()),
                new Amount(460.0));

        Expense e2 = new Expense("Lunch och Bad", event.getPersonByName("Emma").get(),
                Arrays.asList(event.getPersonByName("Lena").get()),
                new Amount(285));

        Expense e3 = new Expense("Bad", event.getPersonByName("Jon-Erik").get(),
                Arrays.asList(event.getPersonByName("Jon-Erik").get(), event.getPersonByName("Pär").get(), event.getPersonByName("Adam").get(), event.getPersonByName("Jörgen").get()),
                new Amount(660));

        Expense e4 = new Expense("Bärs", event.getPersonByName("Jon-Erik").get(),
                Arrays.asList(event.getPersonByName("Jon-Erik").get(), event.getPersonByName("Pär").get(), event.getPersonByName("Adam").get(), event.getPersonByName("Lena").get()),
                new Amount(304));

        Expense e5 = new Expense("Middag", event.getPersonByName("Jon-Erik").get(),
                Arrays.asList(event.getPersonByName("Jon-Erik").get(), event.getPersonByName("Pär").get(), event.getPersonByName("Emma").get(), event.getPersonByName("Lena").get()),
                new Amount(787));

        Expense e6 = new Expense("Bowling", event.getPersonByName("Jon-Erik").get(),
                Arrays.asList(event.getPersonByName("Jon-Erik").get(), event.getPersonByName("Pär").get(), event.getPersonByName("Adam").get(), event.getPersonByName("Jörgen").get(),
                        event.getPersonByName("Mathilda").get(), event.getPersonByName("Emma").get(), event.getPersonByName("Lena").get()),
                new Amount(1400));

        Expense e7 = new Expense("Stugga", event.getPersonByName("Jon-Erik").get(),
                Arrays.asList(event.getPersonByName("Jon-Erik").get(), event.getPersonByName("Pär").get(), event.getPersonByName("Adam").get(), event.getPersonByName("Jörgen").get(),
                        event.getPersonByName("Mathilda").get(), event.getPersonByName("Emma").get(), event.getPersonByName("Lena").get()),
                new Amount(1000));

        Expense e8 = new Expense("Matkassar", event.getPersonByName("Jörgen").get(),
                Arrays.asList(event.getPersonByName("Jon-Erik").get(), event.getPersonByName("Pär").get(), event.getPersonByName("Adam").get(), event.getPersonByName("Jörgen").get(),
                        event.getPersonByName("Mathilda").get(), event.getPersonByName("Emma").get(), event.getPersonByName("Lena").get()),
                new Amount(825));

        Expense e9 = new Expense("Pizza", event.getPersonByName("Jörgen").get(),
                Arrays.asList(event.getPersonByName("Jon-Erik").get(), event.getPersonByName("Pär").get(), event.getPersonByName("Adam").get(), event.getPersonByName("Jörgen").get(),
                        event.getPersonByName("Mathilda").get(), event.getPersonByName("Emma").get(), event.getPersonByName("Lena").get()),
                new Amount(960));

        event.setExpencies(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9));
        event.processExpencies();
        event.printResult();
    }

}
