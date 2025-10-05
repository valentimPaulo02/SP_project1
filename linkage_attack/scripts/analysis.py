import pandas as pd
from sqlalchemy import create_engine


engine = create_engine("postgresql://postgres:password@localhost:5432/pds_proj_1")

def analysis():

    med_orig = pd.read_sql("SELECT id, name FROM med_data", engine)
    linked = pd.read_csv("./out_files/linked_candidates.csv")

    med_orig["id"] = med_orig["id"].astype(int)
    linked["id"] = linked["id"].astype(int)

    eval_df = linked.merge(
        med_orig,
        on="id",
        how="left"
    )

    eval_df["match"] = eval_df["name_x"].astype(str) == eval_df["name_y"].astype(str)

    total_links = len(eval_df)
    correct_links = eval_df["match"].sum()
    total_med_rows = med_orig["id"].nunique()
    pct_correct = correct_links / total_links * 100 
    pct_med_reidentified = correct_links / total_med_rows * 100 

    eval_df.to_csv("./out_files/result.csv", index=False)

    print(f"Total linked rows: {total_links}")
    print(f"Correct matches: {correct_links} ({pct_correct:.1f}%)")
    print(f"Med rows re-identified: {correct_links} / {total_med_rows} ({pct_med_reidentified:.1f}%)")

if __name__ == "__main__":
    analysis()