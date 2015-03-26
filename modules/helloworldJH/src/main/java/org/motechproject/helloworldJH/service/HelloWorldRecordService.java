package org.motechproject.helloworldJH.service;

import java.util.List;

import org.motechproject.helloworldJH.domain.HelloWorldRecord;

/**
 * Service interface for CRUD on simple repository records.
 */
public interface HelloWorldRecordService {

    void create(String name, String message);

    void add(HelloWorldRecord record);

    HelloWorldRecord findRecordByName(String recordName);

    List<HelloWorldRecord> getRecords();

    void delete(HelloWorldRecord record);

    void update(HelloWorldRecord record);
}
