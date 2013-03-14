package org.motechproject.cmslite.api.web;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ektorp.AttachmentInputStream;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.Content;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ResourceController {

    private static final String NOT_FOUND_RESPONSE = "Content not found";

    private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private CMSLiteService cmsLiteService;

    @RequestMapping(value = "/resource", method = RequestMethod.GET)
    @ResponseBody
    public List<ContentDto> getContents() {
        List<Content> contents = cmsLiteService.getAllContents();
        List<ContentDto> contentDtos = new ArrayList<>(contents.size());

        for (final Content content : contents) {
            ContentDto dto = (ContentDto) CollectionUtils.find(contentDtos, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return object instanceof ContentDto &&
                            ((ContentDto) object).getName().equalsIgnoreCase(content.getName()) &&
                            ((ContentDto) object).getType().equalsIgnoreCase(content.getType());
                }
            });

            if (dto == null) {
                contentDtos.add(new ContentDto(content));
            } else {


                dto.addLanguage(content.getLanguage());
            }
        }

        return contentDtos;
    }

    @RequestMapping(value = "/resource/{type}/{language}/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Content getContent(@PathVariable String type, @PathVariable String language, @PathVariable String name) throws ContentNotFoundException {
        Content content;

        switch (type) {
            case "stream":
                content = cmsLiteService.getStreamContent(language, name);
                break;
            case "string":
                content = cmsLiteService.getStringContent(language, name);
                break;
            default:
                content = null;
        }

        return content;
    }

    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addContent(@RequestParam(value = "name") String name,
                           @RequestParam(value = "language") String language,
                           @RequestParam(value = "value", required = false) String value,
                           @RequestParam(value = "contentFile", required = false) MultipartFile contentFile) throws CMSLiteException, IOException {
        if (StringUtils.isNotBlank(value)) {
            cmsLiteService.addContent(new StringContent(language, name, value));
        } else if (null != contentFile) {
            InputStream inputStream = contentFile.getInputStream();
            String checksum = DigestUtils.md5Hex(contentFile.getBytes());
            String contentType = contentFile.getContentType();

            cmsLiteService.addContent(new StreamContent(language,name, inputStream, checksum, contentType));
        } else {
            throw new CMSLiteException("Can't recognize content type");
        }
    }

    @RequestMapping(value = "/resource/{type}/{language}/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeContent(@PathVariable String type, @PathVariable String language, @PathVariable String name) throws ContentNotFoundException {
        switch (type) {
            case "stream":
                cmsLiteService.removeStreamContent(language, name);
                break;
            case "string":
                cmsLiteService.removeStringContent(language, name);
                break;
            default:
        }
    }

    @RequestMapping(value = "/stream/{language}/{name}", method = RequestMethod.GET)
    public void getStreamContent(@PathVariable String language, @PathVariable String name, HttpServletResponse response)
            throws IOException {
        LOG.info(String.format("Getting resource for : stream:%s:%s", language, name));

        OutputStream out = null;
        AttachmentInputStream contentStream = null;

        try {
            out = response.getOutputStream();

            contentStream = (AttachmentInputStream) cmsLiteService.getStreamContent(language, name).getInputStream();

            response.setContentLength((int) contentStream.getContentLength());
            response.setContentType(contentStream.getContentType());
            response.setHeader("Accept-Ranges", "bytes");
            response.setStatus(HttpServletResponse.SC_OK);

            IOUtils.copy(contentStream, out);
        } catch (ContentNotFoundException e) {
            LOG.error(String.format("Content not found for : stream:%s:%s%n:%s", language, name,
                    Arrays.toString(e.getStackTrace())));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, NOT_FOUND_RESPONSE);
        } finally {
            IOUtils.closeQuietly(contentStream);
            IOUtils.closeQuietly(out);
        }
    }

    @RequestMapping(value = "/string/{language}/{name}", method = RequestMethod.GET)
    public void getStringContent(@PathVariable String language, @PathVariable String name, HttpServletResponse response)
            throws IOException {
        LOG.info(String.format("Getting resource for : string:%s:%s", language, name));

        PrintWriter writer = null;

        try {
            writer = response.getWriter();

            StringContent stringContent = cmsLiteService.getStringContent(language, name);

            response.setContentLength(stringContent.getValue().length());
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);

            writer.print(stringContent.getValue());
        } catch (ContentNotFoundException e) {
            LOG.error(String.format("Content not found for : string:%s:%s%n:%s", language, name,
                    Arrays.toString(e.getStackTrace())));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, NOT_FOUND_RESPONSE);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @ExceptionHandler({ContentNotFoundException.class, CMSLiteException.class, IOException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) {
        return e.getMessage();
    }

}
