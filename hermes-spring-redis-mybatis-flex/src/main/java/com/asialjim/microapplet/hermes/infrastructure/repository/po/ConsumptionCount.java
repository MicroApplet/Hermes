package com.asialjim.microapplet.hermes.infrastructure.repository.po;

import lombok.Data;

@Data
public class ConsumptionCount {
    private long total = 0;
    private long pending = 0;
    private long processing = 0;
    private long succeeded = 0;
    private long failed = 0;
    private long retrying = 0;
    private long dead = 0;
    private long archive = 0;
}
