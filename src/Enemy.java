import java.util.List;

public class Enemy {
    String name;
    int level;
    int health;
    int attack;
    int experience;
    int lootPrice;
    List<String> tags;

    Enemy(String name, int level, int health, int attack, int experience, int lootPrice,List<String> tags) {
        this.name = name;
        this.level = level;
        this.health = health;
        this.attack = attack;
        this.experience = experience;
        this.lootPrice = lootPrice;
        this.tags = tags;
    }
}