import pandas as pd


def link_candidates():
    
    med = pd.read_csv("./out_files/med_data_deid_unique.csv")
    work = pd.read_csv("./out_files/work_data_smallgroups.csv")

    work["name"] = work["f_name"].astype(str) + " " + work["l_name"].astype(str)

    med["postal_code"] = med["postal_code"].astype(str)
    work["postal_code"] = work["postal_code"].astype(str)

    linked = med.merge(
        work[["postal_code", "gender", "name"]],
        on=["postal_code", "gender"],
        how="inner"
    )

    linked.to_csv("./out_files/linked_candidates.csv", index=False)
    print(f"Successfully linked {len(linked)} cadidates")

if __name__ == "__main__":
  link_candidates()