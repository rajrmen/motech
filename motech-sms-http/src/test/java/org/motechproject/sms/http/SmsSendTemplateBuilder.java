package org.motechproject.sms.http;

import org.motechproject.sms.http.domain.HttpMethodType;
import org.motechproject.sms.http.template.Request;
import org.motechproject.sms.http.template.Response;
import org.motechproject.sms.http.template.SmsHttpTemplate;

import java.util.Map;

public class SmsSendTemplateBuilder {

    private SmsHttpTemplate smsHttpTemplate = new SmsHttpTemplate();

    public static class RequestBuilder {

        private Request request = new Request();

        public static RequestBuilder create() {
            return new RequestBuilder();
        }

        public RequestBuilder withUrlPath(String urlPath) {
            request.setUrlPath(urlPath);
            return this;
        }

        public RequestBuilder withBodyParameters(Map<String, String> bodyParameters) {
            request.setBodyParameters(bodyParameters);
            return this;
        }

        public RequestBuilder withType(HttpMethodType type) {
            request.setType(type);
            return this;
        }

        public RequestBuilder withQueryParameters(Map<String, String> queryParameters) {
            request.setQueryParameters(queryParameters);
            return this;
        }

        public Request build() {
            return request;
        }

        public RequestBuilder withRecipientSeperator(String seperator) {
            request.setRecipientsSeparator(seperator);
            return this;
        }
    }

    public static class ResponseBuilder {

        private Response response = new Response();

        public ResponseBuilder withSuccess(String success) {
            response.setSuccess(success);
            return this;
        }

        public static ResponseBuilder create() {
            return new ResponseBuilder();
        }

        public Response build() {
            return response;
        }
    }

    public SmsSendTemplateBuilder withRequest(Request request) {
        smsHttpTemplate.getOutgoing().setRequest(request);
        return this;
    }

    public SmsSendTemplateBuilder withResponse(Response response) {
        smsHttpTemplate.getOutgoing().setResponse(response);
        return this;
    }

    public static SmsSendTemplateBuilder create() {
        return new SmsSendTemplateBuilder();
    }

    public SmsHttpTemplate build() {
        return smsHttpTemplate;
    }
}
