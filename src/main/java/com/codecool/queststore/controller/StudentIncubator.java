package com.codecool.queststore.controller;

import com.codecool.queststore.model.Session;
import com.codecool.queststore.model.Student;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.OutputStream;

public class StudentIncubator implements HttpHandler {
    public void handle(HttpExchange httpExchange) throws IOException {

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/incubator.twig");
        JtwigModel model = JtwigModel.newModel();

        if (Session.guard(httpExchange)) {
            Student loggedUser = (Student) Session.getLoggedUser(httpExchange);

//            InventoryDAO inventoryDAO = new InventoryDAO();
//            usedInventory = inventoryDAO.getUsedInventory(loggedUser.getStudentId());
//
//            model.with("usedInventory", usedInventory);
//            model.with("userName", loggedUser.getFirstName());
//            model.with("currentMoney", loggedUser.getCurrentMoney());
//            model.with("totalMoney", loggedUser.getTotalMoney());
        }

        String response = template.render(model);

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
