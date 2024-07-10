import sys

from pyspark import SparkContext


def basic_avg(numbers):
    """Compute the avg"""
    sum_count = numbers.map(lambda x: (x, 1)).fold(
        (0, 0), (lambda x, y: (x[0] + y[0], x[1] + y[1])))
    return sum_count[0] / float(sum_count[1])


if __name__ == "__main__":
    master = "local"
    if len(sys.argv) == 2:
        master = sys.argv[1]
    sc = SparkContext(master, "Sum")
    sc.setLogLevel("WARN")
    nums = sc.parallelize([1, 2, 3, 4])
    avg = basic_avg(nums)
    print(avg)
    sc.stop()
