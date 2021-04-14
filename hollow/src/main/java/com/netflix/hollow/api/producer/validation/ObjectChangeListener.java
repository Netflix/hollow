package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.listener.AnnouncementListener;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @Inject DataMeshClient dataMeshClient;
 *
 * ObjectChangeListener dataMeshCdcListener = new ObjectChangeListener("Movie",
 *   (o ->  {
 *      Movie m = (Movie) o;
 *      m.getMovieId();
 *     dataMeshClient.post(m);
 *   });
 *
 * HollowProducer producer = cinderProducerBuilder.forNamespace("myNamepsace)
 *                          .withListener(dataMeshCdcListener)
 *                          .build();
 */

public class TypeSpecificObjectChangeListener implements AnnouncementListener {

    String typeName;
    private final Function<HollowDataAccess, A> apiFunction;
    private final UnaryOperator<GenericHollowObject> callback;

    public ObjectChangeListener(String typeName,
            Function<HollowDataAccess, A> apiFunction,
            UnaryOperator<GenericHollowObject> callback) {
        this.typeName = typeName;
        this.apiFunction = apiFunction;
        this.callback = callback;
    }

    @Override
    public void onAnnouncementStart(HollowProducer.ReadState readState) {
        // compute +, -, * ordinals for type typeName

        A hollowApi = apiFunction.apply(readState.getStateEngine());
        // for each added ordinal
            // synthesize object by using typeName
            // GenericHollowObject object = hollowObjectFunction.apply(hollowApi, o);
            // or
            // GenericHollowObject object = new GenericHollowObject(object.getDataAccess(), TypeA.class.getSimpleName(), o);
            // callback.apply(o);
    }

}
