from pathlib import Path

from pyspark import SparkContext
from pyspark.sql import SparkSession

project_root = Path(__file__).parent.parent.absolute()
test_out_root = f"{project_root}/out"


def create_spark_session() -> SparkSession:
    return SparkSession.builder \
        .config('spark.driver.maxResultSize', '1g') \
        .config('spark.driver.memory', '4g') \
        .config('spark.executor.memory', '2g') \
        .config('spark.hadoop.hive.exec.dynamic.partition.mode', 'nonstrict') \
        .config('spark.hadoop.hive.exec.dynamic.partition', 'true') \
        .getOrCreate()


def create_context() -> SparkContext:
    return SparkContext("local", "Sum")


_sc: SparkSession


def get_spark_context() -> SparkContext:
    global _sc
    return _sc.sparkContext


def get_spark_session() -> SparkSession:
    global _sc
    return _sc


def start_spark_session():
    global _sc
    _sc = create_spark_session()
    _sc.sparkContext.setLogLevel("INFO")


def stop_spark_session():
    global _sc
    _sc.stop()


__ALL__ = ["get_spark_session", "test_output_root", "start_spark_session", "stop_spark_session"]
