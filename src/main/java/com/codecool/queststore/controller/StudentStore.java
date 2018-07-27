package com.codecool.queststore.controller;

import com.codecool.queststore.DAO.InventoryDAO;
import com.codecool.queststore.model.Artifact;
import com.codecool.queststore.model.Session;
import com.codecool.queststore.model.Student;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class StudentStore implements HttpHandler {
    public void handle(HttpExchange httpExchange) throws IOException {
        //System.out.println("STUDENT STORE");

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/store.twig");
        JtwigModel model = JtwigModel.newModel();

        if (Session.guard(httpExchange, "student")) {

            Student loggedUser = (Student) Session.getLoggedUser(httpExchange);

            InventoryDAO inventoryDAO = new InventoryDAO();
            List<Artifact> storeInventory = inventoryDAO.getStoreInventory();
//            System.out.println(storeInventory.get(0).getArtifactPrice());
//            System.out.println(storeInventory.get(1).getArtifactPrice());
//            System.out.println(storeInventory.get(2).getArtifactPrice());

            model.with("storeInventory", storeInventory);
            model.with("userName", loggedUser.getFirstName());
            model.with("currentMoney", loggedUser.getCurrentMoney());
            model.with("totalMoney", loggedUser.getTotalMoney());
        }

        String response = template.render(model);

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
