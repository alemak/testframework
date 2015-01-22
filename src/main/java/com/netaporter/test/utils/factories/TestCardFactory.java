package com.netaporter.test.utils.factories;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.netaporter.test.utils.enums.CardRestriction;
import com.netaporter.test.utils.enums.CardType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
/**
 * Created by prashant.ramcharan@net-a-porter.com on 13/02/14.
 * PURPOSE - Allows you to randomly retrieve a test card number for a specific card and its card restriction.
 */
@Component
public class TestCardFactory {
    private @Value("${testCardsPropertiesFile}")String testCardsPropertiesFile;
    private Properties prop = new Properties();
    private Multimap<CardType, Map<CardRestriction, String>> cards = ArrayListMultimap.create();

    public String getCardNumber(CardType cardType, CardRestriction cardRestriction){
        registerCards(cardType, cardRestriction);
        String cardNumber = cards.get(cardType).iterator().next().get(cardRestriction);
        if (cardNumber == null){
            throw new RuntimeException(String.format("TestCardFactory: There is no %s registered card with a %s card restriction.", cardType, cardRestriction));
        }
        return cardNumber;
    }
    private void registerCards(CardType cardType, CardRestriction cardRestriction){
        // remove all registered cards
        cards.clear();
        switch (cardType){
            case VISA_CREDIT_CARD:
                Map<CardRestriction, String> visaCardDetail = new Hashtable();
                List<String> visaCardList = getCardListFromProperties(CardType.VISA_CREDIT_CARD, CardRestriction.NONE);
                Collections.shuffle(visaCardList);
                visaCardDetail.put(CardRestriction.NONE, visaCardList.get(0));
                visaCardDetail.put(CardRestriction.INTL, visaCardList.get(0));
                visaCardDetail.put(CardRestriction.APAC, visaCardList.get(0));
                visaCardDetail.put(CardRestriction.AM, visaCardList.get(0));
                cards.put(CardType.VISA_CREDIT_CARD, visaCardDetail);
                break;
            case MASTER_CARD:
                Map<CardRestriction, String> masterCardDetail = new Hashtable();
                List<String> masterCardList = getCardListFromProperties(CardType.MASTER_CARD, CardRestriction.NONE);
                Collections.shuffle(masterCardList);
                masterCardDetail.put(CardRestriction.NONE, masterCardList.get(0));
                masterCardDetail.put(CardRestriction.INTL, masterCardList.get(0));
                masterCardDetail.put(CardRestriction.APAC, masterCardList.get(0));
                if (cardRestriction == CardRestriction.AM){
                    masterCardDetail.put(CardRestriction.AM, getCardListFromProperties(CardType.MASTER_CARD, CardRestriction.AM).get(0));
                }
                cards.put(CardType.MASTER_CARD, masterCardDetail);
                break;
            case VISA_ELECTRON:
                Map<CardRestriction, String> visaElectronCardDetail = new Hashtable();
                List<String> visaElectronCardList = getCardListFromProperties(CardType.VISA_ELECTRON, CardRestriction.NONE);
                Collections.shuffle(visaElectronCardList);
                visaElectronCardDetail.put(CardRestriction.NONE, visaElectronCardList.get(0));
                visaElectronCardDetail.put(CardRestriction.INTL, visaElectronCardList.get(0));
                visaElectronCardDetail.put(CardRestriction.APAC, visaElectronCardList.get(0));
                visaElectronCardDetail.put(CardRestriction.AM, visaElectronCardList.get(0));
                cards.put(CardType.VISA_ELECTRON, visaElectronCardDetail);
                break;
            case AMERICAN_EXPRESS:
                Map<CardRestriction, String> americanExpressCardDetail = new Hashtable();
                List<String> americanExpressCardList = getCardListFromProperties(CardType.AMERICAN_EXPRESS, CardRestriction.NONE);
                Collections.shuffle(americanExpressCardList);
                americanExpressCardDetail.put(CardRestriction.NONE, americanExpressCardList.get(0));
                americanExpressCardDetail.put(CardRestriction.INTL, americanExpressCardList.get(0));
                americanExpressCardDetail.put(CardRestriction.APAC, americanExpressCardList.get(0));
                americanExpressCardDetail.put(CardRestriction.AM, americanExpressCardList.get(0));
                cards.put(CardType.AMERICAN_EXPRESS, americanExpressCardDetail);
                break;
            case MAESTRO:
                Map<CardRestriction, String> maestroCardDetail = new Hashtable();
                List<String> maestroCardList;
                if (cardRestriction == CardRestriction.INTL){
                    maestroCardList = getCardListFromProperties(CardType.MAESTRO, CardRestriction.INTL);
                    Collections.shuffle(maestroCardList);
                    maestroCardDetail.put(CardRestriction.INTL, maestroCardList.get(0));
                    cards.put(CardType.MAESTRO, maestroCardDetail);
                }
                if (cardRestriction == CardRestriction.APAC){
                    maestroCardList = getCardListFromProperties(CardType.MAESTRO, CardRestriction.APAC);
                    Collections.shuffle(maestroCardList);
                    maestroCardDetail.put(CardRestriction.APAC, maestroCardList.get(0));
                    cards.put(CardType.MAESTRO, maestroCardDetail);
                }
                break;
            case JCB:
                Map<CardRestriction, String> jcbCardDetail = new Hashtable();
                List<String> jcbCardList;
                if (cardRestriction == CardRestriction.INTL){
                    jcbCardList = getCardListFromProperties(CardType.JCB, CardRestriction.INTL);
                    Collections.shuffle(jcbCardList);
                    jcbCardDetail.put(CardRestriction.INTL, jcbCardList.get(0));
                    cards.put(CardType.JCB, jcbCardDetail);
                }
                if (cardRestriction == CardRestriction.APAC){
                    jcbCardList = getCardListFromProperties(CardType.JCB, CardRestriction.APAC);
                    Collections.shuffle(jcbCardList);
                    jcbCardDetail.put(CardRestriction.APAC, jcbCardList.get(0));
                    cards.put(CardType.JCB, jcbCardDetail);
                }
                break;
            default:
                throw new RuntimeException(String.format("The test card factory does not support this card type (%s) as yet.", cardType.toString()));
        }
    }
    private List<String> getCardListFromProperties(CardType cardType, CardRestriction cardRestriction) {
        String formattedCardType = cardType.toString().replace("_", "").toLowerCase();
        String propertyName = formattedCardType.concat(".").concat(cardRestriction.toString().toLowerCase());
        // check system property
        String cardListString = getFromSystemProperty(propertyName);
        // check properties file
        if (cardListString == null){
            if(prop.isEmpty()){
                if(testCardsPropertiesFile !=null && !testCardsPropertiesFile.equals("${testCardsPropertiesFile}")){
                    try {
                        prop.load(new FileInputStream(testCardsPropertiesFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            cardListString = prop.getProperty(propertyName);
        }
        if (cardListString != null){
            String[] cardList = cardListString.replace(" ", "").split(",");
            return Arrays.asList(cardList);
        }
        throw new RuntimeException(String.format("You need to set a property named -D%s.%s=[..] to use this factory.", formattedCardType, cardRestriction.toString().toLowerCase()));
    }
    private static String getFromSystemProperty(String propertyName){
        return System.getProperty(propertyName);
    }

}

