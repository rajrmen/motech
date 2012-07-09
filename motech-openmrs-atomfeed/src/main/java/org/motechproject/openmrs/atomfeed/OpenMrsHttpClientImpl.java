package org.motechproject.openmrs.atomfeed;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;

public class OpenMrsHttpClientImpl implements OpenMrsHttpClient {

    private final HttpClient httpClient;

    public OpenMrsHttpClientImpl(String openmrsUrl) throws URIException, NullPointerException {
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        httpClient.getHostConfiguration().setHost(new URI(openmrsUrl, false));
    }

    @Override
    public String getOpenMrsAtomFeed() {
        GetMethod get = new GetMethod("/moduleServlet/atomfeed/atomfeed");
        try {
            httpClient.executeMethod(get);
            if (get.getStatusCode() == 200) {
                return get.getResponseBodyAsString();
            } else {
                return "";
            }
        } catch (IOException e) {
            return "";
        }
    }
}
