package com.codecool.queststore.DAO;

import com.codecool.queststore.model.DataBaseConnection;
import com.codecool.queststore.model.Quest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestDAO {
    private Connection connection;

    public QuestDAO() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }

    public List<Quest> getQuestsList() {

        List<Quest> questsList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("SELECT * FROM quest");


            while (resultSet.next()) {
                Integer questId = resultSet.getInt("id_quest");
                String questName = resultSet.getString("quest_name");
                String questDescription = resultSet.getString("description");
                String questCategory = resultSet.getString("category");
                Integer questPrice = resultSet.getInt("price");

                Quest quest = new Quest(questId, questName, questDescription, questCategory, questPrice);
                questsList.add(quest);
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return questsList;
    }

    public void addNewQuest(String questName, Integer questValue, String questType,
                            String questDescription) {

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO quest (quest_name, price, category, description) " +
                            "VALUES(?,?,?,?)");
            stmt.setString(1, questName);
            stmt.setInt(2, questValue);
            stmt.setString(3, questType);
            stmt.setString(4, questDescription);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

    public Quest getQuestbyId(Integer questId) {

        Quest editQuest = null;

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM quest " +
                            "WHERE id_quest = ?");
            stmt.setInt(1, questId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                //Integer artifactId = resultSet.getInt("id_artifact");
                String questName = resultSet.getString("quest_name");
                String questDescription = resultSet.getString("description");
                String questCategory = resultSet.getString("category");
                Integer questPrice = resultSet.getInt("price");

                editQuest = new Quest(questId, questName,
                        questDescription, questCategory, questPrice);
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return editQuest;
    }

    public List<String> getQuestCategoryList() {
        List<String> questCategoryList = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("SELECT category FROM quest");

            while (resultSet.next()) {
                String questCategory = resultSet.getString("category");

                if (!questCategoryList.contains(questCategory)){
                    questCategoryList.add(questCategory);
                }

            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return questCategoryList;
    }

    public void updateQuest(Integer questId, String questName, Integer questValue,
                            String questType, String questDescription) {

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE quest " +
                            "SET quest_name = ?, price = ?, category = ?, description = ?" +
                            "WHERE id_quest = ?");

            stmt.setString(1, questName);
            stmt.setInt(2, questValue);
            stmt.setString(3, questType);
            stmt.setString(4, questDescription);
            stmt.setInt(5, questId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

    public List<Quest> getStudentQuests(Integer studentId) {
        List<Quest> studentQuests = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM student_quest " +
                            "JOIN quest ON quest.id_quest = student_quest.id_quest " +
                            "WHERE student_quest.id_student = ?");

            stmt.setInt(1, studentId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Integer questId = resultSet.getInt("id_quest");
                String questName = resultSet.getString("quest_name");
                String questDescription = resultSet.getString("description");
                String questCategory = resultSet.getString("category");
                Integer questPrice = resultSet.getInt("price");

                Quest quest = new Quest(questId, questName, questDescription, questCategory, questPrice);
                studentQuests.add(quest);
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return studentQuests;


    }

    public List<Quest> getStudentQuestsToDo(Integer studentId) {
        List<Quest> studentQuestsList = getQuestsList();
        List<Quest> studentQuestsDone = getStudentQuests(studentId);
        List<Quest> studentQuestsToDo = new ArrayList<>();

        for (Quest quest : studentQuestsList){
            if (!studentQuestsDone.contains(quest)) {
                studentQuestsToDo.add(quest);
            }
        }

        return studentQuestsToDo;
    }



    public void approveQuest(Integer studentId, Integer questId, Integer money) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO student_quest (id_student, id_quest) " +
                            "VALUES(?,?)");
            stmt.setInt(1, studentId);
            stmt.setInt(2, questId);

            stmt.executeUpdate();

            PreparedStatement stmt2 = connection.prepareStatement(
                    "UPDATE student " +
                            "SET current_money =  current_money + ?, total_money = total_money + ? " +
                            "WHERE id_student = ?");
            stmt2.setInt(1, money);
            stmt2.setInt(2, money);
            stmt2.setInt(3, studentId);

            stmt2.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

}
