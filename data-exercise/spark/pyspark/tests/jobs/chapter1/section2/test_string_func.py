from pyspark.sql.types import StructType, StructField, StringType, IntegerType
from pyspark.sql import functions as fn

from tests import get_spark_session

schema = StructType([
    StructField("name", StringType(), True),
    StructField("age", IntegerType(), True),
    StructField("city", StringType(), True)
])

data = [
    ("name", 30, "2024-04-01"),
    ("age", 25, "San Francisco"),
    ("city", 28, "Los Angeles")
]


def test_csv_write():
    sc = get_spark_session()
    df = sc.createDataFrame(data, schema)

    df.select(fn.regexp("city", fn.lit("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))).show()
