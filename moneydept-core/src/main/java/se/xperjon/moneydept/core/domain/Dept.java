package se.xperjon.moneydept.core.domain;

/**
 *
 * @author Jon-Erik
 */
public class Dept {

    private Person from;
    private Person to;
    private Amount amount;
    private String name;

    public Dept(Person from, Person to, Amount amount, String name) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(from.getName());
        sb.append(" owes ");
        sb.append(to.getName());
        sb.append(" ");
        sb.append(amount);
        sb.append(" bucks for ");
        sb.append(name);
        sb.append("!");
        return sb.toString();
    }

}
