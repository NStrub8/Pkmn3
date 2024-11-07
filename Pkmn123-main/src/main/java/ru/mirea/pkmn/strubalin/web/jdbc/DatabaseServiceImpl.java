package ru.mirea.pkmn.strubalin.web.jdbc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.mirea.pkmn.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DatabaseServiceImpl implements DatabaseService {

    private final Connection connection;

    private final Properties databaseProperties;

    public DatabaseServiceImpl() throws SQLException, IOException {

        // Загружаем файл database.properties

        databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream("src/main/resources/database.properties"));

        // Подключаемся к базе данных

        connection = DriverManager.getConnection(
                databaseProperties.getProperty("database.url"),
                databaseProperties.getProperty("database.user"),
                databaseProperties.getProperty("database.password")
        );
    }

    @Override
    public Card getCardFromDatabase(String cardName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from card where name = ?");
        ps.setString(1, cardName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Card card = new Card();
            card.setName(rs.getString("name"));
            card.setNumber(rs.getString("card_number"));
            card.setPokemonStage(PokemonStage.valueOf(rs.getString("stage")));
            card.setHp(rs.getInt("hp"));
            card.setPokemonType(EnergyType.valueOf(rs.getString("pokemon_type")));
            if (rs.getString("evolves_from") != null) {
                card.setEvolvesFrom(getCardFromDatabaseByUUID(UUID.fromString(rs.getString("evolves_from"))));
            } else {
                card.setEvolvesFrom(null);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<AttackSkill>>() {
            }.getType();
            List<AttackSkill> skills = gson.fromJson(rs.getString("attack_skills"), type);
            card.setSkills(skills);

            if (rs.getString("weakness_type") != null) {
                card.setWeaknessType(EnergyType.valueOf(rs.getString("weakness_type")));
            } else {
                card.setWeaknessType(null);
            }
            if (rs.getString("resistance_type") != null) {
                card.setResistanceType(EnergyType.valueOf(rs.getString("resistance_type")));
            } else {
                card.setResistanceType(null);
            }
            card.setRetreatCost(rs.getString("retreat_cost"));
            card.setGameSet(rs.getString("game_set"));
            card.setRegulationMark(rs.getString("regulation_mark").charAt(0));
            if (rs.getString("pokemon_owner") != null) {
                card.setPokemonOwner(getStudentFromDatabaseById(UUID.fromString(rs.getString("pokemon_owner"))));
            } else {
                card.setPokemonOwner(null);
            }
            ps.close();
            return card;
        }
        ps.close();
        return null;
    }

    private Card getCardFromDatabaseByUUID(UUID evolvesFrom) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from card where id = ?");
        ps.setObject(1, evolvesFrom);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Card card = new Card();
            card.setName(rs.getString("name"));
            card.setNumber(rs.getString("card_number"));
            card.setPokemonStage(PokemonStage.valueOf(rs.getString("stage")));
            card.setHp(rs.getInt("hp"));
            card.setPokemonType(EnergyType.valueOf(rs.getString("pokemon_type")));
            if (rs.getString("evolves_from") != null) {
                card.setEvolvesFrom(getCardFromDatabaseByUUID(UUID.fromString(rs.getString("evolves_from"))));
            } else {
                card.setEvolvesFrom(null);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<AttackSkill>>() {
            }.getType();
            List<AttackSkill> skills = gson.fromJson(rs.getString("attack_skills"), type);
            card.setSkills(skills);

            if (rs.getString("weakness_type") != null) {
                card.setWeaknessType(EnergyType.valueOf(rs.getString("weakness_type")));
            } else {
                card.setWeaknessType(null);
            }
            if (rs.getString("resistance_type") != null) {
                card.setResistanceType(EnergyType.valueOf(rs.getString("resistance_type")));
            } else {
                card.setResistanceType(null);
            }
            card.setRetreatCost(rs.getString("retreat_cost"));
            card.setGameSet(rs.getString("game_set"));
            card.setRegulationMark(rs.getString("regulation_mark").charAt(0));
            card.setPokemonOwner(getStudentFromDatabase(rs.getString("pokemon_owner")));
            ps.close();
            return card;
        }
        ps.close();
        return null;
    }

    ;

    public Student getStudentFromDatabaseById(UUID uuid) throws SQLException {
        String query = "select * from student WHERE \"id\" = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, uuid);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Student student = new Student();

                String value;
                if ((value = resultSet.getString("firstName")) != null) {
                    student.setFirstName(value);
                } else student.setFirstName(null);

                if ((value = resultSet.getString("familyName")) != null) {
                    student.setSurName(value);
                } else student.setSurName(null);

                if ((value = resultSet.getString("patronicName")) != null) {
                    student.setFamilyName(value);
                } else student.setFamilyName(null);

                if ((value = resultSet.getString("group")) != null) {
                    student.setGroup(value);
                } else student.setGroup(null);

                return student;
            } else return null;
        }
    }

    @Override
    public Student getStudentFromDatabase(String studentName) throws SQLException {
        if (studentName == null) {
            return null;
        }

        String[] split = studentName.split(" ");
        PreparedStatement ps = connection.prepareStatement("select * from student where " +
                "\"familyName\" = ? and \"firstName\" = ? and \"patronicName\" = ?");
        ps.setString(1, split[0]);
        ps.setString(2, split[1]);
        ps.setString(3, split[2]);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Student student = new Student(
                    rs.getString("firstName"),
                    rs.getString("familyName"),
                    rs.getString("patronicName"),
                    rs.getString("group")
            );
            ps.close();
            return student;
        }
        ps.close();
        return null;
    }

    @Override
    public void saveCardToDatabase(Card card) throws SQLException {
        Card evolvesFrom;
        UUID evolvesFromId = null;
        if ((evolvesFrom = card.getEvolvesFrom()) != null) {
            if (getCardFromDatabase(evolvesFrom.getName()) == null) {
                saveCardToDatabase(evolvesFrom);
            }
            evolvesFromId = getCardIdFromDatabase(evolvesFrom.getName());
        }
        Student pokemonOwner;
        UUID ownerId = null;
        if ((pokemonOwner = card.getPokemonOwner()) != null) {
            if (getStudentFromDatabase(
                    pokemonOwner.getSurName() + " " + pokemonOwner.getFirstName() + " " + pokemonOwner.getFamilyName()
            ) == null) {
                createPokemonOwner(card.getPokemonOwner());
            }
            ownerId = getStudentIdFromDatabase(pokemonOwner.getSurName() + " " + pokemonOwner.getFirstName() + " " + pokemonOwner.getFamilyName());
        }


        String request = "insert into card(id, name, hp, evolves_from, game_set, pokemon_owner, stage,retreat_cost, weakness_type, resistance_type, attack_skills, pokemon_type, regulation_mark, card_number)" +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(request);
        ps.setObject(1, UUID.randomUUID());
        ps.setString(2, card.getName());
        ps.setInt(3, card.getHp());
        ps.setObject(4, evolvesFromId);
        ps.setObject(5, card.getGameSet());
        ps.setObject(6, ownerId);
        ps.setString(7, card.getPokemonStage().name());
        ps.setString(8, card.getRetreatCost());

        if (card.getWeaknessType() != null) {
            ps.setString(9, card.getWeaknessType().name());
        } else {
            ps.setString(9, null);
        }

        if (card.getResistanceType() != null) {
            ps.setString(10, card.getResistanceType().name());
        } else {
            ps.setString(10, null);
        }

        ps.setString(11, new Gson().toJson(card.getSkills()));
        ps.setString(12, card.getPokemonType().name());
        ps.setString(13, String.valueOf(card.getRegulationMark()));
        ps.setString(14, card.getNumber());
        ps.execute();
        ps.close();

    }

    private UUID getStudentIdFromDatabase(String s) throws SQLException {
        String request = "select * from student where \"familyName\" = ? and \"firstName\" = ? and \"patronicName\" = ?";
        String[] namesplit = s.split(" ");
        PreparedStatement ps = connection.prepareStatement(request);
        ps.setString(1, namesplit[0]);
        ps.setString(2, namesplit[1]);
        ps.setString(3, namesplit[2]);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return UUID.fromString(rs.getString("id"));
        }
        ps.execute();
        ps.close();
        return null;
    }

    private UUID getCardIdFromDatabase(String cardname) throws SQLException {
        String request = "select * from card WHERE \"name\" = ?";
        PreparedStatement ps = connection.prepareStatement(request);
        ps.setString(1, cardname);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return UUID.fromString(rs.getString("id"));
        }
        ps.execute();
        ps.close();
        return null;

    }

    @Override
    public void createPokemonOwner(Student owner) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert into student(id, \"familyName\", \"firstName\", \"patronicName\", \"group\") " +
                "values(?, ?, ?, ?, ?)");
        ps.setObject(1, UUID.randomUUID());
        ps.setString(2, owner.getSurName());
        ps.setString(3, owner.getFirstName());
        ps.setString(4, owner.getFamilyName());
        ps.setString(5, owner.getGroup());
        ps.execute();
        ps.close();
    }

}
