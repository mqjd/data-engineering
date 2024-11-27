import pytest

from tests import start_spark_session, stop_spark_session


@pytest.fixture(scope="session", autouse=True)
def setup_spark():
    start_spark_session()
    yield
    stop_spark_session()
