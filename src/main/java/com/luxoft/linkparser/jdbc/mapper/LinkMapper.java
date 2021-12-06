package com.luxoft.linkparser.jdbc.mapper;

import com.luxoft.linkparser.entity.Link;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LinkMapper {
    public Link mapResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String url = resultSet.getString("url");
        int depth = resultSet.getInt("depth");
        int number = resultSet.getInt("number");

        Link link = Link.builder().
                id(id)
                .url(url)
                .depth(depth)
                .number(number)
                .build();

        return link;
    }
}
