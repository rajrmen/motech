package org.motechproject.mds.service;

import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;

import java.util.List;

public interface InstanceService {

    List<EntityRecord> getEntityRecordsPaged(Long entityId, int page, int rows, Order order);

    long countRecords(Long entityId);

    Object createInstance(EntityRecord entityRecord);

    List<EntityRecord> getEntityRecords(Long entityId);

    List<FieldInstanceDto> getInstanceFields(Long entityId, Long instanceId);

    List<HistoryRecord> getInstanceHistory(Long instanceId);

    List<PreviousRecord> getPreviousRecords(Long instanceId);

    EntityRecord newInstance(Long entityId);
}
