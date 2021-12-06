package com.luxoft.linkparser.web.servlet;

import com.luxoft.linkparser.entity.Link;
import com.luxoft.linkparser.service.LinkParserService;
import com.luxoft.linkparser.web.util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ShowParserResultServlet extends HttpServlet {
    private LinkParserService linkParserService;

    public ShowParserResultServlet(LinkParserService linkParserService) {
        this.linkParserService = linkParserService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Link> links;
        HashMap<String, Object> parameters = new HashMap<>();
        try {
            links = linkParserService.findAll();
        } catch (SQLException throwables) {
            String NoDbConn = "No Data Base connection!";
            parameters.put("DBconnection", NoDbConn);
            links = linkParserService.returnList();
        }
        URL url = new URL(links.get(0).getUrl());
        String domain = url.getProtocol() + "://" + url.getAuthority();

        for (Link link : links){
            url = new URL(link.getUrl());
            link.setUrl(link.getUrl().replaceAll(url.getProtocol() + "://" + url.getAuthority(), ""));
        }
        PageGenerator pageGenerator = PageGenerator.instance();

        parameters.put("links", links);
        parameters.put("domain", domain);

        String page = pageGenerator.getPage("result.html", parameters);
        resp.getWriter().write(page);
    }
}
