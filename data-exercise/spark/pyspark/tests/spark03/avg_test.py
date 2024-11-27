from testcontainers.mysql import MySqlContainer

from jobs.chapter1.section1.avg import basic_avg
from tests import get_spark_context


def test_avg():
    sc = get_spark_context()
    df = sc.parallelize([1, 2, 3])
    avg = basic_avg(df)
    assert avg == 2


def test_avg1():
    with MySqlContainer("mysql:8.3.0") as mysql:
        mysql_url = mysql.get_connection_url()
        print(mysql_url)
