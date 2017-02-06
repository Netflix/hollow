package com.netflix.vms.transformer.rest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.TransformerContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/vms/pintitleslicer")
public class VMSPinTitleSlicer {
    private final FileStore fileStore;
    private final TransformerContext ctx;

    @Inject
    public VMSPinTitleSlicer(FileStore fileStore, TransformerContext ctx) {
        this.fileStore = fileStore;
        this.ctx = ctx;
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response doGet(@Context HttpServletRequest req) throws ConnectionException {
        return null;
    }

}
