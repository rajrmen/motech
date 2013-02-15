package org.motechproject.server.messagecampaign.web.util;


import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.LocalDate;

import java.io.IOException;

public class RestDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return RestDateUtil.parseString(jsonParser.getText());
    }
}
