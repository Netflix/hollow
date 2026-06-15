package com.netflix.hollow.diff.ui;

import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.report.HollowDiffReportMetadata;
import com.netflix.hollow.tools.diff.report.HollowDiffReportOptions;
import com.netflix.hollow.ui.HollowUIWebServer;
import com.netflix.hollow.ui.HttpHandlerWithServletSupport;

class DiffUIWebServer extends HollowUIWebServer implements DiffUIServer {
    private final HollowDiffUIRouter router;

    public DiffUIWebServer(HollowDiffUIRouter router, int port) {
        super(new HttpHandlerWithServletSupport(router), port);
        this.router = router;
    }

    @Override
    public HollowDiffUI addDiff(
            String diffPath,
            HollowDiff diff,
            String fromBlobName,
            String toBlobName,
            HollowDiffReportMetadata metadata,
            HollowDiffReportOptions options) {
        return router.addDiff(diffPath, diff, fromBlobName, toBlobName, metadata, options);
    }
}
