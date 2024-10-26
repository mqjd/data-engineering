from jobs import create_spark_session
from pyspark.sql import functions as sf

if __name__ == "__main__":
    spark = create_spark_session()
    source = spark.read.format("csv") \
        .option("header", True) \
        .option("delimiter", ",") \
        .load("/Users/mq/Desktop/test")

    trans = source.select("model", "failure") \
        .groupby(source.model).agg(sf.sum(source.failure).alias("failures"))
    trans.printSchema()
    trans \
        .write \
        .mode("overwrite") \
        .csv("/Users/mq/Desktop/Q1")
