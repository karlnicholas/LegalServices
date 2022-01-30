package com.github.karlnicholas.legalservices.opinionview.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    //    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE;

    @Override
    public LocalDate unmarshal(String xml) throws Exception {
        return LocalDate.parse(xml, dateFormat);
    }

    @Override
    public String marshal(LocalDate object) throws Exception {
        return dateFormat.format(object);
    }

}