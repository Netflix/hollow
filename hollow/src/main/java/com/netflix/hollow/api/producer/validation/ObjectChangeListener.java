package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.listener.AnnouncementListener;
import java.util.function.BiFunction;
import java.util.function.Supplier;
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
    private final UnaryOperator<Object> callback;

    public ObjectChangeListener(String typeName, UnaryOperator<Object> callback) {
        this.typeName = typeName;
        this.callback = callback;
    }

    @Override
    public void onAnnouncementStart(HollowProducer.ReadState readState) {
        // compute +, -, * ordinals for type typeName

        // for each added ordinal
        // synthesize object by using typeName
            // Object o = hollowApi.forType(typeName).getRecord(ordinal)
            // callback.apply(o);
    }

}
