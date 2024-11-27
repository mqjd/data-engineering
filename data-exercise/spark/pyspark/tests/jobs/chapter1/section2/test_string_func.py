from pyspark.sql.types import StructType, StructField, StringType, IntegerType

from tests import get_spark_session

schema = StructType([
    StructField("name", StringType(), True),
    StructField("age", IntegerType(), True),
    StructField("city", StringType(), True)
])

data = [
    ("name", 30, " \t  New York"),
    ("age", 25, "San Francisco"),
    ("city", 28, "Los Angeles")
]


def test_csv_write():
    sc = get_spark_session()
    df = sc.createDataFrame(data, schema)
    df.createOrReplaceTempView("source")
    rows = sc.sql("select trim(regexp_replace(city, '[\\s\\t]+', '')) from source").collect()
    print(rows)
