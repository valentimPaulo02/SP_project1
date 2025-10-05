import pandas as pd
from sqlalchemy import create_engine


engine = create_engine("postgresql://postgres:password@localhost:5432/pds_proj_1")

def get_med_deid_uniques():
    
    df = pd.read_sql("SELECT id, age, gender, postal_code FROM med_data_deid", engine)

    counts = df.groupby(['postal_code', 'gender']).size().reset_index(name='cnt')

    uniques = df.merge(counts[counts['cnt'] == 1][['postal_code','gender']], on=['postal_code','gender'])
    
    uniques.to_csv("./out_files/med_data_deid_unique.csv", index=False)
    print(f"Saved {len(uniques)} unique rows from meta_data_deid")

def get_work_smallgroups():

    df = pd.read_sql('SELECT id, postal_code, gender, f_name, l_name FROM work_data', engine)

    counts = df.groupby(['postal_code', 'gender']).size().reset_index(name='cnt')

    small = df.merge(counts[counts['cnt'] <= 3][['postal_code','gender']], on=['postal_code','gender'])
    
    small.to_csv("./out_files/work_data_smallgroups.csv", index=False)
    print(f"Saved {len(small)} rows from work_data")

if __name__ == "__main__":
    get_med_deid_uniques()
    get_work_smallgroups()