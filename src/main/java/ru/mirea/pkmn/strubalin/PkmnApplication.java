package ru.mirea.pkmn.strubalin;

import java.sql.SQLException;
import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.AttackSkill;
import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.strubalin.web.http.PkmnHttpClient;
import java.io.IOException;
import java.util.stream.Stream;
import ru.mirea.pkmn.strubalin.web.jdbc.DatabaseServiceImpl;


public class PkmnApplication {
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        DatabaseServiceImpl db = new DatabaseServiceImpl();
        CardImport cardImport = new CardImport();
        Card card = cardImport.importCard("my_card.txt");

        PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();
        JsonNode card1 = pkmnHttpClient.getPokemonCard(card.getName(), card.getNumber());
        System.out.println(card1.toPrettyString());


        Stream<JsonNode> stream = card1.findValues("attacks").stream();
        JsonNode attacks = stream.toList().getFirst();
        stream.close();
        for(JsonNode attack : attacks) {
            for(AttackSkill skill : card.getSkills()) {
                if(skill.getName().equals(attack.findValue("name").asText())) {
                    skill.setDescription(attack.findValue("text").asText());
                }
            }
        }

        CardExport cardExport = new CardExport(card);
        cardExport.serializeToBytes();
        for(AttackSkill skill : card.getSkills()) {
            System.out.println(skill);
        }

        db.saveCardToDatabase(card);
        Card card2 = db.getCardFromDatabase("Noibat");
        System.out.println(card2);
    }
}
