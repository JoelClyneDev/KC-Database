package CardTypes;
/**
 * the base class for a yugioh card
 * also serves as an one unit of a room object
 * the @'s below are the values it will have
 */


public class YuGiOhCard {

    public enum MainCardTypes{
        MONSTER("MONSTER"),
        SPELL("SPELL"),
        TRAP("TRAP");

        private String value;
        MainCardTypes(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }

        public static MainCardTypes getEnumValue(String string){
            for (MainCardTypes main : MainCardTypes.values()){
                if (main.getValue().equals(string)){
                    return main;
                }
            }
            return null;
        }

    }

    private String name;
    private MainCardTypes type;
    private String effect;

    //the name of the pack of the card
    private String pack;

    //the id number of the card (including the pack abbreviation
    private String id;
    //worry about this later
    private String cardImagePath;

    public YuGiOhCard(String name, MainCardTypes type,  String effect, String pack, String id, String cardImagePath){
        this.name = name;
        this.type = type;
        this.effect = effect;
        this.pack = pack;
        this.id = id;
        this.cardImagePath = cardImagePath;
    }

    public MainCardTypes getType() {
        return type;
    }

    public String getEffect() {
        return effect;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPack() {
        return pack;
    }

    public String getCardImagePath() {
        return cardImagePath;
    }

    public void setCardImagePath(String cardImagePath) {
        this.cardImagePath = cardImagePath;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setType(MainCardTypes type) {
        this.type = type;
    }
}


