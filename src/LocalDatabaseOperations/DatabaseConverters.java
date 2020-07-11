package LocalDatabaseOperations;

import CardTypes.YuGiOhCard;
import CardTypes.YuGiOhMonster;
import CardTypes.YuGiOhSpellTrap;

public class DatabaseConverters {
    public static String MainCardTypeToString(YuGiOhCard.MainCardTypes value){
        return value == null ? null : value.getValue();
    }

    public static YuGiOhCard.MainCardTypes StringtoMainCardType(String  main){
        return main == null ? null : YuGiOhCard.MainCardTypes.getEnumValue(main);
    }

    public static String MonsterSummoningTypeToString(YuGiOhMonster.MonsterSummoningType summoningType){
        return summoningType == null ? null : summoningType.getDeepType();
    }

    public static YuGiOhMonster.MonsterSummoningType StringToMonsterSummoningType  (String summonString){
        return summonString == null ? null : YuGiOhMonster.MonsterSummoningType.valueOf(summonString);
    }


    public static String MonsterTypeToString (YuGiOhMonster.MonsterTypes monsterType){
        return monsterType == null ? null : monsterType.getMonsterType();
    }

    public static YuGiOhMonster.MonsterTypes StringToMonsterType (String monsterType){
        return monsterType == null ? null : YuGiOhMonster.MonsterTypes.getMonsterTypeClassValue(monsterType);
    }

    public static String AttributeToString (YuGiOhMonster.Attributes attribute){
        return attribute == null ? null : attribute.getValue();
    }

    public static YuGiOhMonster.Attributes StringToAttribute (String attrString){
        return attrString == null ? null : YuGiOhMonster.Attributes.valueOf(attrString);
    }

    public static String DeepSpellTrapTypeToString(YuGiOhSpellTrap.DeepSpellTrapTypes spellTrapType){
        return spellTrapType == null ? null : spellTrapType.getSpellTrapType();
    }

    public static YuGiOhSpellTrap.DeepSpellTrapTypes StringToDeepSpellTrapType(String string){
        return string == null ? null : YuGiOhSpellTrap.DeepSpellTrapTypes.getSpellTrapTypeClassValue(string);
    }
}
