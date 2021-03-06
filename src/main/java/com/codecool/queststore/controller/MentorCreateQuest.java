package com.codecool.queststore.controller;

import com.codecool.queststore.DAO.ClassDAO;
import com.codecool.queststore.DAO.QuestDAO;
import com.codecool.queststore.DAO.UserDAO;
import com.codecool.queststore.model.CoolClass;
import com.codecool.queststore.model.Mentor;
import com.codecool.queststore.model.Quest;
import com.codecool.queststore.model.Session;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


public class MentorCreateQuest implements HttpHandler {

    private QuestDAO questDAO;

    public MentorCreateQuest() {

        questDAO = new QuestDAO();
    }
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        String response = "";

        if (Session.guard(httpExchange, "mentor")) {

            Mentor loggedUser = (Mentor) Session.getLoggedUser(httpExchange);

            if (method.equals("GET")) {

                JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor/create-quest.twig");
                JtwigModel model = JtwigModel.newModel();

                model.with("userName", loggedUser.getFirstName());
                response = template.render(model);
            }

            if (method.equals("POST")) {

                InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();

                Map<String, String> inputs = parseFormData(formData);

                String questName = inputs.get("name");
                String questValueString = inputs.get("surname");
                String questType = inputs.get("standard");
                String questDescription = inputs.get("description");
                Integer questValue = Integer.valueOf(questValueString);

                //System.out.println(questName + questValueString + questType + questDescription);

                questDAO.addNewQuest(questName, questValue, questType, questDescription);

                JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mentor/mentor-top.twig");
                JtwigModel model = JtwigModel.newModel();
                response = template.render(model);

                httpRedirectTo("/mentor", httpExchange);

            }
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<String, String>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }

    private void httpRedirectTo(String dest, HttpExchange httpExchange) throws IOException {
        String hostPort = httpExchange.getRequestHeaders().get("host").get(0);
        httpExchange.getResponseHeaders().set("Location", "http://" + hostPort + dest);
        httpExchange.sendResponseHeaders(302, -1);
    }

}
