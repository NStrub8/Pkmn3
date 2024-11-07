package ru.mirea.pkmn.strubalin;

import ru.mirea.pkmn.Card;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
public class CardExport {
    private final Card card;

    public CardExport(Card card) {
        this.card = card;
    }

    public void serializeToBytes() throws IOException {
        File output = new File("src/main/resources/" + this.card.getName() + ".crd");
        try (FileOutputStream fileOutputStream = new FileOutputStream(output);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(this.card);
        }
    }
}