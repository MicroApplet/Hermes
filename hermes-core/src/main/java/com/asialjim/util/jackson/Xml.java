package com.asialjim.util.jackson;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public final class Xml {
    public static final Jackson instance = Jackson.instance(new XmlMapper());
}