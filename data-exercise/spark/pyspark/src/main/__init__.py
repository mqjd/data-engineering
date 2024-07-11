from pyspark import SparkContext
from pyspark.sql import SparkSession


def create_spark_session(name: str = "Spark Example") -> SparkSession:
    return SparkSession.builder \
        .appName(name) \
        .config('spark.driver.maxResultSize', '16g') \
        .config('spark.driver.memory', '16g') \
        .config('spark.executor.memory', '8g') \
        .config('spark.sql.sources.partitionOverwriteMode', 'dynamic') \
        .getOrCreate()


def create_context(name: str = "Spark Example") -> SparkContext:
    return SparkContext("local", name)
