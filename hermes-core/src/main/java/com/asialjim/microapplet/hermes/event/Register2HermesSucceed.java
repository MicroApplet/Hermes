package com.asialjim.microapplet.hermes.event;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;

@Data
@Accessors(chain = true)
public class Register2HermesSucceed {
    Map<String, Set<String>> serviceSubTypes;
}