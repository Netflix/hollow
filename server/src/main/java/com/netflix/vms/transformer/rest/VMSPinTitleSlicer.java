package com.netflix.vms.transformer.rest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.EnvironmentEnum;
import com.netflix.gutenberg.s3access.S3Direct;
import com.netflix.vms.transformer.DynamicBusinessLogic;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.common.BusinessLogic;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.override.InputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.OutputSlicePinTitleProcessor;
import com.netflix.vms.transformer.override.PinTitleHelper;
import com.netflix.vms.transformer.override.PinTitleProcessor;
import com.netflix.vms.transformer.override.PinTitleProcessor.TYPE;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This is a REST endpoint that is exposed to allow slicing transformer output blobs.
 */
@Singleton
@Path("/vms/pintitleslicer")
public class VMSPinTitleSlicer {
    private final Set<EnvironmentEnum> supportedEnvs = EnumSet.of(EnvironmentEnum.test);
    private final File localBlobStore;
    private final Supplier<CinderConsumerBuilder> cinderConsumerBuilder;
    private final S3Direct s3Direct;
    private final TransformerContext ctx;
    private final DynamicBusinessLogic dynamicLogic;

    @Inject
    public VMSPinTitleSlicer(Supplier<CinderConsumerBuilder> cinderConsumerBuilder, S3Direct s3Direct,
            DynamicBusinessLogic dynamicLogic) {

        this.cinderConsumerBuilder = cinderConsumerBuilder;
        this.s3Direct = s3Direct;
        ctx = new SimpleTransformerContext();
        this.dynamicLogic = dynamicLogic;

        localBlobStore = new File(System.getProperty("java.io.tmpdir"), "VMSPinTitleSlicer");
        if (!localBlobStore.exists()) localBlobStore.mkdirs();
    }

    @GET
    @Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN })
    public Response doGet(@Context HttpServletRequest req,
            @QueryParam("prod") boolean isProd,
            @QueryParam("output") boolean isOutput,
            @QueryParam("namespace") String namespace,
            @QueryParam("version") String versionStr,
            @QueryParam("topnodes") String topNodesStr) throws Exception, Throwable {

        // Validate Requirement
        EnvironmentEnum envEnum = NetflixConfiguration.getEnvironmentEnum();
        if (!supportedEnvs.contains(envEnum)) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(String.format("ERROR: Feature is not supported in %s - Supported envs:%s.  Specified params[prod=%s, output=%s, namespace=%s, version=%s, topnodes=%s], localBlobStore=%s", envEnum, supportedEnvs, isProd, isOutput, namespace, versionStr, topNodesStr, localBlobStore))
                    .type(MediaType.TEXT_PLAIN_TYPE).build();
        } else if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(topNodesStr) || versionStr == null) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(String.format("ERROR: namespace, topnodes and version parameters are required. Specified params[prod=%s, output=%s, namespace=%s, version=%s, topnodes=%s], localBlobStore=%s", isProd, isOutput, namespace, versionStr, topNodesStr, localBlobStore))
                    .type(MediaType.TEXT_PLAIN_TYPE).build();
        }

        DynamicBusinessLogic.CurrentBusinessLogicHolder logicAndMetadata = dynamicLogic.getLogicAndMetadata();
        BusinessLogic businessLogic = logicAndMetadata.getLogic();

        try {
            // cleanup files older than 7 days or oldest files to keep the max temp files to 20
            long version = Long.parseLong(versionStr);
            int[] topNodes = PinTitleHelper.parseTopNodes(topNodesStr);

            // Determine whether to process input or output data slicing
            TYPE type = isOutput ? TYPE.OUTPUT : TYPE.INPUT;
            PinTitleProcessor processor = isOutput ?
                    new OutputSlicePinTitleProcessor(cinderConsumerBuilder, s3Direct, namespace, localBlobStore.getPath(), isProd, ctx, businessLogic)
                    : new InputSlicePinTitleProcessor(cinderConsumerBuilder, s3Direct, namespace, localBlobStore.getPath(), isProd, ctx, businessLogic);
            File slicedFile = processor.getFile(namespace, type, version, topNodes);

            if (!slicedFile.exists()) { // Perform slicing if it does not exists
                synchronized (this) {
                    cleanupOldFiles(localBlobStore, 7, 20);
                    slicedFile = processor.process(type, version, topNodes);
                }
            }

            return Response.ok(new FileStreamingOutput(slicedFile), MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .header("content-disposition", "attachment; filename = " + slicedFile.getName()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(String.format("ERROR: Failed to slice data with specified params[prod=%s, output=%s, namespace=%s, version=%s, topnodes=%s] "
                            + "- Make sure version and namespace are valid. Exception=%s", isProd, isOutput, namespace, versionStr, topNodesStr, ex))
                    .type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    private void cleanupOldFiles(File dir, int daysOld, int maxFilesToRetain) {
        TreeSet<File> sortedFiles = new TreeSet<>(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.lastModified() == o2.lastModified()) return 0;
                return o1.lastModified() <= o2.lastModified() ? -1 : 1;
            }
        });

        // Delete older files than daysOld
        long purgeTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000);
        for (File file : dir.listFiles()) {
            sortedFiles.add(file);
            if (file.lastModified() < purgeTime) {
                file.delete();
            }
        }

        // Delete oldest files to meet maxFilesToRetain
        if (sortedFiles.size() > maxFilesToRetain) {
            int numToDelete = sortedFiles.size() - maxFilesToRetain;
            for (File file : sortedFiles) {
                if (numToDelete-- <= 0) break;
                file.delete();
            }
        }
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
