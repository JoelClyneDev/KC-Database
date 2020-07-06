package CardTypes;


public class YuGiOhSpellTrap extends YuGiOhCard {
    /**
     * The types of spell and trap cards, they'll be shared between spell and trap cards, even though you cant have a quick-play trap
     */
    public enum DeepSpellTrapTypes{
        NORMAL("Normal"),
        QUICK_PLAY("Quick-Play"),
        CONTINUOUS("Continuous"),
        EQUIP("Equip"),
        FIELD("Field");


        private String spellTrapType;
        DeepSpellTrapTypes(String spellTrapType){
            this.spellTrapType = spellTrapType;
        }

        public String getSpellTrapType() {
            return spellTrapType;
        }

        /**
         * gets the enum from the string
         * @param query
         * @return
         */
        public static YuGiOhSpellTrap.DeepSpellTrapTypes getSpellTrapTypeClassValue(String query){
            for (YuGiOhSpellTrap.DeepSpellTrapTypes type : YuGiOhSpellTrap.DeepSpellTrapTypes.values()){
                if (type.getSpellTrapType().equals(query)){
                    return type;
                }
            }
            return NORMAL;
        }


    }


    private DeepSpellTrapTypes deepSpellTrapTypes;

    public YuGiOhSpellTrap(String name, MainCardTypes type, String effect, String pack, String id, String cardImagePath, DeepSpellTrapTypes deepSpellTrapTypes) {
        super(name, type, effect, pack, id, cardImagePath);
        this.deepSpellTrapTypes = deepSpellTrapTypes;
    }

    public DeepSpellTrapTypes getDeepSpellTrapTypes() {
        return deepSpellTrapTypes;
    }

    public void setDeepSpellTrapTypes(DeepSpellTrapTypes deepSpellTrapTypes) {
        this.deepSpellTrapTypes = deepSpellTrapTypes;
    }
}

