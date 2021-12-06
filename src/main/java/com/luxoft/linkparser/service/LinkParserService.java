package com.luxoft.linkparser.service;

import com.luxoft.linkparser.entity.Link;
import com.luxoft.linkparser.jdbc.JdbcParserDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class LinkParserService {
    private JdbcParserDao jdbcParserDao;
    private List<Link> uniqueURL;

    public LinkParserService(JdbcParserDao jdbcParserDao) {
        this.jdbcParserDao = jdbcParserDao;

    }

    private static int linksCounter(Link link){
        int numberCount = 0;
        try {
            Document document = Jsoup.connect(link.getUrl()).get();

            Elements links = document.select("a[href]");
            Elements media = document.select("[src]");
            URL url = new URL(link.getUrl());

            for(Element href: links) {
                String absoluteUrl = href.attr("abs:href");
                if(!absoluteUrl.contains(url.getHost()) && !absoluteUrl.equals("")) {
                    numberCount++;
                }
            }

            for(Element src: media) {
                String absouleSrc = src.attr("abs:src");
                if(!absouleSrc.contains(url.getHost()) && !absouleSrc.equals("")){
                    numberCount++;
                }
            }
            link.setNumber(numberCount);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return numberCount;
    }

    private void getlinks(Link page) {
        try {
            Document doc = Jsoup.connect(page.getUrl()).get();
            Elements links = doc.select("a[href]");

            if(links.size() > 10){
                links.subList(10, links.size()).clear();
            }
            if (links.isEmpty()) {
                return;
            }
            URL url = new URL(page.getUrl());
            links.stream().map((link) -> link.attr("abs:href")).forEachOrdered((this_url) -> {

                if(this_url.contains(url.getHost()) && !checkDuplicates(uniqueURL, this_url)) {
                    Link nextPage = Link.builder().
                            url(this_url)
                            .depth(page.getDepth() + 1)
                            .number(0)
                            .build();

                    uniqueURL.add(nextPage);
                }
            });
            try {
                getlinks(uniqueURL.get(uniqueURL.indexOf(page) + 1));
            }catch (IndexOutOfBoundsException ex){
                return;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean checkDuplicates(List<Link> links, String url){
        for (Link l : links){
            if(l.getUrl().equals(url)){
                return true;
            }
        }
        return false;
    }

    public void addAll(String url) throws SQLException {
        uniqueURL = new ArrayList<>();
        Link link = Link.builder().
                url(url)
                .depth(0)
                .number(0)
                .build();
        uniqueURL.add(link);
        getlinks(link);

        // Clear table from previous result
        jdbcParserDao.clearTable();

        for (Link page : uniqueURL){
            page.setNumber(linksCounter(page));
            jdbcParserDao.add(page);
        }
    }

    public List<Link> findAll() throws SQLException {
        List<Link> links = jdbcParserDao.findAll();
        return links;
    }

    public List<Link> returnList() {
        int id = 1;
        for (Link page : uniqueURL){
            page.setId(id++);
        }
        return uniqueURL;
    }
}
