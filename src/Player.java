import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    String name;
    int level;
    int health;
    int maxHealth;
    int attack;
    int coin;
    int experience;
    int experienceToLevelUp;
    String map;
    int storyProgress;
    Map<String, Integer> lootBackpack; // lootName -> total price
    List<String> unlockedMap = new ArrayList<>();
    //if finished: -1 not started:-2
    Map<String,int[]> tasksProgress = new HashMap();
    Map<String,Integer> potionBackpack;

    Player(String name) {
        this.name = name;
        this.level = 1;
        this.health = 10;
        this.maxHealth = 10;
        this.attack = 1;
        this.coin = 0;
        this.experience = 0;
        this.experienceToLevelUp = 50;
        this.storyProgress = 1;
        this.unlockedMap.add("tutorial");
        this.map = "tutorial";
        this.lootBackpack = new HashMap<>();
        this.tasksProgress = new HashMap<>();
        this.tasksProgress.put("slay chickens", new int[]{-2, 3});
        this.potionBackpack = new HashMap<>();
    }

    Player(String name, int level, int health, int maxHealth, int attack,int coin, int experience,
           int experienceToLevelUp, int storyProgress, ArrayList<String> unlockedMap,
           String map, Map<String, Integer> lootBackpack,Map<String,int[]> tasksProgress,Map<String,Integer> potionBackpack) {
        this.name = name;
        this.level = level;
        this.health = health;
        this.maxHealth = maxHealth;
        this.attack = attack;
        this.coin = coin;
        this.experience = experience;
        this.experienceToLevelUp = experienceToLevelUp;
        this.storyProgress = storyProgress;
        this.unlockedMap = unlockedMap;
        this.map = map;
        this.lootBackpack = lootBackpack;
        this.tasksProgress = tasksProgress;
        this.potionBackpack = potionBackpack;
    }
    void gainExperience(int experience) {
        this.experience += experience;
        while (this.experience >= this.experienceToLevelUp) {
            this.experience -= this.experienceToLevelUp;
            this.levelUP();
        }
    }
    void levelUP() {
        this.level++;
        System.out.println("!!!Level UP!");
        this.experienceToLevelUp = (int) Math.round(this.experienceToLevelUp * 1.2);
        this.maxHealth = (int) Math.round(this.maxHealth * 1.5);
        this.health = this.maxHealth;
        this.attack = (int) Math.round(this.attack * 1.5);
    }
    void gainLoot(String lootName, int lootPrice) {
        int oldTotalPrice = this.lootBackpack.getOrDefault(lootName, 0);
        this.lootBackpack.put(lootName, oldTotalPrice + lootPrice);
    }
    void usePotion(String potionName) {
        switch (potionName) {
            case "mini health potion" -> {
                if (this.potionBackpack.get("mini health potion") > 0) {
                    this.health += 10;
                    System.out.println("You use the mini health potion.");
                    if (this.health > this.maxHealth) {
                        System.out.printf("Health overflow the max health with %d health.%n", this.health - this.maxHealth);
                        this.health = this.maxHealth;
                    }
                    System.out.printf("Now health: %d%n", this.health);
                }
                else {
                    System.out.println("You do not have this potion!");
                }
            }
        }
    }
    void die() {
        System.out.println("You lost!");
        this.map = "town";
        System.out.println("You are sent back to the town");
        this.health = (int) Math.round((double) this.maxHealth * 0.2);
        System.out.printf("Your HP has been reduced to 20% of its maximum. Current HP: %d.",this.health,this.maxHealth);
    }
}