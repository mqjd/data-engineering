from __future__ import annotations

import pendulum
from airflow.decorators import dag, task


@dag(
    dag_id="first_dag",
    schedule=None,
    start_date=pendulum.datetime(2021, 1, 1, tz="UTC"),
    catchup=False,
    tags=["example"],
)
def first_dag():
    @task()
    def task1():
        desc = "this is first task"
        print("current: ", desc)
        return desc

    @task(multiple_outputs=True)
    def task2(last_task: str):
        desc = "this is second task"
        print("current: ", desc)
        print("last: ", last_task)
        return {"task3": desc}

    @task()
    def task3(last_task: str):
        desc = "this is third task"
        print("current: ", desc)
        print("last: ", last_task)
        return desc

    tas1_output = task1()
    tas2_output = task2(tas1_output)
    task3(tas2_output["task3"])


first_dag()
