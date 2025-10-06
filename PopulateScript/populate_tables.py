from sqlalchemy import create_engine, MetaData, Table, Column, Numeric, Integer, VARCHAR, text
from sqlalchemy.orm import scoped_session, sessionmaker

import csv
from random import randint, choice
from datetime import datetime, timedelta

def random_datetime(begin, end):
    delta = end-begin
    random_seconds = randint(1, int(delta.total_seconds()))
    return begin+timedelta(seconds=random_seconds)

def main():
    engine = create_engine("postgresql://postgres:password@localhost:5432/pds_proj_1")
    CONN = engine.connect()
    META_DATA = MetaData()
    META_DATA.reflect(bind=engine)
    MED_TABLE = META_DATA.tables["med_data"]
    WORK_TABLE = META_DATA.tables["work_data"]
    MED_TABLE_DEID = META_DATA.tables["med_data_deid"]

    with open("./Data/med.csv", 'r') as med_file:
        med_csv = list(csv.reader(med_file))

        for line in med_csv:
            row = MED_TABLE.insert().values(id=line[0], name=line[1], age=line[2],
                address=line[3], email=line[4], gender=line[5], postal_code=line[6],
                diagnosis=line[7])
            CONN.execute(row)
        CONN.commit()

        for line in med_csv:
            row = MED_TABLE_DEID.insert().values(age=line[2], gender=line[5], 
                postal_code=line[6], diagnosis=line[7])
            CONN.execute(row)
        CONN.commit()


    with open("./Data/work.csv", 'r') as work_file:
        work_csv = csv.reader(work_file)

        for line in work_csv:
            row = WORK_TABLE.insert().values(id=line[0], f_name=line[1],
                l_name=line[2], postal_code=line[3], gender=line[4],
                education=line[5], workplace=line[6], department=line[7])
            CONN.execute(row)
        CONN.commit()

if __name__ == '__main__':
    main()
