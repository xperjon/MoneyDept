package se.xperjon.moneydept.core.domain;

import java.util.Objects;

/**
 *
 * @author Jon-Erik
 */
public class Person {

    private final String name;
    private final Role role;

    public Person(String name, Role role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    
    public enum Role {
        PAYER,
        TREAT
    }

    @Override
    public String toString() {
        return name;
    }

    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    
}
