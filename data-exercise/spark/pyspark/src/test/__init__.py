from pyspark import SparkContext
from pyspark.sql import SparkSession


def create_spark_session() -> SparkSession:
    return SparkSession.builder \
        .config('spark.driver.maxResultSize', '16g') \
        .config('spark.driver.memory', '16g') \
        .config('spark.executor.memory', '8g') \
        .config('spark.hadoop.hive.exec.dynamic.partition.mode', 'nonstrict') \
        .config('spark.hadoop.hive.exec.dynamic.partition', 'true') \
        .getOrCreate()


def create_context() -> SparkContext:
    return SparkContext("local", "Sum")
