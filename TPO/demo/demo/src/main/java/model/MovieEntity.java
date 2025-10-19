package com.rutear.demo.model;

import java.util.Set;
import java.util.HashSet;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;




@Node("Movie")
public class MovieEntity {
    @Id 
    private final String title;
    @Property("tagline")
    private final String description;
    @Relationship(type = "ACTED_IN", direction = INCOMING)
    private Set<PersonEntity> actors = new HashSet<>();
    public MovieEntity(String title, String description) {
        this.title = title;
        this.description = description;
    }     

    public void setActors(Set<PersonEntity> actors) {
        this.actors = (actors == null) ? new HashSet<>() : new HashSet<>(actors);
    }

    public void addActor(PersonEntity actor) {
        if (actor != null) {
            this.actors.add(actor);
        }
    }

    public void removeActor(PersonEntity actor) {
        this.actors.remove(actor);
    }

    @Override
    public String toString() {
        return "MovieEntity{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", actors=" + actors +
                '}';
    }
}