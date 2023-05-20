package com.joe.ksqldb.udf.aggregate.factory;

import com.joe.ksqldb.udf.aggregate.LatestByTimeUdaf;
import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;

@UdafDescription(
        name = "LATEST_BY_TIME",
        description = "Pick latest event based on time param. Type has to be STRUCT<VALUE ANY, TIME BIGINT>"
)
public class LatestByTimeUdafFactory {

    @UdafFactory(
            description = "Generic function to return latest by time"
    )
    public static <T, U> Udaf<T, T, U> generic() {
        return new LatestByTimeUdaf<>();
    }
}
