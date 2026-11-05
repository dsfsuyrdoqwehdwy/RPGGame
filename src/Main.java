import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import static java.lang.Math.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

//TODO:
// add arrayList : repeatableTaskProgress
// armors and weapons
// add in "settings":when health lower than user input value,alert the user with "!!!Your health is below the value"

public class Main {
    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean ifInLoopPregame = true;
        Player player = null;
        System.out.println("Welcome to RPGGame!");
        do {
            String answer;

            switch (dialogueMenu(new String[]{
                    "new game",
                    "load a save",
            })) {
                case -1 -> {
                    return;
                }
                //new game
                case 1 -> {
                    System.out.println("Enter your name(no spaces)");
                    System.out.println("Type 'q' to exit");
                    System.out.print("> ");
                    answer = scanner.nextLine();
                    if (!answer.equalsIgnoreCase("q")) {
                        player = new Player(answer);
                        ifInLoopPregame = false;
                    }
                }
                //load save
                case 2 -> {
                    System.out.println("Enter your save name(no spaces)");
                    System.out.println("Type 'q' to exit");
                    System.out.print("> ");
                    answer = scanner.nextLine();
                    if (!answer.equalsIgnoreCase("q")) {
                        String fileName = answer + ".txt";
                        try {
                            player = loadSave(fileName);
                            ifInLoopPregame = false;
                            System.out.println("Game loaded successfully!");
                        } catch (IOException | NumberFormatException e) {
                            System.out.println("Failed to load save: " + e.getMessage());
                        }
                    }
                }
            }
        } while (ifInLoopPregame);

        if (player.storyProgress == 1) {
            preGameStory(player.name);
            player.tasksProgress.put("slay chickens",new int[]{0,3});
            player.storyProgress++;
        }

        while (true) {
            Helper.printEqualsLine();
            System.out.println("Main menu");
            switch (dialogueMenu(new String[]{
                    "find and fight with a mob",
                    "print your stats",
                    "check backpack",
                    "change map",
                    "try to fight with the boss of this map",
                    "check tasks",
                    "talk with NPCs",
                    "open shops",
                    "open settings",
            })) {
                //quit
                case -1 -> {
                    return;
                }
                // find and fight with a mob
                case 1 -> {
                    fightMob(player);
                }
                // print your stats
                case 2 -> {
                    printStats(player);
                }
                // check backpack
                case 3 -> {
                    checkBackpack(player);
                }
                // change map
                case 4 -> {
                    System.out.println("Coming soon");
                }
                // try to fight with the boss of this map
                case 5 -> {
                    fightBoss(player);
                }
                // check tasks
                case 6 -> {
                    checkTasks(player);
                }
                //talk with NPC
                case 7 -> {
                    talkNPC(player);
                }
                //shop
                case 8 -> {
                    shop(player);
                }
                // open settings
                case 9 -> {
                    player = settings(player);
                }
            }
        }
    }

    static void timeSleep(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static int dialogueMenu(String[] option) {
        try {
            while (true) {
                Helper.printHyphenLine();
                System.out.println("Select an action you want to perform.");
                System.out.println();
                for (int i = 0; i < option.length; i++) {
                    System.out.printf("%d. %s%n", i + 1, option[i]);
                }
                System.out.println("Type 'q' to exit");
                System.out.print("> ");
                String choiceString = scanner.nextLine().trim();
                int choice;
                if (choiceString.equalsIgnoreCase("q")) {
                    return -1;
                }
                try {
                    choice = Integer.parseInt(choiceString);
                } catch (NumberFormatException e) {
                    Helper.printHyphenLine();
                    System.out.println("Enter a number!");
                    continue;
                }
                if (choice > 0 && choice <= option.length) {
                    return choice;
                } else {
                    Helper.printHyphenLine();
                    System.out.println("Enter a number between 1 and " + (option.length));
                }
            }
        }
        finally {
            Helper.printHyphenLine();
        }
    }

    static Player fightBoss(Player player) {
        Enemy boss;
        switch (player.map) {
            case "tutorial" -> {
                boss = new Enemy("big chicken",10,100,1,1000,1000,new ArrayList<>(List.of("boss")));
            }
            case "town" -> {
                System.out.println("There is no boss here.");
                return player;
            }
            case "cave" -> {
                System.out.println("Coming soon");
                return player;
            }
            default -> {
                System.out.println("Invalid map!");
                return player;
            }
        }
        fightMob(player,boss);
        return player;
    }

    static void checkTasks(Player player) {
        System.out.println("Tasks");
        System.out.println();
        for (Map.Entry<String, int[]> entry : player.tasksProgress.entrySet()) {
            if (entry.getValue()[0] >= 0) {
                System.out.printf("%s : %d/%d%n", entry.getKey(), entry.getValue()[0],entry.getValue()[1]);
            }
            else if (entry.getValue()[0] == -1) {
                System.out.printf("%s : finished%n", entry.getKey());
            }
        }
    }

    static Player shop(Player player) {
        System.out.println("Shop");
        System.out.println();
        System.out.println("1 - mini health potion(10 health regeneration)(50 coins)");
        System.out.println("q - quit the dialogue");
        System.out.print("> ");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("q")) {
            return player;
        }
        int answerAction = Integer.parseInt(answer);
        Helper.printHyphenLine();
        System.out.println("How many items you want buy");
        System.out.println("q - quit the dialogue");
        System.out.print("> ");
        String countString = scanner.nextLine();
        if (countString.equalsIgnoreCase("q")) {
            return player;
        }
        int count = Integer.parseInt(countString);
        Helper.printHyphenLine();
        switch (answerAction) {
            case 1 -> {
                if (player.coin > count * 50) {
                    player.coin -= count * 50;
                    player.potionBackpack.put("mini health potion",player.potionBackpack.getOrDefault("mini health potion",0) + count);
                    System.out.printf("You buy the %d mini health potion with %d coins.%n",count,count * 50);
                    System.out.printf("Now you have %d coins left.%n",player.coin);
                }
                else {
                    System.out.println("You do not have enough money!");
                }
            }
            default -> {
                System.out.println("Enter a right number!");
            }
        }
        return player;
    }

    static Player settings(Player player) {
        System.out.println("Settings");
        Helper.printHyphenLine();
        System.out.println("1 - saves");
        System.out.println("2 - change logs");
        System.out.println("q - quit the dialogue");
        System.out.print("> ");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("q")) {
            return player;
        }
        int answerAction = Integer.parseInt(answer);
        Helper.printHyphenLine();
        switch (answerAction) {
            case 1 -> {
                System.out.println("1 - save your games");
                System.out.println("2 - load a save");
                System.out.println("q - quit the dialogue");
                System.out.print("> ");
                String answerSave = scanner.nextLine();
                if (answerSave.equalsIgnoreCase("q")) {
                    return player;
                }
                int answerSaveAction = Integer.parseInt(answerSave);
                switch (answerSaveAction) {
                    case 1 -> { //save your game
                        System.out.println("Enter save name(no spaces):");
                        System.out.print("> ");
                        String saveName = scanner.nextLine() + ".txt";
                        try {
                            saveGame(player, saveName);
                            System.out.println("Game saved to " + saveName);
                        } catch (IOException e) {
                            System.out.println("Failed to save: " + e.getMessage());
                        }
                    }
                    case 2 -> { //load a save
                        System.out.println("Enter your save name(no spaces):");
                        System.out.print("> ");
                        String fileName = scanner.nextLine() + ".txt";
                        try {
                            player = loadSave(fileName);
                            System.out.println("Game loaded successfully!");
                        } catch (IOException | NumberFormatException e) {
                            System.out.println("Failed to load save: " + e.getMessage());
                        }
                    }
                    default -> {
                        System.out.println("Enter a right number!");
                    }
                }
            }
            case 2 -> {
                printChangeLogs();
                }
            default -> {
                System.out.println("Enter a right number!");
            }
        }
        return player;
    }

    static void printChangeLogs() {
        System.out.println("Change logs");
        Helper.printHyphenLine();
        System.out.println("v0.1.0 10/29/2026");
        System.out.println();
        System.out.println("1.Add change logs");
        System.out.println("2.Load saves and save games");
        System.out.println("3.Function of fight with mobs");
        System.out.println("4.Backpacks");
        System.out.println("5.Level of player");
        System.out.println("6.Sell loots");
        System.out.println("7.Pregame story");
        System.out.println("8.Talk with NPCs");
        System.out.println("9.Basic structures");
        System.out.println("10.change the format of tasksProgress");
        Helper.printHyphenLine();
        System.out.println("v0.1.1 11/1/2026");
        System.out.println();
        System.out.println("1.Add time sleep between each line of fight mobs and pregame story");
    }
    static Player talkNPC(Player player) {
        System.out.println("Choose an NPC to talk");
        System.out.println("Type 'q' to exit");
        Helper.printHyphenLine();

        switch (player.map) {
            case "tutorial" -> {
                List<String> dialogueMenuNPC =  new ArrayList<>();
                if (player.storyProgress == 1) {
                    System.out.println("You have not unlocked this");
                    return player;
                }
                System.out.println("Choose an NPC to talk");
                if (player.storyProgress >= 2) {
                    dialogueMenuNPC.add("Strawen");
                }
                int choiceNPC = dialogueMenu(dialogueMenuNPC.toArray(new String[0]));
                int choiceAction;
                switch (choiceNPC) {
                    case 1 -> {
                            System.out.printf("Welcome %s.%n",player.name);
                            List<String> dialogueMenu = new ArrayList<>(Arrays.asList("ask about the task of slaying chickens"));
                            if (player.storyProgress >= 3) {
                                dialogueMenu.add("ask about the task of beat the boss");
                            }
                            choiceAction = dialogueMenu(dialogueMenu.toArray(new String[0]));
                            switch (choiceAction) {
                                case -1 -> {
                                    return player;
                                }
                                case 1 -> {
                                    if (player.tasksProgress.get("slay chickens")[0] < player.tasksProgress.get("slay chickens")[1] &&
                                            player.tasksProgress.get("slay chickens")[0] >= 0) {
                                        System.out.println("You have not finished this yet.");
                                        System.out.printf("You have %d left.%n",
                                                player.tasksProgress.get("slay chickens")[1] - player.tasksProgress.get("slay chickens")[0]);
                                    }
                                    else if (player.tasksProgress.get("slay chickens")[0] == player.tasksProgress.get("slay chickens")[1]) {
                                        System.out.println("Congratulations! You have finished the task.");
                                        System.out.println("There are more tasks in the future");
                                        System.out.println("Here are the rewards for you.");
                                        Helper.printHyphenLine();
                                        System.out.println("You got 50 experiences!");
                                        player.gainExperience(50);
                                        player.tasksProgress.get("slay chickens")[0] = -1;
                                        player.storyProgress++;
                                        System.out.println();
                                        timeSleep(1000);
                                        System.out.println("This seems you have enough strength");
                                        timeSleep(1000);
                                        System.out.println("Now here is a new task for you");
                                        timeSleep(1000);
                                        System.out.println("Try to beat the boss of this map");
                                        timeSleep(1000);
                                        System.out.println("The big chicken...");
                                        timeSleep(1000);
                                        System.out.println("=== QUEST RECEIVED: Kill the big chicken! ===");
                                        player.tasksProgress.put("kill big chicken",new int[]{0,1});
                                        timeSleep(1000);
                                    }
                                    else if (player.tasksProgress.get("slay chickens")[0] == -1) {
                                        System.out.println("You have finished this task yet.");
                                    }
                                }
                                case 2 -> {
                                    if (player.tasksProgress.get("kill big chicken")[0] >= 0 && player.tasksProgress.get("kill big chicken")[0] < player.tasksProgress.get("slay chickens")[1]) {
                                        System.out.println("You have not finished this yet.");
                                        System.out.println("Go to 'try to fight with the boss of this map'");
                                    }
                                    else if (player.tasksProgress.get("kill big chicken")[0] == player.tasksProgress.get("slay chickens")[1]) {
                                        System.out.println("Congratulations! You have finished the task.");
                                        System.out.println("Now you unlocked the town");
                                        System.out.println("try the 'change map'");
                                        player.unlockedMap.add("town");
                                        player.storyProgress++;
                                        player.tasksProgress.get("kill big chicken")[0] = -1;
                                    }
                                    else if (player.tasksProgress.get("slay chickens")[0] == -1) {
                                        System.out.println("You have finished this task yet.");
                                    }
                                }
                            }
                    }
                }
            }

            default -> System.out.println("There are no NPCs here.");
        }

        return player;
    }

    static void preGameStory(String name) {
        String[] story = {
                "In the year 3024, the world was consumed by the Great Static.",
                "Reality glitched. The land of Varosia was shattered into maps,",
                "and the creatures within it -- corrupted by wild code -- went mad.",
                "-",
                "Even the chickens. ESPECIALLY the chickens.",
                "-",
                "You wake up on a grassy field, head aching, memory blank.",
                "The only thing you remember is your own name: " + name + ".",
                "",
                "An old scarecrow named Strawen creaks to life and speaks:",
                "",
                "  \"Ah... another wanderer pulled out of the Static.\"",
                "  \"Welcome to the Tutorial Fields, " + name + ".\"",
                "  \"Beyond this fence lies chaos -- cursed pigs, glitched beasts,",
                "   and at the end of every map, a Boss who guards the way forward.\"",
                "",
                "  \"You look weak. Pathetic, even. Level 1? Ha!\"",
                "  \"Start small. Fight the chickens. Collect their loot.\"",
                "  \"Grow strong enough...\"",
                "",
                "  \"Oh, and one more thing, wanderer...\"",
                "  \"Never trust a chicken...\"",
                "=",
                "=== QUEST RECEIVED: Slay 3 chickens! ===",
                "=== The Tutorial Fields are now open. Good luck. ==="
        };

        Helper.printEqualsLine();

        for (String line : story) {
            if (line.equals("=")) {
                Helper.printEqualsLine();
            } else if (line.equals("-")) {
                Helper.printHyphenLine();
            } else {
                System.out.println(line);
            }

            timeSleep(500);
        }
    }

    static Player checkBackpack(Player player) {
        System.out.println("Loots backpack");
        System.out.println();
        printLootBackpack(player.lootBackpack);
        Helper.printHyphenLine();
        System.out.println("Potions backpack");
        System.out.println();
        printPotionBackpack(player.potionBackpack);
        switch (dialogueMenu(new String[]{
                "sell loots",
                "set armors and weapons",
                "use potions",
        })) {
            case 1 -> {
                player = sellLoots(player);
            }
            case 2 -> {
                System.out.println("Coming soon.");
            }
            case 3 -> {
                player = usePotions(player);
            }
            default -> {
                System.out.println("Invalid choice.");
            }
        }
        return player;
    };

    static Player usePotions(Player player) {
        if (player.potionBackpack.isEmpty()) {
            System.out.println("Your potion backpack is empty.");
            return player;
        }
        System.out.println("Potion backpack");
        System.out.println();
        printPotionBackpack(player.potionBackpack);
        Helper.printHyphenLine();
        System.out.println("Type potion's name to use");
        System.out.println("Type 'q' to quit");
        System.out.print("> ");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("q")) {
            return player;
        }
        Helper.printHyphenLine();
        boolean ifInPotionBackpack = false;
        for (String name : player.potionBackpack.keySet()) {
            if (name.equals(answer)) {
                ifInPotionBackpack = true;
                break;
            }
        }
        if (ifInPotionBackpack) {
            player.usePotion(answer);
        }
        else {
            System.out.println("Potion not in backpack!");
        }
        return player;
    }

    static void printPotionBackpack(Map<String,Integer> potionBackpack) {
        if (potionBackpack.isEmpty()) {
            System.out.println("Your potion backpack is empty");
            return;
        }
        for (Map.Entry<String,Integer> entry : potionBackpack.entrySet()) {
            System.out.printf("%s: %d%n",entry.getKey(),entry.getValue());
        }
    }

    static Player sellLoots(Player player) {
        if (player.lootBackpack.isEmpty()) {
            System.out.println("Your loot backpack is empty");
            return player;
        }
        System.out.println("Loots backpack");
        System.out.println();
        printLootBackpack(player.lootBackpack);
        Helper.printHyphenLine();
        System.out.println("Choose one to sell");
        System.out.println("Type 'all' to sell all");
        System.out.println("Type 'q' to exit");
        System.out.print("> ");
        String answerSell = scanner.nextLine();
        if (answerSell.equalsIgnoreCase("q")) {
            return player;
        }
        Helper.printHyphenLine();
        if (answerSell.equalsIgnoreCase("all")) {
            for (int price : player.lootBackpack.values()) {
                player.coin += price;
            }
            player.lootBackpack.clear();
            System.out.println("You sold all.");
            System.out.printf("Now you have %d coins.%n",player.coin);
            return player;
        }
        boolean ifInBackpack = false;
        for (String k : player.lootBackpack.keySet()) {
            if (answerSell.equals(k)) {
                ifInBackpack = true;
                break;
            }
        }
        if (ifInBackpack) {
            int price = player.lootBackpack.remove(answerSell);
            player.coin += price;
            System.out.printf("You sold %s with %d coins.%n",answerSell,price);
            System.out.printf("Now you have %d coins.%n",player.coin);
        }
        else {
            System.out.printf("%s not in backpack!%n",answerSell);
        }
        return player;
    }

    static void printLootBackpack(Map<String,Integer> backpack) {
        if (backpack.isEmpty()) {
            System.out.println("Your loot backpack is empty");
            return;
        }
        //System.out.println("Your loot backpack");
        for (Map.Entry<String,Integer> entry : backpack.entrySet()) {
            System.out.printf("%s: %d coins%n",entry.getKey(),entry.getValue());
        }
    }

    static Player fightMob(Player player) {
        Enemy enemy = generateMobs(player);
        return fightMob(player,enemy);
    }

    static Player fightMob(Player player,Enemy enemy) {
        System.out.printf("%s VS %s%n", player.name, enemy.name);
        System.out.printf("Your health: %d ; Enemy health: %d%n", player.health, enemy.health);
        System.out.printf("Your attack: %d ; Enemy attack: %d%n", player.attack, enemy.attack);
        Helper.printHyphenLine();
        System.out.println("Fighting start!");
        do {
            Helper.printHyphenLine();
            timeSleep(100);
            enemy.health -= player.attack;
            System.out.printf("%s attack %s with %d damage! Now %s has %d health left!%n",
                    player.name, enemy.name, player.attack, enemy.name, enemy.health);
            if (enemy.health <= 0) {
                break;
            }
            timeSleep(200);
            player.health -= enemy.attack;
            System.out.printf("%s attack %s with %d damage! Now %s has %d health left!%n",
                    enemy.name, player.name, enemy.attack, player.name, player.health);
            timeSleep(200);
        } while (player.health > 0);

        if (enemy.health <= 0) {
            System.out.printf("%s win!%n", player.name);

            String lootName = generateLootsName();
            player.gainLoot(lootName, enemy.lootPrice);

            player.gainExperience(enemy.experience);

            if (enemy.name.equals("chicken") &&
                    0 <= player.tasksProgress.get("slay chickens")[0] &&
                    player.tasksProgress.get("slay chickens")[0] < player.tasksProgress.get("slay chickens")[1]) {
                player.tasksProgress.get("slay chickens")[0] += 1;
            }
            else if (enemy.name.equals("big chicken") && enemy.ifHasTag("boss") &&
            0 <= player.tasksProgress.get("kill big chicken")[0] &&
            player.tasksProgress.get("kill big chicken")[0] < player.tasksProgress.get("kill big chicken")[1]) {
                player.tasksProgress.get("kill big chicken")[0] += 1;
            }
        } else {
            player.die();
        }
        return player;
    }

    static Enemy generateMobs(Player player) { // Enemy(name,level,health,attack,experience,lootPrice,ifBoss)
        switch (player.map) {
            case "tutorial" -> {
                int randomMob = random.nextInt(1, 1001);
                if (randomMob <= 999) {
                    return new Enemy("chicken", 1, 5, 0, 5, 10 + random.nextInt(10), new ArrayList<>());
                } else {
                    return new Enemy("pig", 2, 10, 1, 100 + random.nextInt(50), 150 + random.nextInt(20), new ArrayList<>());
                    }

            }
            case "cave" -> {
                return null;
            }
            default -> {
                throw new IllegalArgumentException("Unknown map: " + player.map);
            }
        }
    }

    static void printStats(Player player) {
        System.out.println("Your stats");
        Helper.printHyphenLine();
        System.out.printf("Now health: %d%n", player.health);
        System.out.printf("Max health: %d%n", player.maxHealth);
        System.out.printf("Attack: %d%n", player.attack);
        Helper.printHyphenLine();
        System.out.printf("Level: %d%n", player.level);
        System.out.printf("Experience: %d%n", player.experience);
        System.out.printf("Experience to level up: %d%n", player.experienceToLevelUp);
        System.out.printf("Experience left to level up: %d%n",player.experienceToLevelUp-player.experience);
        Helper.printHyphenLine();
        System.out.printf("Now map: %s%n", player.map);
        System.out.printf("Unlocked map: %s%n", String.join(",", player.unlockedMap));
        Helper.printHyphenLine();
        System.out.printf("Loots in backpack: %d items%n", player.lootBackpack.size());
    }

    static String generateLootsName() {
        String[] adjList = {
                "old", "new", "golden", "silver", "bronze",
                "iron", "steel", "wooden", "stone", "bone",
                "obsidian", "marble", "glass", "leather", "velvet",
                "broken", "rusty", "cracked", "shiny", "polished",
                "gleaming", "glowing", "dusty", "ancient", "pristine",
                "frozen", "burning", "smoky", "stormy", "thunder",
                "volcanic", "icy", "poisoned", "venomous", "shadowy",
                "scarlet", "emerald", "crystal", "twisted", "dragon",
                "ghostly", "phantom", "bloody", "royal", "engraved"
        };

        String[] nameList = {
                "potion", "gem", "amulet", "crown",
                "lantern", "grimoire", "scroll", "tome", "idol", "relic",
                "chalice", "key", "compass", "hourglass", "bell",
                "feather", "fang", "skull", "shard", "seed",
                "coin", "map", "mirror", "candle", "torch",
                "vial", "flask", "chest", "urn", "statue",
                "totem", "charm", "bandage", "herb", "root",
                "flower", "leaf", "mushroom", "crystal", "ore",
                "ingot", "pearl", "bone", "tooth", "claw",
                "scale", "hide", "silk", "rune", "sigil"
        };

        return adjList[random.nextInt(adjList.length)] + " " + adjList[random.nextInt(adjList.length)] + " " + nameList[random.nextInt(nameList.length)];
    }

    static Player loadSave(String fileName) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String playerName = br.readLine();
            int playerHealth = Integer.parseInt(br.readLine());
            int playerMaxHealth = Integer.parseInt(br.readLine());
            int playerAttack = Integer.parseInt(br.readLine());
            int playerCoin = Integer.parseInt(br.readLine());
            int playerExperience = Integer.parseInt(br.readLine());
            int playerExperienceToLevelUp = Integer.parseInt(br.readLine());
            int playerLevel = Integer.parseInt(br.readLine());
            String playerMap = br.readLine();
            int playerStoryProgress = Integer.parseInt(br.readLine());
            ArrayList<String> playerUnlockedMap = new ArrayList<>(Arrays.asList(br.readLine().split(",")));

            HashMap<String, Integer> playerLootBackpack = new HashMap<>();
            String line = br.readLine();
            if (line != null && !line.isBlank()) {
                for (String item : line.split(";")) {
                    String[] parts = item.split("/", 2);
                    String name = parts[0];
                    int totalPrice = Integer.parseInt(parts[1]);
                    playerLootBackpack.put(name, totalPrice);
                }
            }

            /*
            ArrayList<String> playerTasksProgressString = new ArrayList<>(Arrays.asList(br.readLine().split(",")));
            ArrayList<Integer> playerTasksProgress = new ArrayList<>();
            for (String item : playerTasksProgressString){
                playerTasksProgress.add(Integer.parseInt(item));
            }
            */

            //taskName,progress needProgress;taskName2,progress2 needProgress2
            //if finished: -1 not started:-2
            Map<String, int[]> playerTasksProgress = new HashMap<>();
            String tasksLine = br.readLine();
            if (tasksLine != null && !tasksLine.isBlank()) {
                for (String item : tasksLine.split(";")) {
                    String[] parts = item.split(",", 2);
                    if (parts.length == 2) {
                        String taskName = parts[0].trim();
                        String[] progressPart = parts[1].split(" ");
                        int[] progress = {Integer.parseInt(progressPart[0].trim()),Integer.parseInt(progressPart[1].trim())};
                        if (!taskName.isEmpty()) {
                            playerTasksProgress.put(taskName, progress);
                        }
                    }
                }
            }

            //potionBackpack: name,count/name,count
            Map<String,Integer> playerPotionBackpack = new HashMap<>();
            String potionBackpackLine = br.readLine();
            if (potionBackpackLine != null && !potionBackpackLine.isBlank()) {
                for (String item : potionBackpackLine.split(";")) {
                    String[] parts = item.split(",", 2);
                    if (parts.length == 2) {
                        String potionName = parts[0].trim();
                        int count = Integer.parseInt(parts[1].trim());
                        if (!potionName.isEmpty()) {
                            playerPotionBackpack.put(potionName, count);
                        }
                    }
                }
            }

            return new Player(playerName, playerLevel, playerHealth, playerMaxHealth, playerAttack,playerCoin,
                    playerExperience, playerExperienceToLevelUp, playerStoryProgress,
                    playerUnlockedMap, playerMap, playerLootBackpack,playerTasksProgress,playerPotionBackpack);
        }
    }

    static void saveGame(Player player, String fileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(player.name);
            bw.newLine();
            bw.write(String.valueOf(player.health));
            bw.newLine();
            bw.write(String.valueOf(player.maxHealth));
            bw.newLine();
            bw.write(String.valueOf(player.attack));
            bw.newLine();
            bw.write(String.valueOf(player.coin));
            bw.newLine();
            bw.write(String.valueOf(player.experience));
            bw.newLine();
            bw.write(String.valueOf(player.experienceToLevelUp));
            bw.newLine();
            bw.write(String.valueOf(player.level));
            bw.newLine();
            bw.write(player.map);
            bw.newLine();
            bw.write(String.valueOf(player.storyProgress));
            bw.newLine();
            bw.write(String.join(",", player.unlockedMap));
            bw.newLine();

            // lootBackpack: name/totalPrice;name2/totalPrice2
            StringBuilder lootLine = new StringBuilder();
            for (Map.Entry<String, Integer> entry : player.lootBackpack.entrySet()) {
                if (lootLine.length() > 0) {
                    lootLine.append(";");
                }
                lootLine.append(entry.getKey()).append("/").append(entry.getValue());
            }
            bw.write(lootLine.toString());
            bw.newLine();

            /*
            //tasksProgress: task1progress,task2progress
            List<String> tasksProgressString = new ArrayList<>();
            for (int v : player.tasksProgress) {
                tasksProgressString.add(String.valueOf(v));
            }
            bw.write(String.join(",", tasksProgressString));
            bw.newLine();
             */

            // tasksProgress: taskName,progress;taskName2,progress2
            StringBuilder tasksLine = new StringBuilder();
            for (Map.Entry<String, int[]> entry : player.tasksProgress.entrySet()) {
                if (tasksLine.length() > 0) {
                    tasksLine.append(";");
                }
                tasksLine.append(entry.getKey()).append(",").append(entry.getValue()[0]).append(" ").append(entry.getValue()[1]);
            }
            bw.write(tasksLine.toString());
            bw.newLine();

            //potionBackpack: potionName,count;potionName,count
            StringBuilder potionLine = new StringBuilder();
            for (Map.Entry<String, Integer> entry : player.potionBackpack.entrySet()) {
                if (potionLine.length() > 0) {
                    tasksLine.append(";");
                }
                tasksLine.append(entry.getKey()).append(",").append(entry.getValue());
            }
            bw.write(tasksLine.toString());
            bw.newLine();
        }
    }
}