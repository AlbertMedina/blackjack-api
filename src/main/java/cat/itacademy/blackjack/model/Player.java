package cat.itacademy.blackjack.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "players")
public class Player {

    @Id
    private Long id;

    private String name;
    private int numberOfWins = 0;
    private int numberOfTies = 0;
    private int numberOfLosses = 0;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public int getNumberOfTies() {
        return numberOfTies;
    }

    public int getNumberOfLosses() {
        return numberOfLosses;
    }

    public void win() {
        numberOfWins++;
    }

    public void tie() {
        numberOfTies++;
    }

    public void lose() {
        numberOfLosses++;
    }
}
