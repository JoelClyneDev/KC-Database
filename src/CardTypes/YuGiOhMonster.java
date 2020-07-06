package CardTypes;

public class YuGiOhMonster extends YuGiOhCard {

    public enum Attributes{
        FIRE("FIRE"),
        WATER("WATER"),
        WIND("WIND"),
        EARTH("EARTH"),
        LIGHT("LIGHT"),
        DARK("DARK"),
        DIVINE("DIVINE");

        private String value;

        Attributes(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }

        public Attributes getAttributeValue(String query){
            for (Attributes attribute : Attributes.values()){
                if (attribute.getValue().equals(query)){
                    return attribute;
                }
            }
            return null;
        }

        public static boolean isInAttributes(String query){
            for (Attributes attribute : Attributes.values()){
                if (attribute.getValue().equals(query)){
                    return true;
                }
            }
            return false;
        }
    }



    public enum MonsterSummoningType {
        NORMAL("Normal"),
        EFFECT("Effect"),
        RITUAL("Ritual"),
        FUSION("Fusion"),
        SYNCHRO("Synchro"),
        XYZ("Xyz"),
        LINK("Link");

        private String deepType;

        MonsterSummoningType(String deepType){
            this.deepType = deepType;

        }

        public static boolean isInDeepMonsterTypes(String query){
            for (MonsterSummoningType deepMonsterType : MonsterSummoningType.values()){
                if (deepMonsterType.getDeepType().equals(query)){
                    return true;
                }
            }
            return false;
        }

        public String getDeepType() {
            return deepType;
        }
    }

    public enum MonsterTypes{
        AQUA("Aqua"),
        BEAST("Beast"),
        BEAST_WARRIOR("Beast-Warrior"),
        CYBERSE("Cyberse"),
        DINOSAUR("Dinosaur"),
        DIVINE_BEAST("Divine-Beast"),
        DRAGON("Dragon"),
        FAIRY("Fairy"),
        FIEND("Fiend"),
        FISH("Fish"),
        INSECT("Insect"),
        MACHINE("Machine"),
        PLANT("Plant"),
        PSYCHIC("Psychic"),
        PYRO("Pyro"),
        REPTILE("Reptile"),
        ROCK("Rock"),
        SEA_SERPENT("Sea Serpent"),
        SPELLCASTER("Spellcaster"),
        THUNDER("Thunder"),
        WARRIOR("Warrior"),
        WINGED_BEAST("Winged Beast"),
        WYRM("Wyrm"),
        ZOMBIE("Zombie");
        /**

         <item>SpellCaster</item>
         <item>Thunder</item>
         <item>Warrior</item>
         <item>Winged Beast</item>
         <item>Wyrm</item>
         <item>Zombie</item>
         */

        private String monsterType;

        MonsterTypes(String monsterType){
            this.monsterType = monsterType;
        }

        public String getMonsterType() {
            return monsterType;
        }

        public static MonsterTypes getMonsterTypeClassValue(String query){
            for (MonsterTypes monsterTypes : MonsterTypes.values()){
                if (monsterTypes.getMonsterType().equals(query)){
                    return monsterTypes;
                }
            }
            return null;
        }

    }
    private int level;
    private MonsterTypes monsterType;
    private boolean effectStatus;
    private Attributes attribute;
    private MonsterSummoningType monsterSummoningType;
    private int attack;
    private int defense;
    private boolean pendulum;
    private String pendulumEffect;
    private int pendulumScale;


    //non summoning, non pendulum

    public YuGiOhMonster(String name, MainCardTypes type, String effect, String pack, String id, String cardImagePath, MonsterTypes monsterType, int level, int attack, int defense, boolean effectStatus, Attributes attribute) {
        super(name, type, effect, pack, id, cardImagePath);
        this.monsterType = monsterType;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.effectStatus = effectStatus;
        pendulum = false;
        this.attribute = attribute;


        //the constructor for pendulum monsters
    }

    public YuGiOhMonster(String name, MainCardTypes type, String effect, String pack, String id, String cardImagePath, MonsterTypes monsterType, int level, int attack, int defense, boolean effectStatus, Attributes attribute, String pendulumEffect, int pendulumScale){
        super(name, type, effect, pack, id, cardImagePath);
        this.monsterType = monsterType;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.effectStatus = effectStatus;
        pendulum = true;
        this.pendulumEffect = pendulumEffect;
        this.pendulumScale = pendulumScale;
        this.attribute = attribute;
    }

    //summoning, non pendulum
    public YuGiOhMonster(String name, MainCardTypes type, String effect, String pack, String id, String cardImagePath, MonsterTypes monsterType, int level, int attack, int defense, boolean effectStatus, Attributes attribute, MonsterSummoningType deepMonsterType) {
        super(name, type, effect, pack, id, cardImagePath);
        this.monsterType = monsterType;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.effectStatus = effectStatus;
        pendulum = false;
        this.attribute = attribute;
        monsterSummoningType = deepMonsterType;


        //the constructor for pendulum monsters

    }



    //summoning, pendulum
    public YuGiOhMonster(String name, MainCardTypes type, String effect, String pack, String id, String cardImagePath, MonsterTypes monsterType, int level, int attack, int defense, boolean effectStatus, Attributes attribute, String pendulumEffect, int pendulumScale, MonsterSummoningType deepMonsterType){
        super(name, type, effect, pack, id, cardImagePath);
        this.monsterType = monsterType;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.effectStatus = effectStatus;
        pendulum = true;
        this.pendulumEffect = pendulumEffect;
        this.pendulumScale = pendulumScale;
        this.attribute = attribute;
        monsterSummoningType = deepMonsterType;
    }

    public MonsterTypes getMonsterTypeClassValue(String query){
        for (MonsterTypes monsterTypes : MonsterTypes.values()){
            if (monsterTypes.getMonsterType().equals(query)){
                return monsterTypes;
            }
        }
        return null;
    }

    //have view interpret -1, and -2
    public int getAttack() {
        return attack;
    }
    //have view interpret -1, and -2
    public int getDefense() {
        return defense;
    }

    public int getLevel() {
        return level;
    }

    public Attributes getAttribute() {
        return attribute;
    }

    public boolean isPendulum() {
        return pendulum;
    }

    public boolean isEffectStatus() {
        return effectStatus;
    }

    public int getPendulumScale() {
        return pendulumScale;
    }

    public MonsterSummoningType getMonsterSummoningType() {
        return monsterSummoningType;
    }

    public MonsterTypes getMonsterType() {
        return monsterType;
    }

    public String getPendulumEffect() {
        return pendulumEffect;
    }

    public void setMonsterSummoningType(MonsterSummoningType monsterSummoningType) {
        this.monsterSummoningType = monsterSummoningType;
    }

    public void setPendulum(boolean pendulum) {
        this.pendulum = pendulum;
    }

    public void setLevel(int level){
        this.level = level;
    }

    public void setAttack(int attack){
        this.attack = attack;
    }
    public void setDefense(int def){
        this.defense = def;
    }
    public void setPendulumScale(int scale){
        this.pendulumScale = scale;
    }

    public void setAttribute(Attributes attribute) {
        this.attribute = attribute;
    }

    public void setEffectStatus(boolean effectStatus) {
        this.effectStatus = effectStatus;
    }

    public void setMonsterType(MonsterTypes monsterType) {
        this.monsterType = monsterType;
    }

    public void setPendulumEffect(String pendulumEffect) {
        this.pendulumEffect = pendulumEffect;
    }

    public void showAllAttributes(){
        System.out.println(super.getName());
        System.out.println(super.getEffect());
        System.out.println(super.getId());
        System.out.println(super.getPack());
        System.out.println(super.getType());
        System.out.println(getLevel());

        //parse the weird attack and defense values
        if (getAttack() == -1){
            System.out.println("?");
        }
        else if (getAttack() == -2){
            System.out.println("-");
        }
        else {
            System.out.println(getAttack());

        }
        if (getDefense() == -1){
            System.out.println("?");
        }
        else if (getDefense() == -2){
            System.out.println("-");
        }
        else {
            System.out.println(getDefense());
        }
        System.out.println(getDefense());
        System.out.println(getMonsterType().getMonsterType());
        System.out.println(getAttribute().getValue());
        System.out.println("Pendulum = " +  isPendulum());
        System.out.println("Effect Monster = " + isEffectStatus());
        if (isPendulum()){
            System.out.println(pendulumEffect);
            System.out.println(pendulumScale);
        }
    }



}
