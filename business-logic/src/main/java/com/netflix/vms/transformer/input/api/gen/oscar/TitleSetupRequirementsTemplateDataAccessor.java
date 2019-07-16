package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class TitleSetupRequirementsTemplateDataAccessor extends AbstractHollowDataAccessor<TitleSetupRequirementsTemplate> {

    public static final String TYPE = "TitleSetupRequirementsTemplate";
    private OscarAPI api;

    public TitleSetupRequirementsTemplateDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (OscarAPI)consumer.getAPI();
    }

    public TitleSetupRequirementsTemplateDataAccessor(HollowReadStateEngine rStateEngine, OscarAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public TitleSetupRequirementsTemplateDataAccessor(HollowReadStateEngine rStateEngine, OscarAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public TitleSetupRequirementsTemplateDataAccessor(HollowReadStateEngine rStateEngine, OscarAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public TitleSetupRequirementsTemplate getRecord(int ordinal){
        return api.getTitleSetupRequirementsTemplate(ordinal);
    }

}