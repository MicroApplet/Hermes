package com.asialjim.util.jackson;

import com.fasterxml.jackson.databind.json.JsonMapper;

public final class Json {
    public static final Jackson instance = Jackson.instance(new JsonMapper());
}