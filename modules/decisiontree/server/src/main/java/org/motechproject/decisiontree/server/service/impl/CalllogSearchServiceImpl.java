package org.motechproject.decisiontree.server.service.impl;

import org.motechproject.decisiontree.core.CallDetail;
import org.motechproject.decisiontree.server.domain.CallDetailRecord;
import org.motechproject.decisiontree.server.repository.AllCallDetailRecords;
import org.motechproject.decisiontree.server.service.CalllogSearchParameters;
import org.motechproject.decisiontree.server.service.CalllogSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("calllogSearchService")
public class CalllogSearchServiceImpl implements CalllogSearchService {
    private AllCallDetailRecords allCallDetailRecords;
    private static final int PAGE_SIZE = 10;

    @Autowired
    public CalllogSearchServiceImpl(AllCallDetailRecords allCallDetailRecords) {
        this.allCallDetailRecords = allCallDetailRecords;
    }


    @Override
    public List<CallDetail> search(CalllogSearchParameters searchParameters) {
        List<String> dispositions = new ArrayList<>();
        if (searchParameters.getAnswered()) {
            dispositions.add(CallDetailRecord.Disposition.ANSWERED.name());
        }
        if (searchParameters.getBusy()) {
            dispositions.add(CallDetailRecord.Disposition.BUSY.name());
        }
        if (searchParameters.getFailed()) {
            dispositions.add(CallDetailRecord.Disposition.FAILED.name());
        }
        if (searchParameters.getNoAnswer()) {
            dispositions.add(CallDetailRecord.Disposition.NO_ANSWER.name());
        }
        if (searchParameters.getUnknown()) {
            dispositions.add(CallDetailRecord.Disposition.UNKNOWN.name());
        }
        List<CallDetail> callLogs = allCallDetailRecords.search(searchParameters.getPhoneNumber(),
                searchParameters.getFromDateAsDateTime(),
                searchParameters.getToDateAsDateTime(),
                searchParameters.getMinDuration(),
                searchParameters.getMaxDuration(),
                dispositions, searchParameters.getPage(), PAGE_SIZE);
        return callLogs;
    }

}
