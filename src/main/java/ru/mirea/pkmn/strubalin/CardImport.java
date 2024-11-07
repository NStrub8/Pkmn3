package ru.mirea.pkmn.strubalin;
import ru.mirea.pkmn.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CardImport {
    public Card importCard(String filename) throws IOException {

        FileInputStream inputStream = new FileInputStream("src/main/resources/" + filename);
        Card card = new Card();
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(inputStreamReader))
        {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ", 2);  // Разбиваем строку на две части: номер и значение
                if (parts.length < 2) continue;       // Если строка не содержит значение, пропускаем ее
                String key = parts[0];
                String value = parts[1];

                switch (key) {
                    case "1.":
                        card.setPokemonStage(PokemonStage.valueOf(value));
                        break;
                    case "2.":
                        card.setName(value);
                        break;
                    case "3.":
                        card.setHp(Integer.parseInt(value));
                        break;
                    case "4.":
                        card.setPokemonType(EnergyType.valueOf(value));
                        break;
                    case "5.":
                        if (!value.equals("-")) {
                            Card evolvesCard = importCard(value);
                            card.setEvolvesFrom(evolvesCard);
                        } else {
                            card.setEvolvesFrom(null);
                        }
                        break;
                    case "6.":
                        List<AttackSkill> attackSkills = new ArrayList<>();
                        String[] skills = value.split(",");
                        for (String skill : skills) {
                            String[] skillParts = skill.split("/");
                            if (skillParts.length >= 3) {  // Проверка, что есть три части
                                attackSkills.add(new AttackSkill(skillParts[1], skillParts[0], Integer.parseInt(skillParts[2])));
                            } else {
                                System.out.println("Ошибка: некорректный формат умений - " + skill);
                            }
                        }
                        card.setSkills(attackSkills);
                        break;
                    case "7.":
                        card.setWeaknessType(!value.isEmpty() ? EnergyType.valueOf(value) : null);
                        break;
                    case "8.":
                        card.setResistanceType(!value.equals("-") ? EnergyType.valueOf(value) : null);
                        break;
                    case "9.":
                        card.setRetreatCost(!value.equals("-") ? value : null);
                        break;
                    case "10.":
                        card.setGameSet(!value.equals("-") ? value : null);
                        break;
                    case "11.":
                        card.setRegulationMark(value.isEmpty() ? null : value.charAt(0));
                        break;
                    case "12.":
                        if (!value.equals("-")) {
                            String[] ownerInfo = value.split("/");
                            if (ownerInfo.length >= 4) {  // Проверяем, что ownerInfo содержит 4 элемента
                                Student owner = new Student(ownerInfo[1], ownerInfo[0], ownerInfo[2], ownerInfo[3]);
                                card.setPokemonOwner(owner);
                            } else {
                                System.out.println("Ошибка: некорректный формат данных владельца - " + value);
                                card.setPokemonOwner(null);
                            }
                        } else {
                            card.setPokemonOwner(null);
                        }
                        break;
                    case "13.":
                        card.setNumber(!value.equals("-") ? value : null);
                        break;
                    default:
                        System.out.println("Неизвестный ключ: " + key);
                }
            }

        }
        return card;
    }

    public Card deserializeCard(String name) throws IOException, ClassNotFoundException {
        String path = "src/main/resources/" + name + ".crd";
        FileInputStream fileInputStream = new FileInputStream(path);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        return (Card) objectInputStream.readObject();
    }
}
