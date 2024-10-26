import sys

from jobs import create_context

sc = create_context()
sc.setLogLevel("INFO")
# noinspection PyProtectedMember
LoggerFactory = sc._jvm.org.slf4j.LoggerFactory
logger = LoggerFactory.getLogger("Section1-1")


def basic_avg(numbers):
    """Compute the avg"""
    sum_count = numbers.map(lambda x: (x, 1)).fold(
        (0, 0), (lambda x, y: (x[0] + y[0], x[1] + y[1])))
    return sum_count[0] / float(sum_count[1])


if __name__ == "__main__":
    master = "local"
    if len(sys.argv) == 2:
        master = sys.argv[1]
    nums = sc.parallelize([1, 2, 3, 4])
    avg = basic_avg(nums)
    logger.info("avg: {}", avg)
    sc.stop()
