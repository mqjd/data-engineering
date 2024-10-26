import pytest
from testcontainers.mysql import MySqlContainer

from jobs.chapter1.section1.avg import basic_avg
from tests import create_context


def test_avg():
    sc = create_context()
    df = sc.parallelize([1, 2, 3])
    avg = basic_avg(df)
    assert avg == 2


def test_avg1():
    with MySqlContainer("mysql:8.3.0") as mysql:
        mysql_url = mysql.get_connection_url()
        print(mysql_url)


if __name__ == '__main__':
    pytest.main(["-v", "avg.py"])
