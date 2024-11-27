from pyspark.sql.functions import lit
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, TimestampType

from tests import get_spark_session, test_out_root

schema = StructType([
    StructField("name", StringType(), True),
    StructField("age", IntegerType(), True),
    StructField("city", StringType(), True)
])

data = [
    ("name", 30, "New York"),
    ("age", 25, "San Francisco"),
    ("city", 28, "Los Angeles")
]

csv_options = {
    "header": "true",
    "delimiter": ",",
    "quote": "\"",
    "nullValue": "",
    "emptyValue": "",
    "timestampFormat": "yyyy-MM-dd HH:mm:ss.SSSSSS",
    "quoteAll": False
}


def test_csv_write():
    sc = get_spark_session()
    df = sc.createDataFrame(data, schema)
    df = (
        df.withColumn("c1", lit(None).cast(StringType()))
        .withColumn("c2", lit("").cast(StringType()))
        .withColumn("c3", lit("\"").cast(StringType()))
        .withColumn("c4", lit(",").cast(StringType()))
        .withColumn("c5", lit("2024-11-07 12:00:00.000000").cast(TimestampType()))
    )

    df.show()

    df.coalesce(1).write.format("csv").mode("overwrite").options(**csv_options).save(f"{test_out_root}/test")


def test_csv_read():
    sc = get_spark_session()
    df = sc.read.options(**csv_options).csv(f"{test_out_root}/test")
    df.show()
