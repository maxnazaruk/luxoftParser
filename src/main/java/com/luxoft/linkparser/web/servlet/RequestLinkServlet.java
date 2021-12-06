package com.luxoft.linkparser.web.servlet;

import com.luxoft.linkparser.service.LinkParserService;
import com.luxoft.linkparser.web.util.PageGenerator;
import org.postgresql.util.PSQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RequestLinkServlet extends HttpServlet {
    private LinkParserService linkParserService;

    public RequestLinkServlet(LinkParserService linkParserService) {
        this.linkParserService = linkParserService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PageGenerator pageGenerator = PageGenerator.instance();
        String page = pageGenerator.getPage("link_request.html");
        resp.getWriter().write(page);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PageGenerator pageGenerator = PageGenerator.instance();


        String str = req.getParameter("url");
        try {
            URL url = new URL(str);
            linkParserService.addAll(str);
            resp.sendRedirect("/result");
        } catch (MalformedURLException ex) {
            String UrlIsNotValid = "URL is not valid";
            Map<String, Object> parameters = Map.of("errorMessage", UrlIsNotValid);
            String page = pageGenerator.getPage("link_request.html", parameters);
            resp.getWriter().write(page);
        } catch (SQLException throwables) {
            String NoDbConn = "No Data Base connection!";
            String resultInfo = "You can still see the parse result by typing /result path, but provided data will be taken from the List";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("DBconnection", NoDbConn);
            parameters.put("resultInfo", resultInfo);
            String page = pageGenerator.getPage("link_request.html", parameters);
            resp.getWriter().write(page);
        }
    }
}
