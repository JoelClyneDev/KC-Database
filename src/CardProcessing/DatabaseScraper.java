package CardProcessing;

import CardTypes.YuGiOhCard;
import CardTypes.YuGiOhMonster;
import CardTypes.YuGiOhSpellTrap;
import LocalDatabaseOperations.CardDatabaseManager;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class DatabaseScraper{
    //the url to the Yu-Gi-Oh! database website
    private static final String YUGIOH_DATABASE_URL_FULL = "https://www.db.yugioh-card.com/yugiohdb/card_list.action";
    //the url to the Yu-Gi-Oh! database website that works with the individual packs
    private static final String YUGIOH_DATABASE_URL_CUT = "https://www.db.yugioh-card.com";
    //the search query to find a table on the page with all of the databases
    private static final String TABLE_GETTER = "table.card_list";
    //use this search query to get a specific pack list by some category in the list of card distribution tables
    private static final String PACK_CATEGORY_GETTER = "td";
    //use this search query to get the body of a pack category without the header at the top
    private static final String CATEGORY_BODY_GETTER = "div.list_body";
    //use this search query to get all packs for a specific year in a body
    private static final String BODY_PACK_YEAR_GETTER = "div.toggle.none";
    //use this search query to get an individual pack from a year
    private static final String YEAR_INDIVIDUAL_PACK_GETTER = "div.pack.pack_en";
    //use this with getElementsByTag to get the <input ...> part of a pack
    private static final String INDIVIDUAL_INPUT_LINE_GETTER = "input";
    //get name of pack
    private static final String PACK_NAME_GETTER = "h1";
    //get all cards in pack
    private static final String CARD_LIST_IN_PACK_GETTER = "box_list";
    //the list of urls for the packs
    private ArrayList<String> packLinkList;



    //the link to the yugioh wikia to get card images
    private static final String WIKI_URL = "https://yugioh.fandom.com/wiki/";
    //the link to yugipedia just incase the wiki doesn't have the picture
    private static final String YUGIPEDIA_URL = "https://yugipedia.com/wiki/";

    String databaseInfo;

    /**
     * gets the information from the konami website
     * Rn it only gets the name
     */
    public void runScrape(boolean downloadCards) throws IOException, InterruptedException {

        main2(downloadCards);
    }
    /**
     fills the pack Link List with all of the packs found in the elements argument
     allPacksInYear - all of the packs in a year a as shown on the Yu-Gi-Oh! database website
     */
    private ArrayList<String> populatePackLinkList(Elements allPacksInYear){
        //this loop gets all of the links in one category table
        //loop this again for every category
        ArrayList<String> localPackLinkList = new ArrayList<>();

        for (Element element : allPacksInYear){
            //separates the individual packs of a year
            Elements individualPacks = element.select(YEAR_INDIVIDUAL_PACK_GETTER);
            //prints the name of those packs
            for (Element pack : individualPacks){
                //looks at everything in the pack with the input tag
                Element url = pack.getElementsByTag(INDIVIDUAL_INPUT_LINE_GETTER).get(0);
                //adds the proper beginning to the url
                //now I need to save the url
                //packLinkList.add(YUGIOH_DATABASE_URL_CUT + url.val());
                //this is if I just want to make it a local variable
                localPackLinkList.add(YUGIOH_DATABASE_URL_CUT + url.val());
            }
        }
        return localPackLinkList;
    }

    /**
     * loops through every table on the yugioh database page, getting the elements to be processed with populatePackLinkList
     * returns - elements of every pack on the Yu-Gi-Oh! Database
     */
    private Elements loopThroughAllTablesOnDatabaseWebsite() throws IOException {
        Elements allPacks = new Elements();
        //connect to the website
        Document doc = Jsoup.connect(YUGIOH_DATABASE_URL_FULL).get();
        //gets the table of all of the gray tables
        Elements boosterAndPromotionTableList = doc.select(TABLE_GETTER);
        //removes the last one I dont need
        boosterAndPromotionTableList.remove(boosterAndPromotionTableList.size() - 1);
        for (Element currentTableList : boosterAndPromotionTableList){
            //this all the tables in the table list
            Elements currentTables = currentTableList.select(PACK_CATEGORY_GETTER);
            for (Element categoryPackList : currentTables){
                //get the body with the card lists by year, ignoring the table title
                Element tableInformationPart = categoryPackList.select(CATEGORY_BODY_GETTER).get(0);
                Elements packsByYear = tableInformationPart.select(BODY_PACK_YEAR_GETTER);
                allPacks.addAll(packsByYear);
            }
        }
        return allPacks;
    }

    /**
     * card names with updates make the program not work
     * this fixes them
     */
    private String fixCardName(String brokenName) throws UnsupportedEncodingException {
        String[] repair = brokenName.split("\\(");
        String fixedName = URLEncoder.encode(repair[0], StandardCharsets.UTF_8);
        fixedName = fixedName.replace("+", "_");
        return fixedName;

    }

    private String fixHashtags(String brokenName) throws UnsupportedEncodingException {
        brokenName = fixCardName(brokenName);
        brokenName = brokenName.replace("%23", "");
        return brokenName;
    }


    private ArrayList<YuGiOhCard> getCardsFromPackLink(String packUrl, boolean downloadCards) throws IOException {
        // an array list containing all the cards pulled from the pack
        ArrayList<YuGiOhCard> allCardsInPack= new ArrayList<>();


        //the pack page for the current link
        Document currentPackPage = Jsoup.connect(packUrl).get();
        //get the pack name from its box
        String packName = currentPackPage.getElementsByTag(PACK_NAME_GETTER).get(0).text();
        //get the table with all of the cards
        Elements cardTable = currentPackPage.getElementsByClass(CARD_LIST_IN_PACK_GETTER).select("li");

        //get all the info for each card
        for (int i = 0; i < cardTable.size(); i++){
            Element currentCard = cardTable.select("li").get(i);
            //get the name
            String cardName = currentCard.select("strong").text();
            String originalCardName = cardName;
            cardName = fixCardName(cardName);
            //get the effect
            String cardEffect = currentCard.select("dd.box_card_text").text();
            //the main type of the card
            String cardType = currentCard.select("span.box_card_attribute").text();
            //get the picture of the card from another database
            YuGiOhCard.MainCardTypes enumCardType = YuGiOhCard.MainCardTypes.getEnumValue(cardType);

            //just in case the links don't work
            String cardExtraInfoUrl;
            Document cardExtraInfoPage;
            try {
                cardExtraInfoUrl = WIKI_URL + cardName;
                cardExtraInfoPage = Jsoup.connect(cardExtraInfoUrl).get();
            } catch (HttpStatusException he){
                try {
                    cardExtraInfoUrl = YUGIPEDIA_URL + cardName;
                    cardExtraInfoPage = Jsoup.connect(cardExtraInfoUrl).get();
                } catch ( HttpStatusException he2 ){
                    System.out.println(originalCardName + " - broken card");
                    continue;
                }
            }
            String wikiURL = WIKI_URL + cardName;
            String yugipediaURL = YUGIPEDIA_URL + cardName;


            //System.out.println(imagePageUrl);
            String cardImageUrl = "";

            //try the wiki first
            try {
                Document imagePage = Jsoup.connect(wikiURL).get();
                cardImageUrl = imagePage.select("td.cardtable-cardimage").get(0).select("img[src]").get(0).attr("src");
            } catch (Exception iobe ) {
                //if the wiki doesn't have the picture use yugipedia
                Document imagePageBackup = Jsoup.connect(yugipediaURL).get();
                System.out.println(YUGIPEDIA_URL + cardName);
                try {
                    cardImageUrl = imagePageBackup.select("div.cardtable-main_image-wrapper").get(0).select("img[src]").get(0).attr("src");
                } catch (Exception ie) {
                    try {
                        imagePageBackup = Jsoup.connect(YUGIPEDIA_URL + cardName + "_(card)").get();
                        cardImageUrl = imagePageBackup.select("div.cardtable-main_image-wrapper").get(0).select("img[src]").get(0).attr("src");
                    } catch (Exception he2) {
                        try {
                            String cardImagePath = YUGIPEDIA_URL + fixHashtags(originalCardName);
                            imagePageBackup = Jsoup.connect(cardImagePath).get();
                            cardImageUrl = imagePageBackup.select("div.cardtable-main_image-wrapper").get(0).select("img[src]").get(0).attr("src");
                            System.out.println(cardImagePath + " Numbers fix?");
                        } catch (Exception e4) {
                            System.out.println(cardImageUrl);
                            System.out.println(originalCardName + " - broken Image");
                            e4.printStackTrace();
                            continue;
                        }
                    }
                }
                System.out.println(cardImageUrl);
            }
            String cardImagePath = "";

            if (downloadCards) {
                cardImagePath = downloadCardImages(cardImageUrl, cardName);
            }

            String packAbbreviation = null;
            try {
                packAbbreviation = cardExtraInfoPage.selectFirst("div.cardset").select("span").get(5).text();
            } catch (NullPointerException npe) {
                try {
                    packAbbreviation = cardExtraInfoPage.select("td.cardtablespanrow").get(1).select("a.mw-redirect").get(0).text();
                } catch (Exception e) {
                    String imagePageBackupUrl = yugipediaURL;
                    Document imagePageBackup = Jsoup.connect(imagePageBackupUrl).get();
                    try {
                        packAbbreviation = imagePageBackup.select("table#cts--EN").select("a.mw-redirect").get(0).text();
                    } catch (IndexOutOfBoundsException ibe) {
                        try {
                            packAbbreviation = imagePageBackup.select("table#cts--NA").select("a.mw-redirect").get(0).text();
                        } catch (IndexOutOfBoundsException e2) {
                            imagePageBackupUrl = yugipediaURL + "_(card)";
                            imagePageBackup = Jsoup.connect(imagePageBackupUrl).get();
                            try {
                                packAbbreviation = imagePageBackup.select("table#cts--EN").select("a.mw-redirect").get(0).text();
                            } catch (IndexOutOfBoundsException ibee) {
                                try {
                                    packAbbreviation = imagePageBackup.select("table#cts--NA").select("a.mw-redirect").get(0).text();
                                } catch (Exception e1) {
                                    try {
                                        imagePageBackupUrl = YUGIPEDIA_URL + fixHashtags(originalCardName);
                                        imagePageBackup = Jsoup.connect(imagePageBackupUrl).get();
                                        packAbbreviation = imagePageBackup.select("table#cts--EN").select("a.mw-redirect").get(0).text();
                                    } catch (Exception e3) {
                                        System.out.println(originalCardName + " - bad pack name");
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            finally {
                if (packAbbreviation == null) {
                    try {
                        packAbbreviation = cardExtraInfoPage.selectFirst("a.new").text();
                    } catch (Exception e){
                        System.out.println(originalCardName + " - broken pack name");
                        e.printStackTrace();
                    }
                }
                //the build the incrementing part
            }
            //now I have all the elements shared by All YuGiOhCards
            if (cardType.equals(YuGiOhCard.MainCardTypes.SPELL.getValue()) || cardType.equals(YuGiOhCard.MainCardTypes.TRAP.getValue())) {
                YuGiOhSpellTrap newSpellOrTrap = generateSpellTrap(originalCardName, cardType, cardEffect, packName, packAbbreviation, cardImagePath, currentCard);
                allCardsInPack.add(newSpellOrTrap);
            }
            else{
                YuGiOhMonster newMonster = generateYuGiOhMonster(originalCardName, cardEffect, cardType, cardImagePath, packName, packAbbreviation, currentCard);
                allCardsInPack.add(newMonster);
            }
        }
        System.out.println(packName + " card extraction complete!");
        return allCardsInPack;
    }

    private int getAttackOrDefense(String pointStr){
        //get the attack and defense
        //if value is ? then the int is -1
        //if values is - then the int is -2
        int currentPoints;
        if (pointStr.equals("?")){
            currentPoints = -1;
        }
        else if (pointStr.equals("-")){
            currentPoints = -2;
        }
        else {
            currentPoints = Integer.parseInt(pointStr);
        }
        return currentPoints;
    }

    private YuGiOhMonster generateYuGiOhMonster(String cardName, String cardEffect, String cardType, String cardImagePath, String packName, String packAbbreviation, Element currentCard) throws IOException {
        //get the Attribute(Fire, Dark, etc)
        YuGiOhMonster.Attributes currentAttribute = YuGiOhMonster.Attributes.valueOf(cardType);
        String[] levelText = new String[0];
        int monsterLevel = 0;
        try {
            levelText = currentCard.select("span.box_card_level_rank").text().split(" ");
            monsterLevel = Integer.parseInt(levelText[1]);
        } catch (IndexOutOfBoundsException ie){
            levelText = currentCard.select("span.box_card_linkmarker").text().split(" ");
            monsterLevel = Integer.parseInt(levelText[1]);
        }
        //get the other stuff in the bracket part
        String otherAttributesOriginal = currentCard.select("span.card_info_species_and_other_item").text();
        //remove the beginning and ending bracket and spaces, replace the /'s with nothing, effectively removing them, split by the left over spaces
        String[] otherAttributesArray = otherAttributesOriginal.substring(2, otherAttributesOriginal.length() - 2).replaceAll("/", "").split(" {2}");
        //the monster type is always the first one
        YuGiOhMonster.MonsterTypes currentMonsterType = YuGiOhMonster.MonsterTypes.getMonsterTypeClassValue(otherAttributesArray[0]);
        //determine if this is an effect monster
        boolean currentEffectStatus = false;
        if (otherAttributesArray[otherAttributesArray.length - 1].equals("Effect")){
            currentEffectStatus = true;
            //these dont get deleted in the case of a non effect monster
            otherAttributesArray[otherAttributesArray.length - 1] = null;
        }
        else if (otherAttributesArray[otherAttributesArray.length - 1].equals("Normal")){
            //these dont get deleted in the case of a non effect Normal monster
            otherAttributesArray[otherAttributesArray.length - 1] = null;
        }
        //remove the already parsed
        otherAttributesArray[0] = null;
        //determine extra deck type if there is
        YuGiOhMonster.MonsterSummoningType currentMonsterSummoningType = null;
        boolean pendulumStatus = false;
        String pendulumEffect = "";
        int pendulumScale = 0;
        for (String currentAttr : otherAttributesArray){
            if (currentAttr != null){
                if(YuGiOhMonster.MonsterSummoningType.isInDeepMonsterTypes(currentAttr)){
                    currentAttr = currentAttr.toUpperCase();
                    currentMonsterSummoningType = YuGiOhMonster.MonsterSummoningType.valueOf(currentAttr);
                }
                else if (currentAttr.equals("Pendulum")){
                    pendulumStatus = true;
                    //get the pendulum values
                    try {
                        pendulumEffect = currentCard.select("span.box_card_pen_effect").get(0).text();
                        pendulumScale = Integer.parseInt(currentCard.select("span.box_card_pen_scale").get(0).text());
                    } catch (IndexOutOfBoundsException ie){
                        String backupURL = YUGIPEDIA_URL + fixCardName(cardName);
                        Document backupCardFinder = Jsoup.connect(backupURL).get();
                        pendulumScale = Integer.parseInt(backupCardFinder.select("table.innertable").get(0).select("tr").get(4).select("p").text());
                        pendulumEffect = backupCardFinder.select("div.lore").get(0).select("dd").get(0).text();

                    }

                }
            }
        }
        String atkStr = currentCard.select("span.atk_power").text().substring(4);
        int currentAtk = getAttackOrDefense(atkStr);
        String defStr = currentCard.select("span.def_power").text().substring(4);
        int currentDefense = getAttackOrDefense(defStr);
        return finalizeYuGiOhMonster(cardName, cardEffect, packName, packAbbreviation, cardImagePath, currentMonsterType, monsterLevel, currentAtk, currentDefense, currentEffectStatus, currentAttribute, pendulumEffect, pendulumScale, pendulumStatus, currentMonsterSummoningType);


        //now we have all of the card information shared by all monsters
    }
    private YuGiOhMonster finalizeYuGiOhMonster(String cardName, String cardEffect, String packName, String packAbbreviation, String cardImagePath, YuGiOhMonster.MonsterTypes currentMonsterType, int monsterLevel, int currentAtk, int currentDefense, boolean currentEffectStatus, YuGiOhMonster.Attributes currentAttribute, String pendulumEffect, int pendulumScale, boolean pendulumStatus, YuGiOhMonster.MonsterSummoningType currentMonsterSummoningType){
        YuGiOhMonster newMonster;
        if(currentMonsterSummoningType == null){
            if (pendulumStatus){
                //non summoning, pendulum
                newMonster = new YuGiOhMonster(cardName, YuGiOhCard.MainCardTypes.MONSTER, cardEffect,packName,packAbbreviation,cardImagePath,currentMonsterType, monsterLevel, currentAtk, currentDefense, currentEffectStatus, currentAttribute, pendulumEffect, pendulumScale);
            }
            else{
                //non summoning, non pendulum
                newMonster = new YuGiOhMonster(cardName, YuGiOhCard.MainCardTypes.MONSTER, cardEffect,packName,packAbbreviation,cardImagePath,currentMonsterType, monsterLevel, currentAtk, currentDefense, currentEffectStatus, currentAttribute);
            }
        }
        else {
            if (pendulumStatus){
                //summoning, pendulum
                newMonster = new YuGiOhMonster(cardName, YuGiOhCard.MainCardTypes.MONSTER, cardEffect, packName, packAbbreviation, cardImagePath, currentMonsterType, monsterLevel, currentAtk, currentDefense, currentEffectStatus, currentAttribute,  pendulumEffect, pendulumScale, currentMonsterSummoningType);
            }
            else {
                //summoning, non pendulum
                newMonster = new YuGiOhMonster(cardName, YuGiOhCard.MainCardTypes.MONSTER, cardEffect,packName,packAbbreviation,cardImagePath,currentMonsterType, monsterLevel, currentAtk, currentDefense, currentEffectStatus, currentAttribute, currentMonsterSummoningType);
            }
        }
        return newMonster;
    }

    private YuGiOhSpellTrap generateSpellTrap(String name, String typeStr, String effect, String pack, String id, String cardImagePath, Element currentCard){
        String deepCardType = currentCard.select("span.box_card_effect").text();
        YuGiOhSpellTrap.DeepSpellTrapTypes trueDeepCardType = YuGiOhSpellTrap.DeepSpellTrapTypes.getSpellTrapTypeClassValue(deepCardType);
        YuGiOhCard.MainCardTypes spellOrTrap = YuGiOhCard.MainCardTypes.getEnumValue(typeStr);
        return new YuGiOhSpellTrap(name, spellOrTrap, effect, pack, id, cardImagePath, trueDeepCardType);
    }

    private String downloadCardImages(String cardImageUrl, String cardName){
        try {
            boolean card_dir = new File(System.getProperty("user.dir") + "/card_images/").mkdirs();
            URL javaVerCardURL = new URL(cardImageUrl);
            BufferedImage cardImage = ImageIO.read(javaVerCardURL);
            String imageDirectory = System.getProperty("user.dir") + "/card_images/" + cardName + ".png";
            File cardImageFile = new File(imageDirectory);
            ImageIO.write(cardImage, "png", cardImageFile);
            //file stuff

            //File cardImageFile = new File(imageDirectory, cardImage + ".png");
            return  cardImageFile.getAbsolutePath();
            //compress later
        } catch (IllegalStateException | MalformedURLException ie){
            System.out.println(cardImageUrl);
            System.out.println(cardName);
            ie.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * The main process for the database extraction
     * Runs the sc
     * @throws IOException
     */


    public LinkedBlockingQueue<ArrayList<String>> prepareCardsForThreading(ArrayList<String> cardsForPrep){
        LinkedBlockingQueue<ArrayList<String>> threadingPackage = new LinkedBlockingQueue<>();
        int changeCount = 0;
        ArrayList<String> tempCards = new ArrayList<>();
        for (int i = 0; i < cardsForPrep.size(); i++){
            tempCards.add(cardsForPrep.get(i));
            changeCount += 1;
            if (changeCount >= 10 || i == cardsForPrep.size() - 1){
                threadingPackage.add(cloneArrayList(tempCards));
                changeCount = 0;
                tempCards.clear();
            }
        }
        return threadingPackage;
    }

    public ArrayList<String> cloneArrayList(ArrayList<String> original){
        return new ArrayList<>(original);
    }

    public synchronized void addCardsToDatabase(LinkedBlockingQueue<YuGiOhCard> cardsForProcessing, Connection conn) throws SQLException {
        for (YuGiOhCard card : cardsForProcessing){
            //split between monster and spell tables
            if (card instanceof YuGiOhMonster){
                YuGiOhMonster monster = ((YuGiOhMonster) card);
                CardDatabaseManager.insertMonster(monster, conn);
            }else if (card instanceof YuGiOhSpellTrap){
                YuGiOhSpellTrap spellTrap = (YuGiOhSpellTrap) card;
                CardDatabaseManager.insertSpellTrap(spellTrap, conn);
            }

        }
    }
    public void main2(boolean downloadCards) throws IOException, InterruptedException {
        //make a database
        Connection conn = CardDatabaseManager.createNewDatabase("Card-Database");
        //get the path if its useful
        //First, get the elements of all table on the database website
        Elements allPacks = loopThroughAllTablesOnDatabaseWebsite();
        //Next, use those elements to get all the links for the packLinkList
        ArrayList<String> localPackLinkList = populatePackLinkList(allPacks);
        //Use a queue to put the cards into threads
        LinkedBlockingQueue<ArrayList<String>> threadingCards = prepareCardsForThreading(localPackLinkList);


        int tempCount = 0;
        System.out.println(threadingCards.size() + " prepared pakcages");
        ArrayList<Thread> threadsForJoining = new ArrayList<>();

        while (!threadingCards.isEmpty()){
            ThreadHandler packProcessThread = new ThreadHandler(threadingCards.poll(), conn, tempCount, downloadCards);
            tempCount += 1;
            Thread wrapper = new Thread(packProcessThread);
            wrapper.start();
            threadsForJoining.add(wrapper);
        }
        for (Thread t : threadsForJoining){
            t.join();
        }
        System.out.println("Processing Complete!");
/*
            }
            for (String url: localPackLinkList){
                ArrayList<String> temporaryURLHolder = new ArrayList<>();
                temporaryURLHolder.add(url);
                if (temporaryURLHolder.size() >= 50){
                    ThreadHandler packProcessThread = new ThreadHandler(temporaryURLHolder, cardDB);
                    Thread wrappedPackProcessThread = new Thread(packProcessThread);
                    wrappedPackProcessThread.start();
                    temporaryURLHolder.clear();
                }

 */


    }





    public class ThreadHandler implements Runnable{

        private ArrayList<String> packUrls;
        private LinkedBlockingQueue<YuGiOhCard> cardsForProcessing;
        private int name;
        private Connection conn;
        private boolean downloadCards;

        public ThreadHandler(ArrayList<String> packUrls, Connection conn, int name, boolean downloadCards){
            this.packUrls = packUrls;
            this.cardsForProcessing = new LinkedBlockingQueue<>();
            this.name = name;
            this.conn = conn;
            this.downloadCards = downloadCards;
        }

        @Override
        public void run() {
            System.out.println("Thread " + name + " Started");
            try {
                for (String link : packUrls){
                    ArrayList<YuGiOhCard> newCards = getCardsFromPackLink(link, downloadCards);
                    //After, put every card in the pack in the queue for processing
                    cardsForProcessing.addAll(newCards);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Thread " + name + " Finished");
            try {
                addCardsToDatabase(cardsForProcessing, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }


/**
 * may delete later bcs database class should handle this
 * //check if it's serializable
 *         Serializable tempSerial = getIntent().getSerializableExtra("result");
 *         if (tempSerial instanceof SearchQueryPackager){
 *             inputForSearch = (SearchQueryPackager) tempSerial;
 *         }
 */
//old try catch for image and extra info pages
/**
 String imagePageUrl = WIKI_URL + cardName;
 Document imagePage = null;
 try {
 imagePage = Jsoup.connect(imagePageUrl).get();
 } catch (HttpStatusException hse){
 try {
 imagePageUrl = WIKI_URL + cardName;
 imagePage = Jsoup.connect(imagePageUrl).get();
 } catch (HttpStatusException a){
 System.out.println(cardName + " - broken name");
 continue;
 }

 }
 */
}
