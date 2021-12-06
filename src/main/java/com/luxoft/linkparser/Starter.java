package com.luxoft.linkparser;

import com.luxoft.linkparser.jdbc.JdbcParserDao;
import com.luxoft.linkparser.service.LinkParserService;
import com.luxoft.linkparser.web.servlet.RequestLinkServlet;
import com.luxoft.linkparser.web.servlet.ShowParserResultServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Starter {
    public static void main(String[] args) throws Exception {
        // dao
        JdbcParserDao jdbcParserDao = new JdbcParserDao();

        // service
        LinkParserService linkParserService = new LinkParserService(jdbcParserDao);

        // servlet
        RequestLinkServlet requestLinkServlet = new RequestLinkServlet(linkParserService);
        ShowParserResultServlet showAllReviewsRequestServlet = new ShowParserResultServlet(linkParserService);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(requestLinkServlet), "/");
        context.addServlet(new ServletHolder(showAllReviewsRequestServlet), "/result");

        Server server = new Server(8080);
        server.setHandler(context);

        server.start();
    }
}
