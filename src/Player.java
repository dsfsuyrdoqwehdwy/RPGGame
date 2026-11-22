import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import constants.*;

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
    List<String> unlockedMap;
    //if finished: -1 not started:-2
    Map<String,int[]> tasksProgress;
    Map<String,Integer> potionBackpack;
    Map<String,String> settings;

    Player() {
        this("Unnamed");
    }

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
        this.unlockedMap = new ArrayList<>();
        this.unlockedMap.add(Maps.TUTORIAL);
        this.map = Maps.TUTORIAL;
        this.lootBackpack = new HashMap<>();
        this.tasksProgress = new HashMap<>();
        this.tasksProgress.put(Tasks.SLAY_CKICKENS, new int[]{Tasks.NOT_STARTED, Tasks.SLAY_CKICKENS_REQUIRE});
        this.potionBackpack = new HashMap<>();
        this.settings = new HashMap<>();
        this.settings.put(Settings.HEALTH_WARN,Settings.OFF);
        this.settings.put(Settings.HEALTH_WARN_VALUE,Constants.ZERO);
    }

    Player(String name, int level, int health, int maxHealth, int attack,int coin, int experience,
           int experienceToLevelUp, int storyProgress, ArrayList<String> unlockedMap,
           String map, Map<String, Integer> lootBackpack,Map<String,int[]> tasksProgress,Map<String,Integer> potionBackpack,
           Map<String,String> settings) {
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
        this.settings = settings;
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
            case Potions.MINI_HEALTH -> {
                if (this.potionBackpack.getOrDefault(Potions.MINI_HEALTH,0) > 0) {
                    this.health += Potions.MINI_HEALTH_REGENERATION;
                    System.out.println("You use the mini health potion.");
                    if (this.health > this.maxHealth) {
                        System.out.printf("Health overflow the max health with %d health.%n", this.health - this.maxHealth);
                        this.health = this.maxHealth;
                        this.potionBackpack.put(Potions.MINI_HEALTH,this.potionBackpack.getOrDefault(Potions.MINI_HEALTH,0)-1);
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
        if (this.unlockedMap.contains(Maps.TOWN)) {
            this.map = Maps.TOWN;
            System.out.println("You are sent back to the town");
        }
        else {
            this.map = Maps.TUTORIAL;
            System.out.println("You are sent back to the tutorial");
        }
        this.health = (int) Math.round((double) this.maxHealth * 0.2);
        System.out.printf("Your HP has been reduced to 20%% of its maximum. Current HP: %d.%n", this.health, this.maxHealth);
    }
}