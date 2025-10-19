package com.rutear.demo.model;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;



@Node("Person")
public class PersonEntity {
    @Id
    private final String name;
    private final Integer born;
    public PersonEntity(String name, Integer born) {
        this.name = name;
        this.born = born;
    }


    public String getName() {
        return name;
    }

    public Integer getBorn() {
        return born;
    }

    /**
     * Los campos son final; en lugar de setters mutables se proporcionan métodos
     * inmutables que devuelven una nueva instancia con el campo modificado.
     */
    public PersonEntity withName(String name) {
        return new PersonEntity(name, this.born);
    }

    public PersonEntity withBorn(Integer born) {
        return new PersonEntity(this.name, born);
    }

    @Override
    public String toString() {
        return "PersonEntity{name='" + name + "', born=" + born + '}';
    }
}