package com.netflix.vms.transformer.rest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.override.InputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.OutputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.PinTitleHelper;
import com.netflix.vms.transformer.util.VMSProxyUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

@Singleton
@Path("/vms/pintitleslicer")
public class VMSPinTitleSlicer {
    private static final String localBlobStore = "/mnt/VMSPinTitleSlicer";

    private final FileStore fileStore;
    private final TransformerContext ctx;

    @Inject
    public VMSPinTitleSlicer(FileStore fileStore, TransformerContext ctx) {
        this.fileStore = fileStore;
        this.ctx = ctx;
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response doGet(@Context HttpServletRequest req,
            @QueryParam("isProd") boolean isProd,
            @QueryParam("isOutput") boolean isOutput,
            @QueryParam("vip") String vip,
            @QueryParam("version") long version,
            @QueryParam("topNodes") String topNodesStr) throws Exception {

        File slicedFile = null;
        int[] topNodes = PinTitleHelper.parseTopNodes(topNodesStr);
        String proxyURL = VMSProxyUtil.getProxyURL(isProd);
        if (isOutput) {
            OutputSlicePinTitleProcessor p = new OutputSlicePinTitleProcessor(vip, proxyURL, localBlobStore, ctx);
            p.setPinTitleFileStore(fileStore);
            slicedFile = p.fetchOutputSlice(version, topNodes);
        } else {
            InputSlicePinTitleProcessor p = new InputSlicePinTitleProcessor(vip, proxyURL, localBlobStore, ctx);
            p.setPinTitleFileStore(fileStore);
            slicedFile = p.fetchInputSlice(version, topNodes);
        }

        return Response.ok(new FileStreamingOutput(slicedFile)).build();
    }

    private static class FileStreamingOutput implements StreamingOutput {
        private final File file;

        FileStreamingOutput(File file) {
            this.file = file;
        }

        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
            FileInputStream is = new FileInputStream(file);
            IOUtils.copyLarge(is, output);
        }
    }
}