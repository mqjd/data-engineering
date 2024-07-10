from src.main import create_spark_session

if __name__ == "__main__":
    spark = create_spark_session()
    source = spark.read.format("csv") \
        .option("header", True) \
        .option("delimiter", ",") \
        .load("/Users/mq/Desktop/data_Q1_2019")
    source \
        .write \
        .mode("overwrite") \
        .partitionBy("date") \
        .parquet("/Users/mq/Desktop/Q1")
