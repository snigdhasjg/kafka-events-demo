package com.joe.ksqldb.udf.aggregate;

import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.schema.ksql.SchemaConverters;
import io.confluent.ksql.schema.ksql.SqlArgument;
import io.confluent.ksql.schema.ksql.types.SqlType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LatestByTimeUdaf<T, U> implements Udaf<T, T, U> {

    Schema inputStructSchema;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<SqlType> aggSqlTypeOptional;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<SqlType> returnSqlTypeOptional;

    @Override
    @SuppressWarnings("unchecked")
    public T initialize() {
        return (T) new Struct(inputStructSchema).put("TIME", -1L);
    }

    @Override
    public T aggregate(T current, T aggregate) {
        Struct currentStruct = (Struct) current;
        Struct aggregateStruct = (Struct) aggregate;
        if (currentStruct.getInt64("TIME") < aggregateStruct.getStruct("VAL").getInt64("TIME")) {
            return aggregate;
        }
        return current;
    }

    @Override
    public T merge(T aggOne, T aggTwo) {
        Struct aggOneStruct = (Struct) aggOne;
        Struct aggTwoStruct = (Struct) aggTwo;
        if (aggOneStruct.getInt64("TIME") < aggTwoStruct.getInt64("TIME")) {
            return aggTwo;
        }
        return aggOne;
    }

    @Override
    @SuppressWarnings("unchecked")
    public U map(T agg) {
        Struct aggStruct = (Struct) agg;
        return (U) aggStruct.get("VALUE");
    }

    @Override
    public void initializeTypeArguments(List<SqlArgument> argTypeList) {
        SqlType inputSqlType = argTypeList.get(0).getSqlTypeOrThrow();
        inputStructSchema = SchemaConverters.sqlToConnectConverter().toConnectSchema(inputSqlType);
        if (!isValidInputSchema(inputStructSchema)) {
            throw new RuntimeException("Unable to match schema STRUCT<VALUE ANY, TIME BIGINT>");
        }
        aggSqlTypeOptional = Optional.of(inputSqlType);
        returnSqlTypeOptional = Optional.of(SchemaConverters.connectToSqlConverter().toSqlType(inputStructSchema.field("VALUE").schema()));
    }

    @Override
    public Optional<SqlType> getAggregateSqlType() {
        return aggSqlTypeOptional;
    }

    @Override
    public Optional<SqlType> getReturnSqlType() {
        return returnSqlTypeOptional;
    }

    private boolean isValidInputSchema(Schema schema) {
        if (Schema.Type.STRUCT.equals(schema.type()) && nonNull(schema.field("VALUE")) && nonNull(schema.field("TIME"))) {
            Field timeField = schema.field("TIME");
            return Schema.Type.INT64.equals(timeField.schema().type());
        }
        return false;
    }
}
