package com.jgit.gitwithjava;

import com.jgit.gitwithjava.custom.service.GitCustomService;
import org.eclipse.jgit.api.Git;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class GitwithJavaApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GitwithJavaApplication.class, args);
        /*File file = new File(DefaultCredentials.getRootFolder()+"Videos/springBootProject/GitwithJava");
        Git git = Git.open(file);
        GitCustomService gitCustomService = new GitCustomService();
        gitCustomService.gitPush(git);
        git.close();*/
/*
        // Create DocumentBuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Create DOM Document
        Document doc = builder.newDocument();

        // Create root element
        Element rootElement = doc.createElement("employees");
        doc.appendChild(rootElement);

        Element employee = doc.createElement("employee");
        employee.setAttribute("id", String.valueOf(Math.random()));

        Element nameElement = doc.createElement("name");
        nameElement.appendChild(doc.createTextNode("kflsfklsf"));
        employee.appendChild(nameElement);

        Element positionElement = doc.createElement("position");
        positionElement.appendChild(doc.createTextNode("software"));
        employee.appendChild(positionElement);

        Element salaryElement = doc.createElement("salary");
        salaryElement.appendChild(doc.createTextNode("20000"));
        employee.appendChild(salaryElement);
        rootElement.appendChild(employee);


        // Write the DOM document to a file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(new File(DefaultCredentials.getRootFolder())));
        transformer.transform(source, result);*/
    }
}
