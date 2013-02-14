//CHECKSTYLE:OFF
package org.motechproject.ivr.calllog.tmp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

@SuppressWarnings("PMD")
public class ResourceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String target = req.getPathInfo();
        if (target == null)
            target = "/";
        if (target.equals("/"))
            target += "index.html";

        String resName = target.substring(target.lastIndexOf("/") + 1, target.length());

        URL url = new File("/Users/akulasm/code/motech/motech/modules/ivr/motech-calllog/src/main/resources/webapp" + target).toURL();
        System.out.println(url.toString());

        if (url == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String contentType = getServletContext().getMimeType(resName);
            if (contentType != null) {
                res.setContentType(contentType);
            }
            copyResource(url, res);
        }
    }

    private void copyResource(URL url, HttpServletResponse res) throws IOException {
        OutputStream os = null;
        InputStream is = null;

        try {
            os = res.getOutputStream();
            is = url.openStream();

            int len = 0;
            byte[] buf = new byte[1024];
            int n;

            while ((n = is.read(buf, 0, buf.length)) >= 0) {
                os.write( buf, 0, n );
                len += n;
            }
            res.setContentLength(len);
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
//CHECKSTYLE:ON
