package ru.mirea.pkmn.strubalin;

import ru.mirea.pkmn.*;
import ru.mirea.pkmn.strubalin.web.jdbc.DatabaseServiceImpl;

import java.io.IOException;
import java.sql.SQLException;

public class PkmnApplication {
    public static void main(String[] args) throws IOException, SQLException {
        Card card = CardImport.readCard("src/main/resources/my_card.txt");

        DatabaseServiceImpl db = new DatabaseServiceImpl();
        db.createPokemonOwner(card.getPokemonOwner());
        db.saveCardToDatabase(card);

        System.out.println(db.getCardFromDatabase("Noibat").toString());
        System.out.println(db.getStudentFromDatabase("Strubalin Nikita Pavlovich").toString());
    }
}