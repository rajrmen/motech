package org.motechproject.commcareintegration.domain;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.commcareintegration.service.CommcareService;
import org.motechproject.commcareintegration.service.CommcareServiceImpl;
import org.motechproject.dao.MotechJsonReader;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class CaseInstance {
	
	private static String jsonString = "\"cases\": [{\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"L0WH2B96HS1QW97ZRZLT6WJSQ\", \"GITCWXU9WMCEEYZJ8R1VBFR7J\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-03-28T14:31:55Z\", \"properties\": {\"question1Test\": \"hadTetanus2\", \"case_name\": \"hadTetanus1\", \"case_type\": \"test_case\", \"date_opened\": \"2012-03-28T10:17:19Z\", \"external_id\": \"hadTetanus1\", \"owner_id\": null}, \"server_date_modified\": \"2012-03-28T14:31:55Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-03-28T10:31:49Z\", \"case_id\": \"DLPQ0G2JYIMLA4K77GDQ530RS\", \"closed\": false, \"indices\": {}}, {\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"M0LPY52RKB5MGS3T7TYLVLJ9M\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-03-27T18:50:27Z\", \"properties\": {\"case_type\": \"test_case\", \"date_opened\": \"2012-03-27T14:50:22Z\", \"external_id\": \"hadTetanus1\", \"owner_id\": null, \"case_name\": \"hadTetanus1\"}, \"server_date_modified\": \"2012-03-27T18:50:27Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-03-27T14:50:22Z\", \"case_id\": \"GKKAPIG9ET2GW6OD9ZJNK4G1B\", \"closed\": false, \"indices\": {}}, {\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"HR1O3RFCEBK7GLBKBAHNCWAWU\", \"DHLY402IQ2SAB13UUSTJNOPZ1\", \"BT8PUWXEA7A56VL15OFDCI6BK\", \"HUTTBV2EHCYWA6RNEWT0VFTLU\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-03-28T15:36:11Z\", \"properties\": {\"monthOfPregnancy\": \"9\", \"case_name\": \"item2 item3 item4\", \"case_type\": \"pregnancy\", \"date_opened\": \"2012-03-28T11:04:20Z\", \"external_id\": \"AnswerHere\", \"owner_id\": null}, \"server_date_modified\": \"2012-03-28T15:36:11Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-03-28T11:36:05Z\", \"case_id\": \"SHMXJDVG3831OT10DRNAIRS5O\", \"closed\": false, \"indices\": {}}]";
	private static String jsonString2 = "{\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"L0WH2B96HS1QW97ZRZLT6WJSQ\", \"GITCWXU9WMCEEYZJ8R1VBFR7J\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-03-28T14:31:55Z\", \"properties\": {\"question1Test\": \"hadTetanus2\", \"case_name\": \"hadTetanus1\", \"case_type\": \"test_case\", \"date_opened\": \"2012-03-28T10:17:19Z\", \"external_id\": \"hadTetanus1\", \"owner_id\": null}, \"server_date_modified\": \"2012-03-28T14:31:55Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-03-28T10:31:49Z\", \"case_id\": \"DLPQ0G2JYIMLA4K77GDQ530RS\", \"closed\": false, \"indices\": {}}";
	private static String jsonString3 = "[{\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"L0WH2B96HS1QW97ZRZLT6WJSQ\", \"GITCWXU9WMCEEYZJ8R1VBFR7J\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-03-28T14:31:55Z\", \"properties\": {\"question1Test\": \"hadTetanus2\", \"case_name\": \"hadTetanus1\", \"case_type\": \"test_case\", \"date_opened\": \"2012-03-28T10:17:19Z\", \"external_id\": \"hadTetanus1\", \"owner_id\": null}, \"server_date_modified\": \"2012-03-28T14:31:55Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-03-28T10:31:49Z\", \"case_id\": \"DLPQ0G2JYIMLA4K77GDQ530RS\", \"closed\": false, \"indices\": {}}, {\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"M0LPY52RKB5MGS3T7TYLVLJ9M\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-03-27T18:50:27Z\", \"properties\": {\"case_type\": \"test_case\", \"date_opened\": \"2012-03-27T14:50:22Z\", \"external_id\": \"hadTetanus1\", \"owner_id\": null, \"case_name\": \"hadTetanus1\"}, \"server_date_modified\": \"2012-03-27T18:50:27Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-03-27T14:50:22Z\", \"case_id\": \"GKKAPIG9ET2GW6OD9ZJNK4G1B\", \"closed\": false, \"indices\": {}}, {\"date_closed\": null, \"domain\": \"usm-motech\", \"xform_ids\": [\"HR1O3RFCEBK7GLBKBAHNCWAWU\", \"DHLY402IQ2SAB13UUSTJNOPZ1\", \"BT8PUWXEA7A56VL15OFDCI6BK\", \"HUTTBV2EHCYWA6RNEWT0VFTLU\"], \"version\": \"1.0\", \"server_date_opened\": \"2012-03-28T15:36:11Z\", \"properties\": {\"monthOfPregnancy\": \"9\", \"case_name\": \"item2 item3 item4\", \"case_type\": \"pregnancy\", \"date_opened\": \"2012-03-28T11:04:20Z\", \"external_id\": \"AnswerHere\", \"owner_id\": null}, \"server_date_modified\": \"2012-03-28T15:36:11Z\", \"user_id\": \"5d622c4336d118a9020d1c758e71f368\", \"date_modified\": \"2012-03-28T11:36:05Z\", \"case_id\": \"SHMXJDVG3831OT10DRNAIRS5O\", \"closed\": false, \"indices\": {}}]";
	String domain;
	String case_id;
	String user_id;
	String closed;
	String date_closed;
	List<String> xform_ids;
	String date_modified;
	String version;
	String server_date_modified;
	String server_date_opened;
	Map<String, String> properties;
	Map<String, Object> indices;
	
	public static void main(String args[]) throws HttpException, IOException {
		MotechJsonReader motechReader = new MotechJsonReader();
		List<CaseInstance> cases = (List<CaseInstance>) motechReader.readFromString(jsonString3, new TypeToken<List<CaseInstance>>() {
        }.getType());
		System.out.println(cases.size());
		CommcareService commcareService = new CommcareServiceImpl(new HttpClient());
		commcareService.getCasesByUserId("5d622c4336d118a9020d1c758e71f368", "usm-motech");
	}

}
